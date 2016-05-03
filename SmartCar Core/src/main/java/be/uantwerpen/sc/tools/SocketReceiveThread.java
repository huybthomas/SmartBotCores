package be.uantwerpen.sc.tools;

import org.apache.catalina.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Arthur on 3/05/2016.
 */
public class SocketReceiveThread extends Thread{

    ServerSocket serverSocket = null;
    boolean received;

    public SocketReceiveThread(ServerSocket serverSocket ,boolean received) {
        super("SocketReceiveThread");
        this.serverSocket = serverSocket;
        this.received = received;
    }
    public void run() {
        try {
            //Receive Answer
            Socket receiveSocket = serverSocket.accept();
            DataInputStream dIn = new DataInputStream(receiveSocket.getInputStream());

            //Use this before if it doesnt work?
            System.out.println(dIn.readChar());

            //Check if acknowledged
            if(dIn.readUTF().equals("ACK")) {
                //Message acknowledged
                synchronized (this) {
                    received = true;
                }
            }

            receiveSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
