package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.tools.SocketReceiveThread;
import be.uantwerpen.sc.tools.Terminal;
import org.apache.catalina.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Arthur on 2/05/2016.
 */
public class CCommandSender {

    public CCommandSender(){

    }

    public boolean sendCommand(String str , String ip){
        try {
            //byte[] message = str.getBytes();
            //System.out.println(message.toString());
            int attempts = 0;

            str = str.concat("\n");
            byte[] bytes = str.getBytes();

            //Open Sockets
            Socket socket = new Socket(ip, 1313);
            socket.setSoTimeout(500);
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dIn = new DataInputStream(socket.getInputStream());

            while(attempts <5) {
                /*//Wait for #
                try {
                    socket.setSoTimeout(2000);
                    while (!dIn.readUTF().contains("#")) {
                    }
                }catch(SocketTimeoutException e){
                    //Just try again
                }*/

                //Setup Ack receiver
                //ServerSocket serverSocket = new ServerSocket(1313);
                //SocketReceiveThread receiver = new SocketReceiveThread(serverSocket);
                //receiver.start();

                //Send message
                //dOut.writeInt(message.length); // write length of the message
                dOut.flush();
                //dOut.writeUTF(str);
                dOut.write(bytes);
                //dOut.write(str);       // write the message

                //Receive Message
                //Use this before if it doesnt work?
                try {
                    //Check if acknowledged
                    byte[] ackBytes = new byte[4];
                    dIn.readFully(ackBytes);
                    String response = new String(ackBytes);
                    Terminal.printTerminal("Response:" + response);
                    if(response.startsWith("ACK")){
                        //Message acknowledged
                        socket.close();
                        return true;
                    }if(response.startsWith("NACK")){
                        socket.close();
                        return false;
                    }

                    //clear buffer
                    if(dIn.available() > 0) {
                        byte[] removed = new byte[dIn.available()];
                        dIn.readFully(removed);
                    }




                }catch(SocketTimeoutException e){
                    Terminal.printTerminalInfo("SocketTimeout");
                    e.printStackTrace();
                }
                attempts++;
            }

            //Close all

            socket.close();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            Terminal.printTerminalInfo("IOException");
            return false;
        }
    }
}
