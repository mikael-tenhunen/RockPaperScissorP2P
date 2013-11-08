package rockpaperscissor;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miikka
 */
public class PeerNode {
    //this is the socket that this peernode will use to listen to other peers
    Socket socket;
    InetAddress address;
    
    public PeerNode(Socket socket, String host) {
        this.socket = socket;
        try {
            this.address = InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
