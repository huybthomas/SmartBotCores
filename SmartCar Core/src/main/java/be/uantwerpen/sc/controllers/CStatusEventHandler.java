package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.net.Socket;

/**
 * Created by Arthur on 4/05/2016.
 */
@Service
public class CStatusEventHandler implements Runnable
{
    @Autowired
    DataService dataService;

    @Autowired
    mqttLocationPublisher locationPublisher;

    Socket socket;
    DataInputStream dIn;

    @Value("{$car.ccore.ip}")
    private String coreIP;

    @Value("{$car.ccore.eventport}")
    private int coreEventPort;

    public CStatusEventHandler(){
        try{
            socket = new Socket(coreIP, coreEventPort);
            dIn = new DataInputStream(socket.getInputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                byte[] bytes = readData();
                String s = new String(bytes);
                //System.out.println(s);
                //TODO Continue this method
                if (s.startsWith("DRIVE EVENT: FINISHED")){
                    synchronized (this){
                        dataService.robotBusy = false;
                    }
                }if (s.startsWith("TRAFFICLIGHT DETECTION EVENT")){
                    String statusString = s.split(":", 2)[1];
                    String status = "";
                    if(statusString.contains("GREEN")){
                        status = "GREEN";
                    }
                    if(statusString.contains("RED")){
                        status = "RED";
                    }
                    if(statusString.contains("NONE")){
                        status = "NONE";
                    }
                    synchronized (this) {
                        dataService.trafficLightStatus = status;
                    }
                }
                if (s.startsWith("TRAVEL DISTANCE EVENT")){
                    String millisString = s.split(":", 2)[1].trim();
                    int millis = Integer.parseInt(millisString);
                    synchronized (this) {
                        //Terminal.printTerminal("Distance: " + millis);
                        dataService.setMillis(millis);
                        locationPublisher.publishLocation(millis);
                    }
                }if (s.startsWith("TAG DETECTION EVENT")){
                    String tag = s.split(":", 2)[1];
                    synchronized (this){
                        dataService.setTag(tag);
                        dataService.robotBusy = false;
                        dataService.setCurrentLocationAccordingTag();
                        if(!tag.trim().equals("NONE")){
                            dataService.locationUpdated = true;}
                    }
                }if (s.startsWith("TRAFFIC_LIGHT")){
                    String trafficlightStatus = s.split(" ", 2)[1];
                    synchronized (this){
                        dataService.trafficLightStatus = trafficlightStatus;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        try{
            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private byte[] readData(){
        byte[] bytes = new byte[1024];
        try {
            byte b = dIn.readByte();
            char c = ((char) b);
            int i = 0;
            while (c != '\n') {
                //Terminal.printTerminal("" + c);
                bytes[i] = b;
                i++;
                b = dIn.readByte();
                c = ((char) b);
            }
            bytes[i-1] = '\0';
            return bytes;
        }catch(Exception e){
            e.printStackTrace();
        }
        return bytes;
    }
}
