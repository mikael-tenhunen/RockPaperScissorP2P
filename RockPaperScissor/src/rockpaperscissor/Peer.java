package rockpaperscissor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import userinterface.MainWindow;

/**
 * This class represents a peer and is a kind of a controller for communication
 * with interface to other peers (PeerHandler objects) and GUI.
 * 
 * Remote peers are identified by either the InetSocketAddress of their listening
 * ServerSockets, or their PeerHandler. InetSocketAddresses, PeerHandlers, scores 
 * and current choices of gestures are kept in lists with consistent index 
 * numbers. This means that remote peers can also be identified by index numbers.
 */
public class Peer {
    private final List<InetSocketAddress> playerServers;
    private final List<PeerHandler> playerHandlers;
    private final List<Gesture> currentChoices;
    private final List<Integer> scores;
    private final ServerSocket serverSocket;
    private Gesture myCurrentGesture;
    private ServerRole serverRole;
    private int myScore;
    private MainWindow mainWindow;
    
    
    /**
     * Constructor for Peer.
     * 
     * @param socket the ServerSocket that is listening for incoming connections.
     */
    public Peer(ServerSocket socket) {
        serverSocket = socket;
        playerServers = new ArrayList();
        playerHandlers = new ArrayList();
        currentChoices = new ArrayList();
        scores = new ArrayList();
        myScore = 0;
        myCurrentGesture = Gesture.UNKNOWN;
    }
    
