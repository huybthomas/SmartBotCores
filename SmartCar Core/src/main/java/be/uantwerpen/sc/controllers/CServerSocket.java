package be.uantwerpen.sc.controllers;

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
            int attemps = 0;

            //Open Sockets
            ServerSocket serverSocket = new ServerSocket(1004);
            Socket socket = new Socket(ip, 1003);
            Socket receiveSocket = null;

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());


            while(!ack && attemps < 5 ) {
                //Send message
                dOut.writeInt(message.length); // write length of the message
                dOut.write(message);           // write the message
                attemps++;

                //Receive Answer
                receiveSocket = serverSocket.accept();
                DataInputStream dIn = new DataInputStream(receiveSocket.getInputStream());

                //Check if acknowledged
                if(dIn.readUTF().equals("ACK")){
                    //Message acknowledged
                    ack = true;
                }

                if(!ack){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //Close all
            serverSocket.close();
            socket.close();
            receiveSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
