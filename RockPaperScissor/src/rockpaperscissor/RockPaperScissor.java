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
        sendTestMessage(peer);
        //TEST
        } catch (IOException ex) {
            Logger.getLogger(RockPaperScissor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Peer startServer(int port) throws IOException {
        ServerSocket servsocket = new ServerSocket(port);
        Peer peer = new Peer (servsocket);        
        //start listening server
        Thread serverThread = new Thread(new ServerRole(peer));
        serverThread.start();

        //TEST KOD
        if (port == 8666) {
            System.out.println("Trying to connect to other peer at port 8180");
            peer.connectToPeer("localhost",8180);
        }   
        //TEST KOD
        return peer;
    }
    
    public static void sendTestMessage(Peer peer) {
        Scanner in = new Scanner(System.in);
        System.out.print("Write a message: ");
        String message = in.nextLine();
        peer.testMessage(message);
    }
}
