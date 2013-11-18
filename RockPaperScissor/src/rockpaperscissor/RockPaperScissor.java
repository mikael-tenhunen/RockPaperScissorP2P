package rockpaperscissor;

import java.io.IOException;
import java.util.*;
import java.net.*;
import userinterface.ConnectWindow;
/**
 *
 * @author miikka   
 */
public class RockPaperScissor {

    /**
     *
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
     *
     * @param port
     * @return
     * @throws IOException
     */
    public static Peer startServer(int port) throws IOException {
        ServerSocket servsocket = new ServerSocket(port);
        Peer peer = new Peer (servsocket);   
        //ServerRole of peer is initialized in startServerRole method of Peer
        peer.startServerRole();

        return peer;
    }
    
    /**
     *
     * @param peer
     */
    public static void sendTestMessage(Peer peer) {
        Scanner in = new Scanner(System.in);
        System.out.print("Write a message: ");
        String message = in.nextLine();
        peer.testMessage(message);
    }
    
    /**
     *
     * @param peer
     */
    public static void testConnect(Peer peer) {
        Scanner in = new Scanner(System.in);
        System.out.print("If you want to connect to another peer's port, enter"
                + " port nr (otherwise enter 0): ");
        int port = in.nextInt();
        if (port != 0) {
            peer.connectToPeer("localhost",port,true);
        }
    }
}