    /**
     * @param mainWindow The main game GUI-component that Peer needs for 
     * communicating with the GUI.
     */
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        showListsInGui();
    }
    
    /**
     * The method startServerRole starts a new server thread (that listens to
     * incoming connections).
     */
    public void startServerRole() {
        //start listening server
        serverRole = new ServerRole(this);
        Thread serverThread = new Thread(serverRole);
        serverThread.start();
    }

    /**
     * This method just returns the ServerSocket of a peer.
     * @return serverSocket the ServerSocket that listens for incoming connections.
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    /**
     * @return the list containing all the players except this one
     */
    public synchronized List getPlayerServers() {
        return playerServers;
    }
    
    /**
     * @return this peer's own score
     */
    public synchronized int getScore() {
        return myScore;
    }
    
    /**
     *
     * @return this peer's current choice of gesture (is Gesture.UNKNOWN if no 
     * choice has been made yet).
     */
    public synchronized Gesture getGesture() {
        return myCurrentGesture;
    }
    
    //
    /**
     * This method adds information representing a newly connected player to the 
     * fields of this Peer object.
     * @param peerHandler the PeerHandler object that represents the interface
     * to the peer we are about to add as a new player.
     */
    public synchronized void addPlayer(PeerHandler peerHandler) {
        playerHandlers.add(peerHandler);
        currentChoices.add(Gesture.UNKNOWN);
        scores.add(0);
        playerServers.add((InetSocketAddress) peerHandler.getServerSocketAddress());
        //Now request the score and current gesture of this player
        peerHandler.requestScore();
        peerHandler.requestGesture();
        System.out.println("Added " + peerHandler.getServerSocketAddress() + 
                " tp playerList");
        System.out.println("Now my playerServers contains " + playerServers);
        showPlayerServersInGui();
//        showScoresInGui();
    }
    
    /**
     * handlePeerServerList gets a list of addresses to ServerSockets listening
     * to incoming connections at other peers. Called by PeerHandler.
     * This method is called when you want to connect yourself to all the other peers
     * that exist in a list of all the current players. It connects you to everyone that
     * is not currently in the list playerServers.
     * @param serverSocketAddresses list of other players listening sockets.
     */
    public synchronized void handlePeerServerList(List<InetSocketAddress> serverSocketAddresses) {
        String otherPeerIp;
        int port;
        //Connect to all the other (until now unknown) peers 
        for (InetSocketAddress socketAddress : serverSocketAddresses) {
            if (playerServers.contains(socketAddress)==false 
                    && !socketAddress.equals((InetSocketAddress)serverSocket.getLocalSocketAddress())) {
                otherPeerIp = socketAddress.getHostString();
                port = socketAddress.getPort();
                //sendMePeerList-flag is set to false
                connectToPeer(otherPeerIp,port,false);
            }
        }
        System.out.println("Now my playerServers contains " + playerServers);
    }
    
    
    /**
     * This method updates the score of another player.
     * It is called by a PeerHandler when it receives the current score of
     * another player. 
     * 
     * When a new swarm is joined, all the peers we connect to are sent a request
     * for their current score.
     * 
     * @param peerHandler
     * @param score
     */
    public void handleScoreFromPeer(PeerHandler peerHandler, int score) {
        int index = playerHandlers.indexOf(peerHandler);
        System.out.println("Received score from peer " + playerServers.get(index));
        System.out.println("The score is: " + score);
        scores.set(index, score);
        showScoresInGui();
    }

    /**
     * connectToPeer is called when this Peer should connect to a remote peer.
     * 
     * It creates a new PeerHandler for the remote Peer by using PeerHandler's 
     * second constructor (connecter constructor, to which we pass input and
     * output streams).
     *
     * @param otherPeerIp IP-address of listening ServerSocket to connect to at
     * remote peer.
     * @param port port number of listening erverSocket to connect to at
     * remote peer.
     * @param sendMePeerList a flag that shows if we are interested in getting 
     * the list of peers in swarm from the remote peer or not. If we are connecting 
     * to all peers in an already received peerServerList, we are not interested 
     * in getting new lists.
     */
    public synchronized void connectToPeer(String otherPeerIp, int port, boolean sendMePeerList) {
        try {
            Socket socket = new Socket (otherPeerIp, port);
            OutputStream outStream = socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.flush();
            InputStream inStream = socket.getInputStream();
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inStream));  
            //Create peerhandler with streams, and start its thread
            PeerHandler peerHandler = new PeerHandler(out,in,this,sendMePeerList);
            serverRole.getExecutor().execute(peerHandler);           
        } catch (IOException ex) {
            System.out.println("Problem connecting to peer at: " 
                    + otherPeerIp + ":" + port);
        }
    }
    
    /**
     * disconnectMe is called when this Peer should disconnect from the swarm it
     * is in.
     * 
     * It sends disconnect notifications to all other peers in swarm (so that
     * they call their disconnectOtherPeer method).
     *
     */
    public synchronized void disconnectMe() {
        if (playerHandlers.size() == 0) {
            closeListener();
        }
        else {
            for (PeerHandler ph : playerHandlers) {
                ph.sendDisconnectNotification((InetSocketAddress)serverSocket.getLocalSocketAddress());
                //ph.closeAll();
            }
        }
    }
    
    /**
     * closeListener is called to close the Server Socket that listens for 
     * incoming connections from other peers.
     */
    public synchronized void closeListener() {
        System.out.println("Trying to close listener...");
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Problem closing server socket");
        }        
    }
    
    /**
     * disconectOtherPeer is called by PeerHandler when it receives a disconnect
     * notification. It removes all the information representing a peer from the
     * lists.
     *
     * @param disconnectingPeerAddress address to the listening socket of the
     * peer that wants to disconnect.
     */
    public synchronized void disconnectOtherPeer(InetSocketAddress disconnectingPeerAddress) {
        int index = playerServers.indexOf(disconnectingPeerAddress);
        playerServers.remove(index);
        playerHandlers.remove(index);
        currentChoices.remove(index);
        scores.remove(index);
        System.out.println("Removed peer: " + disconnectingPeerAddress);
        updateGameState();
        showPlayerServersInGui();
        showScoresInGui();
//        removeGestureFromGui(index);
    }
    
    /**
     * playGesture is called from the GUI when a user wants to play a gesture.
     * 
     * @param gesture Gesture to play
     */
    public synchronized void playGesture(Gesture gesture) {
        myCurrentGesture = gesture;
        for(PeerHandler peerHandler : playerHandlers) {
            peerHandler.sendGesture(gesture);
        }
        updateGameState();
    }
    
    /**
     * updateOpponentGesture is called by PeerHandler when it receives a Gesture
     * from a remote peer.
     *
     * @param peerHandler used to 
     * @param gesture
     */
    public synchronized void updateOpponentGesture(PeerHandler peerHandler, Gesture gesture) {
        int index = playerHandlers.indexOf(peerHandler);
        currentChoices.set(index, gesture);
        updateGameState();
    }

    /**
     * updateGameState checks if everyone in the swarm has picked a gesture, and
     * in that case calculates the score for this round, sets the current Gesture
     * to UNKNOWN and re-enables the send button in GUI.
     */
    public void updateGameState() {
        //Calculate score if all results are in
        if ((!currentChoices.contains(Gesture.UNKNOWN)) 
                && (myCurrentGesture != Gesture.UNKNOWN)) {
            calculateScore();
            showGesturesInGui();
            //nullify gesture list when scores are calculated
            for(int i = 0; i < currentChoices.size(); i++) {
                currentChoices.set(i, Gesture.UNKNOWN);
            }
            myCurrentGesture = Gesture.UNKNOWN;
            showScoresInGui();
            gestureSendAllowedInGui();
        }
    }
    
    /**
     * 
     */
    public synchronized void calculateScore() {
        int nrOfPlayers = currentChoices.size() + 1;
        int papers = 0;
        int rocks = 0;
        int scissors = 0;
        List<Integer> paper = new ArrayList();
        List<Integer> rock = new ArrayList();
        List<Integer> scissor = new ArrayList();        
        Gesture g;
        //Now we count numbers of gestures and put into a List which players
        //(identified by index) chose which gesture
        for(int i = 0; i < currentChoices.size(); i++) {
            g = currentChoices.get(i);
            if (g == Gesture.PAPER) {
                papers++;
                paper.add(i);
            }
            if (g == Gesture.ROCK) {
                rocks++;
                rock.add(i);
            }   
            if (g == Gesture.SCISSOR) {
                scissors++;
                scissor.add(i);
            }            
        }
        
        //Also check for my own score
        if (myCurrentGesture == Gesture.PAPER) {
            papers++;
        }
        else if (myCurrentGesture == Gesture.ROCK) {
            rocks++;
        }
        else if (myCurrentGesture == Gesture.SCISSOR) {
            scissors++;
        }
        
        int score;
        if (papers > 0 && rocks > 0 && scissors > 0) {
            //each of the gestures are chosen, no winners
        }
        else if (papers == 0 && rocks < nrOfPlayers) {
            //no papers, rocks win over scissors
            score = scissors;
            for (int i : rock) {
                scores.set(i, scores.get(i)+score);
            }
            if (myCurrentGesture == Gesture.ROCK) {
                myScore += score;
            }
        }
        else if (rocks == 0 && scissors < nrOfPlayers) {
            //no rocks, scissors win over paper
            score = papers;
            for (int i : scissor) {
                scores.set(i, scores.get(i)+score);
            }
            if (myCurrentGesture == Gesture.SCISSOR) {
                myScore += score;
            }
        }
        else if (scissors == 0 && papers < nrOfPlayers) {
            //no scissors, papers win over rocks
            score = rocks;
            for (int i : paper) {
                scores.set(i, scores.get(i)+score);
            }
            if (myCurrentGesture == Gesture.PAPER) {
                myScore += score;
            }            
        }        
    }
        
    /**
     * textMessage is used to send a text message to all the other peers in swarm.
     * 
     * @param msg text to be sent
     */
    public synchronized void textMessage(String msg) {
        for (PeerHandler peer : playerHandlers) {
            peer.sendTextMessage(msg);
        }
    }

    /**
     * handleTextMessage is called by PeerHandler when it receives a text message.
     * @param textMessage received text message
     */
    public void handleTextMessage(String textMessage) {
        System.out.println("Text message received: \"" + textMessage + "\"");
    }

    /**
     * gestureSendAllowedInGui enables the send button in GUI
     */
    private void gestureSendAllowedInGui() {
        mainWindow.allowGestureSend();
    }
    
