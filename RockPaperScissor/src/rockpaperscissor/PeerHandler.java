
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
    }
    
    public void sendConnectBackRequest() {
        // Make a message object for this
        try {
            String msg = "ConnectBackRequest";
            out.writeObject (msg + "\n");
            
            Object returnMessage = null;
            try {
                while (returnMessage == null) {
                        returnMessage = in.readObject ();
                }
                
            }
            catch (Exception e) {
            }            
        } catch (IOException iOException) {
        }          
    }
    
    public void sendAck() {
        // Make a message object for this
        try {
            String msg = "Ack";
            out.writeObject(msg + "\n");
            
            Object returnMessage = null;
            try {
                while (returnMessage == null) {
                        returnMessage = in.readObject ();
                }
            }
            catch (Exception e) {
            }            
        } catch (IOException iOException) {
        }          
    }

    public void sendGesture(Gesture gesture) {
        try {
            Message msg = new Message("Gesture",gesture);
            out.writeObject(msg + "\n");
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
                case "ConnectBackRequest": 
                    System.out.println("placeholder for ConnectBackRequest");
                    break;
                case "ServerSocketAddress":
                    InetSocketAddress serverToConnectTo = (InetSocketAddress) msg.getMsgObj();
                    //connect to the other peer so it creates a 
                    //PeerHandler for this peer
                    String serverIp = serverToConnectTo.getHostString();
                    int serverPort = serverToConnectTo.getPort();
                    Socket socket = new Socket (serverIp, serverPort);
                    System.out.println("Trying to initialize input- output streams in receiveMessage in PeerHandler");
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    System.out.println("receiveMessage input- output streams initialized!");
                    break;
                case "Gesture":
                    Gesture gesture = (Gesture) msg.getMsgObj();
                    me.updateGameState(this,gesture);
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
