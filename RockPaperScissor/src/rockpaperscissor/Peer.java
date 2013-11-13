package rockpaperscissor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miikka
 */
public class Peer {
    private List<SocketAddress> playerServers;
    private List<PeerHandler> playerHandlers;
    private List<Gesture> currentChoices;
    private List<Integer> scores;
    private ServerSocket serverSocket;
    private Gesture myCurrentGesture;
    
    
    public Peer(ServerSocket socket) {
        serverSocket = socket;
        playerServers = new ArrayList();
        playerHandlers = new ArrayList();
        currentChoices = new ArrayList();
        scores = new ArrayList();
        myCurrentGesture = null;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    //addPlayer is called when the serversocket accepts new player connection
    public void addPlayer(PeerHandler peerHandler) {
        playerHandlers.add(peerHandler);
        currentChoices.add(null);
        scores.add(0);
    }
    
    public void connectToPeer(String otherPeerIp, int port) {
        try {
            Socket socket = new Socket (otherPeerIp, port);
//            addPlayer(peerHandler);
            //LIGG OCH LYSSNA ConnectBackRequest
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object returnMessage = null;
            SocketAddress localServerAddress = serverSocket.getLocalSocketAddress();
            
//            //Test creating peerhandler with streams
//            PeerHandler peerHandler = new PeerHandler(out,in,this);
//            Thread thread = new Thread(peerHandler);
//            thread.start();
            
            //Send local server address to the other peer so it can
            //connect to this peer and this peer creates a PeerHandler for it
//            Message msg = new Message("ServerSocketAddress", localServerAddress);
            Message msg = new Message("TextMessage", "HELLO FROM SPACE!");
            //write message to output stream
            out.writeObject (msg);
            out.flush();
            try {
                Object receivedMessage = null;
                while (receivedMessage == null) {
                    receivedMessage = in.readObject ();
                }
                Message receivedMsg = (Message) receivedMessage;
                System.out.println(receivedMsg.getMsgObj());
            } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);                
            }          
            
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void disconnect() {
        
    }
    
    public void deletePeer() {
    }
    
    public void playGesture(Gesture gesture) {
        for(PeerHandler peerHandler : playerHandlers) {
            peerHandler.sendGesture(gesture);
        }
        myCurrentGesture = gesture;
    }

    public void updateGameState(PeerHandler peerHandler, Gesture gesture) {
        int index = playerHandlers.indexOf(peerHandler);
        currentChoices.set(index, gesture);
        //Calculate score if all results are in
        if (!currentChoices.contains(null)) {
            calculateScore();
            //nullify gesture list when scores are calculated
            for(Gesture g : currentChoices) {
                g = null;
            }
        }
    }
    
    public void calculateScore() {
        int nrOfPlayers = currentChoices.size();
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
        }
        else if (rocks == 0 && scissors < nrOfPlayers) {
            //no rocks, scissors win over paper
            score = papers;
            for (int i : scissor) {
                scores.set(i, scores.get(i)+score);
            }
        }
        else if (scissors == 0 && papers < nrOfPlayers) {
            //no scissors, papers win over rocks
            score = rocks;
            for (int i : paper) {
                scores.set(i, scores.get(i)+score);
            }
        }        
    }
    
    public void testMessage(String msg) {
        for (PeerHandler peer : playerHandlers) {
            peer.sendTextMessage(msg);
        }
    }

    void handleTextMessage(String textMessage) {
        System.out.println("Text message received: \"" + textMessage + "\"");
    }
}
