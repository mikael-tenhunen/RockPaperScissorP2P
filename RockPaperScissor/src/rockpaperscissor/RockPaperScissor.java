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
        
        System.out.print("Choose Port: ");
        int port = in.nextInt();
        try {
            ServerSocket servsocket = new ServerSocket(port);
            Peer peer = new Peer (servsocket);
        } catch (IOException ex) {
            Logger.getLogger(RockPaperScissor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
