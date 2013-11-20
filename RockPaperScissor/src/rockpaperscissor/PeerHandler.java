
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
 * PeerHandler represents the interface between two peers. It listens for 
 * incoming messages and sends messages.
 * 
 */
class PeerHandler implements Runnable {
    Peer me;
    ObjectInputStream in;
    ObjectOutputStream out;
    InetSocketAddress serverSocketAddress;
    boolean sendMePeerList;
    boolean keepReceiving;
    
    /**
     * This is the constructor used when someone has called this peer's server-
     * role (listener constructor).
     * It sends a message requesting serverSocketAddress, so that the other side
     * will send it and the message receiver can call addPlayer.
     * @param peerSocket the socket used to communicate with remote peer
     * @param me Peer object representing local peer
     */
    public PeerHandler(Socket peerSocket, Peer me) {
        this.me = me;
        sendMePeerList = true;
        keepReceiving = true;
        try {
            System.out.println("Trying to initialize input- output streams in PeerHandler");           
            OutputStream outStream = peerSocket.getOutputStream();
            out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.flush();
            InputStream inStream = peerSocket.getInputStream();            
            in = new ObjectInputStream(new BufferedInputStream(inStream));
            sendServerSocketAddressRequest("ServerSocketAddressRequestFromListener");
        } catch (IOException iOException) {
            System.out.println("PeerHandler could not get input and/or output streams to peer.");
        }
    }

    /**
     * Constructor to be called when this peer has called someone else's server-
     * role (connecter constructor). It also sends a serverSocketAddressRequest
     * 
     * @param out OutputStrem to remote peer's PeerHandler
     * @param in InputStream
     * @param me Peer object representing local peer
     * @param sendMePeerList flag that show whether a list of peers in the swarm
     * should be requested or not. It is requested when this peer is
     * connecting to another peer, but not when this peer is connecting to another
     * peer as a result of already having received the list of peers.
     */
    PeerHandler(ObjectOutputStream out, ObjectInputStream in, Peer me, boolean sendMePeerList) {
        this.me = me;
        this.sendMePeerList = sendMePeerList;
        keepReceiving = true;
        this.out = out;
        try {
            out.flush();
        } catch (IOException ex) {
            System.out.println("problem creating output stream in "
                    + "connecter-constructor of PeerHandler");
        }
        this.in = in;
        sendServerSocketAddressRequest("ServerSocketAddressRequestFromConnecter");
    }
    
    /**
     * Sends a request for remote peer's server socket address.
     * 
     * @param type can be either "ServerSocketAddressRequestFromListener" or
     * "ServerSocketAddressRequestFromConnecter" depending on which constructor
     * calls this method.
     */
    private void sendServerSocketAddressRequest(String type) {
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
    
    /**
     * Sends this peer's server-role socket address.
     * 
     * Also checks if this PeerHandler wants peerServerList from the remote peer.
     * If the remote peer had a listener-role in relation to this peer, we send
     * either a ServerSocketAddressToListener or a ServerSocketAddressToListenerNoList
     * 
     * @param type can be "ServerSocketAddressToListener" or
     * "ServerSocketAddressToConnecter". There is no type 
     * ServerSocketAddressToListenerNoList because this is equivalent to what 
     * happens when ServerSocketAddressToConnecter is type
     */
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

    /**
     * Sends this peer's list of peer server socket addresses, so that a peer
     * joining the network can establish connections with all other peers.
     */
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
    
    /**
     * Sends a request for current score of remote peer.
     */
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
    
    /**
     * Sends current score of this peer to remote peer.
     */
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

    /**
     * This is needed for a new peer in the swarm to request the current choices 
     * of already existing peers in the swarm (they might have made a choice 
     * this round when this peer connects).
     */
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

    /**
     * @param gesture Gesture to be sent
     */
    public void sendGesture(Gesture gesture) {
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
    
    /**
     * 
     * @param textMessage text to be sent to remote peer
     */
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
    
    /**
     * This is what needs to be done when the local peer wants to disconnect.
     * 
     * @param socketAddress the address of the listening socket of this peer
     * (used by remote peers as an identifier).
     */
    void sendDisconnectNotification(InetSocketAddress socketAddress) {
        keepReceiving = false;
        try {
            Message msg = new Message("DisconnectNotification",socketAddress);
            out.writeObject(msg);
            out.flush();
        }
        catch (IOException IOException) {
            System.out.println("Problem sending disconnect notification");
        }
    }
    
    /**
     * reveiveMessage receives a message from the remote peer and reacts
     * accordingly.
     * 
     * For a list of all kinds of message that can be received, see
     * rockpaperscissor.Message
     */
    public synchronized void receiveMessage() {
        Object returnMessage = null;
        try {
            returnMessage = in.readObject();
            
            Message msg = (Message) returnMessage;
            String type = msg.getType();
            
            switch(type) {
                case "ServerSocketAddressToListener":
                    InetSocketAddress serverConnecter = (InetSocketAddress) msg.getMsgObj();
                    serverSocketAddress = serverConnecter;
                    sendPeerServerList();
                    me.addPlayer(this);
                    break;
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
        catch (java.io.EOFException eofException) {
            keepReceiving = false;
            closeAll();
        }
        catch (Exception e) {
//            System.out.println("Problem encountered while receiving message");
            System.out.println(e);
            e.printStackTrace();
        }  
    }
    
    /**
     * @return address to this peer's listening socket (used as identifier).
     */
    SocketAddress getServerSocketAddress() {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) serverSocketAddress;
        return inetSocketAddress;
    }
        
    /**
     * Tight loop receiving messages
     */
    @Override
    public void run() {
        System.out.println("PeerHandler run-method activated!");
        while(keepReceiving) {
            receiveMessage();
        }
        closeAll();
        me.closeListener();
        System.out.println("Succesfully disconnected");
    }

    /**
     * close in- and output streams.
     */
    void closeAll() {
        try {
            out.close();
            in.close();
        } catch (IOException iOException) {
            System.out.println("Problem closing input- and output streams for"
                    + "peer handler socket");
        }
    }
}
