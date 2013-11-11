package rockpaperscissor;

import java.net.InetAddress;
import java.net.ServerSocket;
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
    List<ServerSocket> players;
    List<Gesture> currentChoices;
    Map<ServerSocket,Integer> scores;
    ServerSocket me;
    
    public Peer(ServerSocket socket) {
        me = socket;
        players = new ArrayList();
        currentChoices = new ArrayList();
        scores = new HashMap();
    }

    public ServerSocket getServerSocket() {
        return me;
    }
}
