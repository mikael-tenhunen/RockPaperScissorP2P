package rockpaperscissor;

import java.io.IOException;
import java.util.*;
import java.net.*;
import userinterface.ConnectWindow;
/**
 * This is a the main class for the game Rock Paper Scissor.  * 
 */
public class RockPaperScissor {

    /**
     * Main method creates a new GUI-thread and starts a connect window. 
     * ConnectWindow calls startServer.
     * @param args
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Thread t = new Thread() {
            public void run() {
                ConnectWindow.startConnectWindow();
            }
        };
        t.start();
    }
    
    /**
     * startServer creates a new Peer object and starts the server role thread 
     * for the specific peer and then returns the Peer object.
     * @param port
     * @return Peer
     * @throws IOException
     */
    public static Peer startServer(int port) throws IOException {
        ServerSocket servsocket = new ServerSocket(port);
        Peer peer = new Peer (servsocket);   
        //ServerRole of peer is initialized in startServerRole method of Peer
        peer.startServerRole();

        return peer;
    }
}
