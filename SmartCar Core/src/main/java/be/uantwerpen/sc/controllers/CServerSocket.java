package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.tools.SocketReceiveThread;
import org.apache.catalina.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Arthur on 2/05/2016.
 */
public class CServerSocket {

    public CServerSocket(){

    }

    public void sendCommand(String str , String ip){
        try {
            byte[] message = str.getBytes();
            boolean ack = false;

            //Open Sockets
            ServerSocket serverSocket = new ServerSocket(1004);
            Socket socket = new Socket(ip, 1003);

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());


            while(!ack ) {
                //Send message
                dOut.writeInt(message.length); // write length of the message
                dOut.write(message);           // write the message

                SocketReceiveThread receiver = new SocketReceiveThread(serverSocket, ack);
                receiver.start();

                while(!ack) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //Close all
            serverSocket.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
