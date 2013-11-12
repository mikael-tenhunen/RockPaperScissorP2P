package rockpaperscissor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miikka
 */
public class Peer {
    private List<PeerHandler> playerHandlers;
    private List<Gesture> currentChoices;
    private Map<PeerHandler,Integer> scores;
    private ServerSocket serverSocket;
    
    
    public Peer(ServerSocket socket) {
        serverSocket = socket;
        playerHandlers = new ArrayList();
        currentChoices = new ArrayList();
        scores = new HashMap();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    //addPlayer is called when the serversocket accepts new player connection
    public void addPlayer(PeerHandler peerHandler) {
        playerHandlers.add(peerHandler);
    }
    
    public void connectToPeer(String otherPeerIp, int port) {
        try {
            Socket socket = new Socket (otherPeerIp, port);
//            PeerHandler peerHandler = new PeerHandler(socket, this);
//            addPlayer(peerHandler);
            //LIGG OCH LYSSNA ConnectBackRequest
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object returnMessage = null;
            SocketAddress localServerAddress = serverSocket.getLocalSocketAddress();
            //Send local server address to the other peer so it can
            //connect to this peer and this peer creates a PeerHandler for it
            Message msg = new Message("ServerSocketAddress", localServerAddress);
            //write message to output stream
            out.writeObject (msg);
            
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deletePeer() {
    }
    
    public void playGesture() {
    }
}
