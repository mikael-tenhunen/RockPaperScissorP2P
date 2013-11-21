package rockpaperscissor;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        startProgram();
        //TEST
        try {
            System.out.println(InetAddress.getLocalHost());
//            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
//            System.out.println(netInterfaces);
//            NetworkInterface netInterface;
//            while(netInterfaces.hasMoreElements()) {
//                netInterface = netInterfaces.nextElement();
//                System.out.println(netInterface + ", " + );
//                
//            }
        } catch (Exception ex) {
            Logger.getLogger(RockPaperScissor.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TEST
    }
    
    public static void startProgram() {
//        Thread t = new Thread() {
//            public void run() {
//                ConnectWindow.startConnectWindow();
//            }
//        };
//        t.start();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConnectWindow.startConnectWindow();
            }
        });  
    }
    
    /**
     * startServer creates a new Peer object and starts the server role thread 
     * for the specific peer and then returns the Peer object.
     * @param port
     * @return Peer
     * @throws IOException
     */
    public static Peer startServer(int port) throws IOException {
        ServerSocket servSocket = new ServerSocket();
        servSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(),port),port);
        Peer peer = new Peer (servSocket);   
        //ServerRole of peer is initialized in startServerRole method of Peer
        peer.startServerRole();

        return peer;
    }
}
