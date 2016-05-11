package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Arthur on 11/05/2016.
 */
public class CLocationPoller implements Runnable {

    @Autowired
    CCommandSender cCommandSender;

    public CLocationPoller(CCommandSender cCommandSender){
        this.cCommandSender = cCommandSender;
    }

    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            try{
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
            synchronized (this){
                cCommandSender.sendCommand("DRIVE DISTANCE");
            }
        }
    }
}
