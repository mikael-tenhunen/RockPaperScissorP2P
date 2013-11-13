
package rockpaperscissor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * PeerHandler listens for incoming gestures.
 */
class PeerHandler implements Runnable {
    Socket peerSocket;
    Peer me;
    ObjectInputStream in;
    ObjectOutputStream out;
    

    public PeerHandler(Socket peerSocket, Peer me) {
        this.peerSocket = peerSocket;
        this.me = me;
        try {
            System.out.println("Trying to initialize input- output streams in PeerHandler");
            out = new ObjectOutputStream(peerSocket.getOutputStream());
            in = new ObjectInputStream(peerSocket.getInputStream());
//            System.out.println("PeerHandler got input- and output streams.");
        } catch (IOException iOException) {
            System.out.println("PeerHandler could not get input and/or output streams to peer.");
        }
        me.addPlayer(this);
    }

    //Constructor to be called when we have 
    PeerHandler(ObjectOutputStream out, ObjectInputStream in, Peer me) {
        this.in = in;
        this.out = out;
        peerSocket = null;
        this.me = me;
        
        me.addPlayer(this);
    }

    public void sendGesture(Gesture gesture) {
        try {
            Message msg = new Message("Gesture",gesture);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
        }
    }
    
    public void sendTextMessage(String textMessage) {
        try {
            //System.out.println("Sending text message to: " + peerSocket.getRemoteSocketAddress());
            System.out.println("Sending text message");
            Message msg = new Message("TextMessage",textMessage);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
        }        
    }
    
    public void receiveMessage() {
        Object returnMessage = null;
        try {
            while (returnMessage == null) {
                    returnMessage = in.readObject ();
            }
            Message msg = (Message) returnMessage;
            String type = msg.getType();
            
            switch(type) {
                case "ServerSocketAddress":
                    InetSocketAddress serverToConnectTo = (InetSocketAddress) msg.getMsgObj();
                    //connect to the other peer so it creates a 
                    //PeerHandler for this peer
                    String serverIp = serverToConnectTo.getHostString();
                    int serverPort = serverToConnectTo.getPort();
                    //Socket socket = new Socket (serverIp, serverPort);
                    peerSocket = new Socket (serverIp, serverPort);
                    System.out.println("Trying to initialize input- output streams in receiveMessage in PeerHandler");
                    out = new ObjectOutputStream(peerSocket.getOutputStream());
                    in = new ObjectInputStream(peerSocket.getInputStream());
                    System.out.println("receiveMessage input- output streams initialized!");
                    System.out.println("peerhandler's local socket at port: " + peerSocket.getLocalSocketAddress());
                    System.out.println("peerhandler's remote socket at port: " + peerSocket.getRemoteSocketAddress());
                    break;
                case "Gesture":
                    Gesture gesture = (Gesture) msg.getMsgObj();
                    me.updateGameState(this,gesture);
                    break;
                case "TextMessage":
                    System.out.println("Received text message");
                    String textMessage = (String) msg.getMsgObj();
                    me.handleTextMessage(textMessage);
                    //TEST1
//                    Message returnmessage = new Message("TextMessage","GREETINGS FROM THE STARS");
//                    out.writeObject(returnmessage);
//                    out.flush();
                    //TEST1
                    //TEST2
//                    sendTextMessage("WE COME IN PEACE");
                    //TEST2
                    break;
                    
            }
        }
        catch (Exception e) {
        }  
    }
        
    @Override
    public void run() {
        System.out.println("PeerHandler run-method activated!");
        while(true) {
            receiveMessage();
        }
    }
}
