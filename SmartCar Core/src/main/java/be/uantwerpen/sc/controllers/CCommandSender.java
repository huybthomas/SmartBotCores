package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.tools.Terminal;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Arthur on 2/05/2016.
 */
@Service
public class CCommandSender {

    Socket socket;
    DataOutputStream dOut;
    DataInputStream dIn;

    public CCommandSender(){
        try{
            socket = new Socket("146.175.140.190", 1313);
            socket.setSoTimeout(500);
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public CCommandSender(String ip){
        try{
            socket = new Socket(ip, 1313);
            socket.setSoTimeout(500);
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendCommand(String str){
        try {
            //byte[] message = str.getBytes();
            //System.out.println(message.toString());
            int attempts = 0;

            str = str.concat("\n");
            byte[] bytes = str.getBytes();

            //while(attempts <5) {
                //Send message
                //dOut.writeInt(message.length); // write length of the message
                dOut.flush();
                dOut.write(bytes);
                /*
                //Receive Message
                try {
                    //Check if acknowledged
                    byte[] ackBytes = new byte[4];
                    dIn.readFully(ackBytes);
                    String response = new String(ackBytes);
                    Terminal.printTerminal("Response:" + response);
                    if(response.startsWith("ACK")  || response.startsWith("Smar")){
                        //Message acknowledged
                        return true;
                    }if(response.startsWith("NACK")){
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
                */
            //}

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Terminal.printTerminalInfo("IOException");
            return false;
        }
    }

    public boolean close(){
        try{
            socket.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }


    }
}
