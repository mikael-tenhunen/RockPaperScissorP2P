package rockpaperscissor;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author miikka
 */
public class Peer {
    List<PeerNode> players;
    List<Gesture> currentChoices;
    Map<PeerNode,Integer> scores;
    PeerNode me;
    
    public Peer(Socket socket) {
        me = new PeerNode(socket, "127.0.0.1");
        players = new ArrayList();
        currentChoices = new ArrayList();
        scores = new HashMap();
    }
}
