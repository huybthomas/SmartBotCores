package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Arthur on 4/05/2016.
 */
public class CStatusEventHandler implements Runnable{

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dIn;
    @Autowired
    DataService dataService;

    public CStatusEventHandler(){
        try{
            serverSocket = new ServerSocket(1314);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                socket = serverSocket.accept();
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                byte[] bytes = new byte[1024];
                dIn.readFully(bytes);
                String s = new String(bytes);
                //TODO Continue this method
                if (s.startsWith("")){

                }


            }catch(Exception e){
                e.printStackTrace();
            }



        }

        try{
            socket.close();
            serverSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
