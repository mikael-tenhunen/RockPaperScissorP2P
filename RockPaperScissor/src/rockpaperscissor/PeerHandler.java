
package rockpaperscissor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

/**
 * PeerHandler listens for incoming gestures.
 */
class PeerHandler implements Runnable {
    Socket peerSocket;
    Peer me;
    ObjectInputStream in;
    ObjectOutputStream out;
    SocketAddress serverSocketAddress;
    
    //This is the constructor used when someone has called this peer's server-role.
    //It sends a message requesting serverSocketAddress, so that the other side
    //will send it and the message receiver can call addPlayer
    public PeerHandler(Socket peerSocket, Peer me) {
        this.peerSocket = peerSocket;
        this.me = me;
        try {
            System.out.println("Trying to initialize input- output streams in PeerHandler");
            out = new ObjectOutputStream(peerSocket.getOutputStream());
            in = new ObjectInputStream(peerSocket.getInputStream());
//            System.out.println("PeerHandler got input- and output streams.");
            sendServerSocketAddressRequest();
        } catch (IOException iOException) {
            System.out.println("PeerHandler could not get input and/or output streams to peer.");
        }
    }

    //Constructor to be called when this peer has called someone else's server-
    //role. It also sends a serverSocketAddressRequest
    PeerHandler(ObjectOutputStream out, ObjectInputStream in, Peer me) {
        this.in = in;
        this.out = out;
        peerSocket = null;
        this.me = me;
        sendServerSocketAddressRequest();
    }
    
    public void sendServerSocketAddressRequest() {
        try {
            Message msg = new Message("ServerSocketAddressRequest",null);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
        }
    }
    
    //Sends this peer's server-role socket address
    public void sendServerSocketAddress() {
        try {
            SocketAddress serverToConnectTo = me.getServerSocket().getLocalSocketAddress();
            Message msg = new Message("ServerSocketAddress",serverToConnectTo);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
        }
    }

    //Sends this peer's list of peer server socket addresses, so that a peer
    //joining the network can establish connections with all other peers
    public void sendPeerServerList() {
        try {
            System.out.println("Now in sendPeerServerList in PeerHandler...");
            Message msg = new Message("PeerServerList",me.getPlayerServers());
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
        }
    }
    
    //Sends game gesture
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
                    SocketAddress serverToConnectTo = (SocketAddress) msg.getMsgObj();
                    serverSocketAddress = serverToConnectTo;
                    System.out.println("serverSocketAddress received!");
                    //trying sendPeerServerList
                    sendPeerServerList();
                    me.addPlayer(this);
//                    //connect to the other peer so it creates a 
//                    //PeerHandler for this peer
//                    String serverIp = serverToConnectTo.getHostString();
//                    int serverPort = serverToConnectTo.getPort();
//                    //Socket socket = new Socket (serverIp, serverPort);
//                    peerSocket = new Socket (serverIp, serverPort);
//                    System.out.println("Trying to initialize input- output streams in receiveMessage in PeerHandler");
//                    out = new ObjectOutputStream(peerSocket.getOutputStream());
//                    in = new ObjectInputStream(peerSocket.getInputStream());
//                    System.out.println("receiveMessage input- output streams initialized!");
//                    System.out.println("peerhandler's local socket at port: " + peerSocket.getLocalSocketAddress());
//                    System.out.println("peerhandler's remote socket at port: " + peerSocket.getRemoteSocketAddress());
                    break;
                case "ServerSocketAddressRequest":
                    sendServerSocketAddress();
                    break;
                case "PeerServerList":
                    List<SocketAddress> serverSocketAddressList = (List<SocketAddress>) msg.getMsgObj();
                    me.handlePeerServerList(serverSocketAddressList);
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
    
    SocketAddress getServerSocketAddress() {
        return serverSocketAddress;
    }
        
    @Override
    public void run() {
        System.out.println("PeerHandler run-method activated!");
        while(true) {
            receiveMessage();
        }
    }
}
