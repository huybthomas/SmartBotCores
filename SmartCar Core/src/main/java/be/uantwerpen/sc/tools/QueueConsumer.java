package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.QueueService;
import org.springframework.web.client.RestTemplate;

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
                //System.out.println("Consumer wants to consume");
                Thread.sleep(10);
                if(queueService.getContentQueue().size() == 0){
                    //System.out.println("queue is empty");
                }else{
                    System.out.println(queueService.getContentQueue().toString());
                    //Wait until robot not busy
                    synchronized (this) {
                        //check if robot has to wait before point
                        if(dataService.getMillis() > dataService.getLinkMillis()-1000 && !dataService.hasPermission()){
                            //Pause robot
                            sender.sendCommand("DRIVE PAUSE");
                            //Ask for permission
                            RestTemplate rest = new RestTemplate();
                            boolean response = false;
                            while(!response) {
                                response = rest.getForObject("http://" + dataService.serverIP + "/requestlock/" + dataService.getNextNode(), boolean.class);
                            }
                            //response true -> Lock granted
                            dataService.setPermission(true);
                            sender.sendCommand("DRIVE RESUME");
                        }

                        //If robot not busy
                        if(!dataService.robotBusy) {
                            String s = queueService.getJob();
                            sender.sendCommand(s);

                            if(!s.contains("DRIVE DISTANCE")) {
                                dataService.robotBusy = true;
                            }
                            if(s.contains("DRIVE FOLLOWLINE")){
                                //Next Link
                                dataService.nextLink();
                                dataService.setPermission(false);
                                //Unlock point
                            }
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
