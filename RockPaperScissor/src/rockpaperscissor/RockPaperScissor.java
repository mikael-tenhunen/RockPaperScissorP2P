package rockpaperscissor;

import java.io.IOException;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author miikka   
 */
public class RockPaperScissor {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Choose Port to listen to new connections: ");
        int port = in.nextInt();
        try {
            Peer peer = startServer(port);
        
        //TEST    
        testConnect(peer);    
        //TEST
            
        //TEST
        sendTestMessage(peer);
        //TEST
        } catch (IOException ex) {
            Logger.getLogger(RockPaperScissor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Peer startServer(int port) throws IOException {
        ServerSocket servsocket = new ServerSocket(port);
        Peer peer = new Peer (servsocket);   
        //ServerRole of peer is initialized in startServerRole method of Peer
        peer.startServerRole();
        
//        //TEST KOD
//        if (port == 8666) {
//            System.out.println("Trying to connect to other peer at port 8180");
//            peer.connectToPeer("localhost",8180);
//        }   
//        //TEST KOD
        return peer;
    }
    
    public static void sendTestMessage(Peer peer) {
        Scanner in = new Scanner(System.in);
        System.out.print("Write a message: ");
        String message = in.nextLine();
        peer.testMessage(message);
    }
    
    public static void testConnect(Peer peer) {
        Scanner in = new Scanner(System.in);
        System.out.print("If you want to connect to another peer's port, enter"
                + " port nr (otherwise enter 0): ");
        int port = in.nextInt();
        if (port != 0) {
            peer.connectToPeer("localhost",port);
        }
    }
}
