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
            int attempts = 0;

            //Open Sockets
            Socket socket = new Socket(ip, 1004);

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            while(!ack && attempts <5) {
                //Setup Ack receiver
                ServerSocket serverSocket = new ServerSocket(1003);
                SocketReceiveThread receiver = new SocketReceiveThread(serverSocket);
                receiver.start();

                //Send message
                dOut.writeInt(message.length); // write length of the message
                dOut.write(message);           // write the message

                attempts++;

                int softAttempts = 0;
                while(!ack && softAttempts < 5) {
                    softAttempts++;
                    ack = receiver.getAckStatus();
                    try{
                        Thread.sleep(200);
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                serverSocket.close();
            }

            //Close all

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
