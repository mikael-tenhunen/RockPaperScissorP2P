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
 *
 * @author miikka
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
    
    
    public Peer(ServerSocket socket) {
        serverSocket = socket;
        playerServers = new ArrayList();
        playerHandlers = new ArrayList();
        currentChoices = new ArrayList();
        scores = new ArrayList();
        myScore = 0;
        myCurrentGesture = Gesture.UNKNOWN;
    }
    
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        showListsInGui();
    }
    
    public void startServerRole() {
        //start listening server
        serverRole = new ServerRole(this);
        Thread serverThread = new Thread(serverRole);
        serverThread.start();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    public synchronized List getPlayerServers() {
        return playerServers;
    }
    
    public synchronized int getScore() {
        return myScore;
    }
    
    //addPlayer is called when the serversocket accepts new player connection
    public synchronized void addPlayer(PeerHandler peerHandler) {
        playerHandlers.add(peerHandler);
        currentChoices.add(Gesture.UNKNOWN);
        scores.add(0);
        playerServers.add((InetSocketAddress) peerHandler.getServerSocketAddress());
        //Now request the score of this player
        peerHandler.requestScore();
        System.out.println("Added " + peerHandler.getServerSocketAddress() + " tp playerList");
        System.out.println("Now my playerServers contains " + playerServers);
        showPlayerServersInGui();
//        showScoresInGui();
    }
    
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
//        requestOthersScores();
    }
    
    //This is called when connected to a new swarm, so that the scores of the
    //other peers in the swarm are known by this peer
    public synchronized void requestOthersScores() {
        PeerHandler peerHandler;
        for (int i = 0; i < playerHandlers.size(); i++) {
            peerHandler = playerHandlers.get(i);
            System.out.println("Requesting score from " + playerServers.get(i));
            peerHandler.requestScore();
        }
    }
    
    public synchronized void handleScoreFromPeer(PeerHandler peerHandler, int score) {
        int index = playerHandlers.indexOf(peerHandler);
        System.out.println("Received score from peer " + playerServers.get(index));
        System.out.println("The score is: " + score);
        scores.set(index, score);
        showScoresInGui();
    }
    
    //sendMePeerList is a flag that shows if we are interested in getting the peerlist from 
    //the remote peer or not. If we are connecting to all peers in an already received
    //peerServerList, we are not interested in getting new lists.
    public synchronized void connectToPeer(String otherPeerIp, int port, boolean sendMePeerList) {
        try {
            Socket socket = new Socket (otherPeerIp, port);
//            addPlayer(peerHandler);
            OutputStream outStream = socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.flush();
            InputStream inStream = socket.getInputStream();
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inStream)); 
//            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));            
//            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));    
            //Create peerhandler with streams, and start its thread
            PeerHandler peerHandler = new PeerHandler(out,in,this,sendMePeerList);
            serverRole.getExecutor().execute(peerHandler);           
        } catch (IOException ex) {
            System.out.println("Problem connecting to peer at: " 
                    + otherPeerIp + ":" + port);
        }
    }
    
    public synchronized void disconnectMe() {
        for (PeerHandler ph : playerHandlers) {
            ph.sendDisconnectNotification((InetSocketAddress)serverSocket.getLocalSocketAddress());
            ph.closeAll();
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.out.println("Problem closing server socket");
            }
        }
    }
    
    public synchronized void disconnectOtherPeer(InetSocketAddress disconnectingPeerAddress) {
        int index = playerServers.indexOf(disconnectingPeerAddress);
        playerServers.remove(index);
        playerHandlers.remove(index);
        currentChoices.remove(index);
        scores.remove(index);
        System.out.println("Removed peer: " + disconnectingPeerAddress);
        updateGameState();
        showPlayerServersInGui();
    }
    
    public synchronized void playGesture(Gesture gesture) {
        myCurrentGesture = gesture;
        for(PeerHandler peerHandler : playerHandlers) {
            peerHandler.sendGesture(gesture);
        }
        updateGameState();
    }
    
    public synchronized void updateOpponentGesture(PeerHandler peerHandler, Gesture gesture) {
        int index = playerHandlers.indexOf(peerHandler);
        currentChoices.set(index, gesture);
        updateGameState();
    }

    public synchronized void updateGameState() {
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
//            System.out.println("My score: " + myScore);
//            for (int i = 0; i < scores.size(); i++) {
//                System.out.println("Score for player " + i
//                    + ": " + scores.get(i));
//            }
        }
    }
    
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
        
    public synchronized void testMessage(String msg) {
        for (PeerHandler peer : playerHandlers) {
            peer.sendTextMessage(msg);
        }
    }

    void handleTextMessage(String textMessage) {
        System.out.println("Text message received: \"" + textMessage + "\"");
    }

    private void gestureSendAllowedInGui() {
        mainWindow.allowGestureSend();
    }
        
    public void showGesturesInGui() {
        List<Gesture> allGestures = new ArrayList(currentChoices);
        allGestures.add(0,myCurrentGesture);
        mainWindow.updateGestures(allGestures);
    }

    public void showScoresInGui() {
        List<Integer> allScores = new ArrayList(scores);
        allScores.add(0,myScore);
        mainWindow.updateScores(allScores);
    }
    
    public void showPlayerServersInGui() {
        List<InetSocketAddress> allPlayerServers = new ArrayList(playerServers);
        allPlayerServers.add(0,(InetSocketAddress)serverSocket.getLocalSocketAddress());
        mainWindow.updatePlayers(allPlayerServers);
    }    
    
    public void showListsInGui() {    
        showGesturesInGui();
        showScoresInGui();
        showPlayerServersInGui();
    }        
}
