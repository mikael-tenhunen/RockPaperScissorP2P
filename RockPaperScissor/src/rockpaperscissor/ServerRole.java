/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rockpaperscissor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is a thread to receive connections from other peers. 
 * It listens constantly for new connections.
 */
public class ServerRole implements Runnable {
    Peer me;
    ServerSocket serverSocket;

    public ServerRole(Peer me) {
        this.me = me;
        serverSocket = me.getServerSocket();
    }

    public void run() {
        System.out.println ("Listening for incoming connections on port " + serverSocket.getLocalPort());

        try {
            //Listen for incoming connections and start threads to handle any new clients
            while (true) {
                //ServerSocket.accept() returns a Socket
                Socket peerSocket = serverSocket.accept();
                System.out.println("New client connected from port: " + peerSocket.getPort());
                PeerHandler peerHandler = new PeerHandler(peerSocket, me);
                //Thread for a PeerHandler for this socket
                System.out.println("created new PeerHandler");
                Thread t = new Thread(peerHandler);
                t.start();
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

}
