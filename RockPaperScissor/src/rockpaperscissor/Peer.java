package rockpaperscissor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
    private ServerSocket me;
    
    
    public Peer(ServerSocket socket) {
       // me = socket;
        playerHandlers = new ArrayList();
        currentChoices = new ArrayList();
        scores = new HashMap();
    }

    public ServerSocket getServerSocket() {
        return me;
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
            
            
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deletePeer() {
    }
    
    public void playGesture() {
    }
}
