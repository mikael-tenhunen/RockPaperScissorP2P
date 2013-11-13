/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rockpaperscissor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This is a thread to receive connections from other peers. 
 * It listens constantly for new connections.
 */
public class ServerRole implements Runnable {
    private Peer me;
    private ServerSocket serverSocket;
    private int playerLimit = 10;
    private Executor e;


    public ServerRole(Peer me) {
        this.me = me;
        serverSocket = me.getServerSocket();
        e = Executors.newFixedThreadPool(playerLimit);
    }

    public void run() {
        System.out.println ("Listening for incoming connections on port " + serverSocket.getLocalPort());

        try {
            //Listen for incoming connections and start threads to handle any new clients
            while (true) {
                //ServerSocket.accept() returns a Socket
                Socket peerSocket = serverSocket.accept();
                System.out.println("New client connected from port: " + peerSocket.getPort());
                e.execute(new PeerHandler(peerSocket, me));
                System.out.println("created new PeerHandler");
            }
        } catch (IOException iOException) {
            try {
                    System.out.println ("Something went wrong");
                    serverSocket.close ();
            }
            catch (IOException ioex) {
                    System.out.println ("Problem closing serverSocket");
            }
        }
    }
    
    public Executor getExecutor() {
        return e;
    }
}
