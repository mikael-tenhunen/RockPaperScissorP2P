package rockpaperscissor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private List<InetSocketAddress> playerServers;
    private List<PeerHandler> playerHandlers;
    private List<Gesture> currentChoices;
    private List<Integer> scores;
    private ServerSocket serverSocket;
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
    
    //addPlayer is called when the serversocket accepts new player connection
    public synchronized void addPlayer(PeerHandler peerHandler) {
        playerHandlers.add(peerHandler);
        currentChoices.add(Gesture.UNKNOWN);
        scores.add(0);
        playerServers.add((InetSocketAddress) peerHandler.getServerSocketAddress());
        System.out.println("Added " + peerHandler.getServerSocketAddress() + " tp playerList");
        System.out.println("Now my playerServers contains " + playerServers);
        showPlayerServersInGui();
        showScoresInGui();
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
    }
    
    //sendMePeerList is a flag that shows if we are interested in getting the peerlist from 
    //the remote peer or not. If we are connecting to all peers in an already received
    //peerServerList, we are not interested in getting new lists.
    public synchronized void connectToPeer(String otherPeerIp, int port, boolean sendMePeerList) {
        try {
            Socket socket = new Socket (otherPeerIp, port);
//            addPlayer(peerHandler);
            //LIGG OCH LYSSNA ConnectBackRequest
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());            
            //Create peerhandler with streams, and start its thread
            PeerHandler peerHandler = new PeerHandler(out,in,this,sendMePeerList);
            serverRole.getExecutor().execute(peerHandler);           
        } catch (IOException ex) {
            System.out.println("Problem connecting to server at: " 
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
        showPlayerServersInGui();
        showScoresInGui();
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
            showScoresInGui();
            gestureSendAllowedInGui();
            System.out.println("My score: " + myScore);
            for (int i = 0; i < scores.size(); i++) {
                System.out.println("Score for player " + i
                    + ": " + scores.get(i));
            }
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
