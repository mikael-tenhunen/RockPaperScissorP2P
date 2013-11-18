
package rockpaperscissor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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
    InetSocketAddress serverSocketAddress;
    boolean sendMePeerList;
    
    //This is the constructor used when someone has called this peer's server-role.
    //It sends a message requesting serverSocketAddress, so that the other side
    //will send it and the message receiver can call addPlayer
    public PeerHandler(Socket peerSocket, Peer me) {
        this.peerSocket = peerSocket;
        this.me = me;
        sendMePeerList = true;
        try {
            System.out.println("Trying to initialize input- output streams in PeerHandler");          
//            out = new ObjectOutputStream(new BufferedOutputStream(peerSocket.getOutputStream()));
//            in = new ObjectInputStream(new BufferedInputStream(peerSocket.getInputStream()));     
            OutputStream outStream = peerSocket.getOutputStream();
            out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.flush();
            InputStream inStream = peerSocket.getInputStream();            
            in = new ObjectInputStream(new BufferedInputStream(inStream));
//            System.out.println("PeerHandler got input- and output streams.");
            sendServerSocketAddressRequest("ServerSocketAddressRequestFromListener");
        } catch (IOException iOException) {
            System.out.println("PeerHandler could not get input and/or output streams to peer.");
        }
    }

    //Constructor to be called when this peer has called someone else's server-
    //role. It also sends a serverSocketAddressRequest
    PeerHandler(ObjectOutputStream out, ObjectInputStream in, Peer me, boolean sendMePeerList) {
        this.me = me;
        this.sendMePeerList = sendMePeerList;
        this.out = out;
        try {
            out.flush();
        } catch (IOException ex) {
            System.out.println("problem creating output stream in "
                    + "connecter-constructor of PeerHandler");
        }
        this.in = in;
        peerSocket = null;
        sendServerSocketAddressRequest("ServerSocketAddressRequestFromConnecter");
    }
    
    // type can be
    // ServerSocketAddressRequestFromListener
    // ServerSocketAddressRequestFromConnecter
    public void sendServerSocketAddressRequest(String type) {
        try {
            Message msg = new Message(type,null);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
            System.out.println("Problem requesting server socket"
                    + " address from other peer");
        }
    }
    
    //Sends this peer's server-role socket address
    //type can be: 
    // ServerSocketAddressToListener
    // ServerSocketAddressToConnecter
    //
    // There is no type ServerSocketAddressToListenerNoList because this is
    // equivalent to what happens when ServerSocketAddressToConnecter is type
    // 
    //Also checks if this PeerHandler wants peerServerList from the remote peer.
    //If the remote peer had a listener-role in relation to this peer, we send
    //either a ServerSocketAddressToListener or a 
    //ServerSocketAddressToListenerNoList
    public void sendServerSocketAddress(String type) {
        try {
            if ((type == "ServerSocketAddressToListener") && !sendMePeerList) {
                //this is essentially setting type to
                //ServerSocketAddressToListenerNoList, but we don't want duplicate
                //cases in message receiver
                type = "ServerSocketAddressToConnecter";
            }
            InetSocketAddress serverToConnectTo = (InetSocketAddress) me.getServerSocket().getLocalSocketAddress();
            Message msg = new Message(type, serverToConnectTo);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
            System.out.println("Problem sending server socket address");
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
            System.out.println("Sent peerServerList");
        }
        catch (IOException iOException) {
            System.out.println("Problem sending peer server list");
        }
    }
    
    void requestScore() {
        try {
            Message msg = new Message("ScoreRequest",null);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
            System.out.println("Problem sending score request");
        }
    }
    
    void sendScore() {
        try {
            Message msg = new Message("Score", me.getScore());
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
            System.out.println("Problem sending score");
        }
    }    
    
    //This is needed for a new peer in the swarm to request the current choices
    //of already existing peers in the swarm (they might have made a choice 
    //this round when this peer connects)
    public void requestGesture() {
        try {
            Message msg = new Message("GestureRequest",null);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException iOException) {
            System.out.println("Problem sending gesture request");
        }        
    }
    
    //Sends game gesture
    public void sendGesture(Gesture gesture) {
//        System.out.println("Gesture about to be sent from PeerHandler...");
        try {
            Message msg = new Message("Gesture",gesture);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException IOException) {
            System.out.println("Problem sending gesture");
        }
        System.out.println("Gesture sent.");
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
            System.out.println("Problem sending text message");
        }        
    }
    
    void sendDisconnectNotification(InetSocketAddress socketAddress) {
        try {
            Message msg = new Message("DisconnectNotification",socketAddress);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException IOException) {
            System.out.println("Problem sending disconnect notification");
        }
    }
    
    public synchronized void receiveMessage() {
        Object returnMessage = null;
        try {
            while (returnMessage == null) {
                    returnMessage = in.readObject ();
            }
            Message msg = (Message) returnMessage;
            String type = msg.getType();
            
            switch(type) {
                case "ServerSocketAddressToListener":
                    InetSocketAddress serverConnecter = (InetSocketAddress) msg.getMsgObj();
                    serverSocketAddress = serverConnecter;
//                    System.out.println("serverSocketAddress received!");
                    sendPeerServerList();
                    me.addPlayer(this);
                    break;
//                case "ServerSocketAddressToListenerNoList":
//                    InetSocketAddress serverConnecter = (InetSocketAddress) msg.getMsgObj();
//                    serverSocketAddress = serverConnecter;
//                    sendPeerServerList();
//                    me.addPlayer(this);
//                    break;
                case "ServerSocketAddressToConnecter":
                    InetSocketAddress serverListener = (InetSocketAddress) msg.getMsgObj();
                    serverSocketAddress = serverListener;
                    me.addPlayer(this);
                    break;
                case "ServerSocketAddressRequestFromConnecter":
                    sendServerSocketAddress("ServerSocketAddressToConnecter");
                    break;
                case "ServerSocketAddressRequestFromListener":
                    sendServerSocketAddress("ServerSocketAddressToListener");
                    break;
                case "PeerServerList":
                    List<InetSocketAddress> serverSocketAddressList = (List<InetSocketAddress>) msg.getMsgObj();
                    me.handlePeerServerList(serverSocketAddressList);
                    break;
                case "ScoreRequest":
                    sendScore();
                    break;
                case "Score":
                    int score = (Integer) msg.getMsgObj();
                    me.handleScoreFromPeer(this, score);
                    break;
                case "GestureRequest":
                    sendGesture(me.getGesture());
                    break;
                case "Gesture":
                    Gesture gesture = (Gesture) msg.getMsgObj();
                    System.out.println("Gesture received...");
                    me.updateOpponentGesture(this, gesture);
                    break;
                case "TextMessage":
                    System.out.println("Received text message");
                    String textMessage = (String) msg.getMsgObj();
                    me.handleTextMessage(textMessage);
                    break;
                case "DisconnectNotification":
                    System.out.println("DisconnectNotification received.");
                    InetSocketAddress disconnectingPeerAddress = (InetSocketAddress) msg.getMsgObj();
                    me.disconnectOtherPeer(disconnectingPeerAddress);
                    break;     
            }
        }
        catch (Exception e) {
//            System.out.println("Problem encountered while receiving message");
            System.out.print(e);
            e.printStackTrace();
        }  
    }
    
    SocketAddress getServerSocketAddress() {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) serverSocketAddress;
        return inetSocketAddress;
    }
        
    @Override
    public void run() {
        System.out.println("PeerHandler run-method activated!");
        while(true) {
            receiveMessage();
        }
    }

    void closeAll() {
        try {
            in.close();
            out.close();
        } catch (IOException iOException) {
            System.out.println("Problem closing input- and output streams for"
                    + "peer handler socket");
        }
    }
}
