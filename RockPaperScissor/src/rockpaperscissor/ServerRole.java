package rockpaperscissor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This is a thread to receive connections from other peers. 
 * Its run method listens for new incoming connections from other peers. 
 * A Runnable PeerHandler object is created for the connecting peer and is run
 * with an Executor.
 */
public class ServerRole implements Runnable {
    private final Peer me;
    private final ServerSocket serverSocket;
    private final static int PLAYERLIMIT = 10;
    private final Executor e;


    /**
     * Constructs a ServerRole object with a thread pool for PeerHandlers with
     * size playerLimit.
     *
     * @param me a Peer object representing the peer that this ServerRole belongs
     * to
     */
    public ServerRole(Peer me) {
        this.me = me;
        serverSocket = me.getServerSocket();
        e = Executors.newFixedThreadPool(PLAYERLIMIT);
    }

    /**
     * Listens for incoming connections, initiates a new PeerHandler object for
     * every peer that wants to connect, and starts the PeerHandler thread.
     */
    @Override
    public void run() {
        System.out.println ("Listening for incoming connections on port " + serverSocket.getLocalPort());

        try {
            //Listen for incoming connections and start threads to handle any new clients
            while (!serverSocket.isClosed()) {
                //ServerSocket.accept() returns a Socket
                Socket peerSocket = serverSocket.accept();
                System.out.println("New client connected from port: " + peerSocket.getPort());
                e.execute(new PeerHandler(peerSocket, me));
                System.out.println("created new PeerHandler");
            }
        } 
        catch (SocketException se) {
        }
        catch (IOException iOException) {
            System.out.println("Something went wrong with server socket...");
            iOException.printStackTrace();
        }
    }
    
    /**
     * @return Executor
     */
    public Executor getExecutor() {
        return e;
    }
}
