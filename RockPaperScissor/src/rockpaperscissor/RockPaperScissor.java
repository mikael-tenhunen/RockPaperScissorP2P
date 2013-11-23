package rockpaperscissor;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
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
     * startServer creates a new Peer object, starts the server role thread 
     * for the specific peer and then returns the Peer object.
     * @param port
     * @return Peer
     * @throws IOException
     */
    public static Peer startServer(int port) throws IOException {
        ServerSocket servSocket = new ServerSocket();
        InetAddress address = InetAddress.getLocalHost();        
        
        //This code adds support for debian-based systems
        try {
            System.out.println(InetAddress.getLocalHost());
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface netInterface;
            Enumeration<InetAddress> addresses;
            while (netInterfaces.hasMoreElements()) {
                netInterface = netInterfaces.nextElement();
                addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && 
                            !address.isLinkLocalAddress() && 
                            (address instanceof Inet4Address)) {
                        break;
                    }
                }
                if (!address.isLoopbackAddress() && 
                        !address.isLinkLocalAddress() && 
                        (address instanceof Inet4Address)) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Problem getting the local host IP-address");
        }

        servSocket.bind(new InetSocketAddress(address,port),port);
        Peer peer = new Peer (servSocket);   
        //ServerRole of peer is initialized in startServerRole method of Peer
        peer.startServerRole();

        return peer;
    }
}
