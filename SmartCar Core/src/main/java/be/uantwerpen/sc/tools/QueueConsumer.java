package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Niels on 4/05/2016.
 */
public class QueueConsumer implements Runnable
{
    private CCommandSender sender;
    private QueueService queueService;
    private DataService dataService;

    private BlockingQueue<String> jobQueue;

    public  QueueConsumer(QueueService queueService, CCommandSender sender, DataService dataService)
    {
        this.queueService = queueService;
        this.sender = sender;
        this.dataService = dataService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                //System.out.println("Consimer wants to consume");
                Thread.sleep(10);
                if(queueService.getContentQueue().size() == 0){
                    System.out.println("queue is empty");
                }else{
                    //System.out.println(queueService.getContentQueue().toString());
                    synchronized (this) {
                        if(!dataService.robotBusy) {
                            String s = queueService.getJob();
                            sender.sendCommand(s);
                            dataService.robotBusy = true;
                        }
                    }
                }
                //System.out.println("CrunchifyBlockingConsumer: Message - " + queueService.getJob() + " consumed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
