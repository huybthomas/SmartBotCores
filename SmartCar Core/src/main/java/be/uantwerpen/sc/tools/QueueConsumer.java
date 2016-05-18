package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.QueueService;
import org.springframework.web.client.RestTemplate;

import javax.swing.plaf.basic.BasicTreeUI;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Niels on 4/05/2016.
 */
public class QueueConsumer implements Runnable
{
    private CCommandSender sender;
    private QueueService queueService;
    private DataService dataService;

    private boolean first = true;

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
                    Terminal.printTerminal("PrevNode: " + dataService.getPrevNode());
                    if(!first) {
                        //System.out.println(queueService.getContentQueue().toString());
                        //check if robot has to wait before point
                        Terminal.printTerminal("Distance: " + dataService.getMillis() + "\nStopDistance: " + (dataService.getLinkMillis() - 150));
                        Terminal.printTerminal("Permission:" + dataService.hasPermission());
                        if (dataService.getMillis() > dataService.getLinkMillis() - 150 && !(dataService.hasPermission() == dataService.getNextNode())) {
                            //Pause robot
                            sender.sendCommand("DRIVE PAUSE");
                            //Ask for permission
                            RestTemplate rest = new RestTemplate();
                            boolean response = false;
                            while (!response) {
                                Terminal.printTerminal("Lock Requested");
                                response = rest.getForObject("http://" + dataService.serverIP + "/point/requestlock/" + dataService.getNextNode(), boolean.class);
                                if (!response) {
                                    Terminal.printTerminal("Lock Denied: " + dataService.getNextNode());
                                    Thread.sleep(200);
                                }
                            }
                            //response true -> Lock granted
                            Terminal.printTerminal("Lock Granted: " + dataService.getNextNode());
                            dataService.setPermission(dataService.getNextNode());
                            sender.sendCommand("DRIVE RESUME");
                        }
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
                            if(!first) {
                                dataService.nextLink();
                                if(dataService.hasPermission() == dataService.getNextNode()){
                                    //Leave permission
                                }else {
                                    dataService.setPermission(-1);
                                }
                            }else{
                                first = false;
                            }

                            //Unlock point
                            RestTemplate rest = new RestTemplate();
                            rest.getForObject("http://" + dataService.serverIP + "/point/setlock/" + dataService.getPrevNode() + "/0", Integer.class);
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