//    public void removeGestureFromGui(int index) {
//        mainWindow.removeGestureAt(index);
//    }
        
    /**
     *
     */
    public void showGesturesInGui() {
        List<Gesture> allGestures = new ArrayList(currentChoices);
        allGestures.add(0,myCurrentGesture);
        mainWindow.updateGestures(allGestures);
    }

    /**
     *
     */
    public void showScoresInGui() {
        List<Integer> allScores = new ArrayList(scores);
        allScores.add(0,myScore);
        try {
            mainWindow.updateScores(allScores);
        }
        catch (NullPointerException e) {
            System.out.println("nullpointer exception caught in"
                    + " showScoresInGui");
        }
    }
    
    /**
     *
     */
    public void showPlayerServersInGui() {    
        List<InetSocketAddress> allPlayerServers = new ArrayList(playerServers);
        allPlayerServers.add(0,(InetSocketAddress)serverSocket.getLocalSocketAddress());
        try {
            mainWindow.updatePlayers(allPlayerServers);
        }
        catch (NullPointerException e) {
            System.out.println("nullpointer exception caught in"
                    + " showPlayerServersInGui");
        }
    }    
    
    /**
     *
     */
    public void showListsInGui() {    
        showGesturesInGui();
        showScoresInGui();
        showPlayerServersInGui();
    }        
}
