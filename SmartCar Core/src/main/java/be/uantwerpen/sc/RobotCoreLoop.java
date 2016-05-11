package be.uantwerpen.sc;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.controllers.CStatusEventHandler;
import be.uantwerpen.sc.controllers.MapController;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.PathplanningService;
import be.uantwerpen.sc.services.QueueService;
import be.uantwerpen.sc.services.TerminalService;
import be.uantwerpen.sc.tools.*;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Arthur on 4/05/2016.
 */
public class RobotCoreLoop implements Runnable{

    @Autowired
    TerminalService terminalService;
    private QueueService queueService;
    @Autowired
    CStatusEventHandler cStatusEventHandler;
    @Autowired
    CCommandSender cCommandSender;
    @Autowired
    DataService dataService;
    private MapController mapController;
    @Autowired
    private PathplanningType pathplanningType;

    private IPathplanning pathplanning;

    //private String serverIP = "146.175.140.118:1994";
    private String serverIP = "localhost:1994";

    public RobotCoreLoop(QueueService queueService, MapController mapController, PathplanningType pathplanningType, DataService dataService){
        this.queueService = queueService;
        this.mapController = mapController;
        this.pathplanningType = pathplanningType;
        this.dataService = dataService;
        //Setup type
        Terminal.printTerminalInfo("Selected PathplanningType: " + pathplanningType.getType().name());
    }

    public void run(){
        //getRobotId
        RestTemplate restTemplate = new RestTemplate();
        Long robotID = restTemplate.getForObject("http://" + serverIP + "/bot/newRobot", Long.class);
        dataService.setRobotID(robotID);

        Terminal.printTerminal("Got ID: " + robotID);

        //Drive forward
        synchronized (this) {
            queueService.insertJob("DRIVE FOLLOWLINE");
        }
        //Read tag
        synchronized (this) {
            queueService.insertJob("TAG READ UID");
        }
        //Wait for tag read
        synchronized (this) {
            while (dataService.getTag().equals("NO_TAG")) {
                try{
                   Thread.sleep(10);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Terminal.printTerminal("Tag: " + dataService.getTag());

        updateStartLocation();
        Terminal.printTerminal("Start Location: " + dataService.getCurrentLocation());

        //TODO Update location on server (Also on DataService)

        //Setup interface for correct mode
        setupInterface();

        //Use pathplanning (Described in Interface)
        NavigationParser navigationParser = new NavigationParser(pathplanning.Calculatepath(mapController.getMap(),15,6));
        for (DriveDir command : navigationParser.parseMap()){
            synchronized (this) {
                queueService.insertJob(command.toString());
            }
        }
        Terminal.printTerminal(navigationParser.parseMap().toString());
    }

    private void setupInterface(){
        pathplanning = new PathplanningService();
    }

    private void updateStartLocation(){
        switch(dataService.getTag().trim()){
            case "04 67 88 8A C8 48 80":
                dataService.setCurrentLocation(2);
                break;
            case "04 84 88 8A C8 48 80":
                dataService.setCurrentLocation(7);
                break;
            case "04 B3 88 8A C8 48 80":
                dataService.setCurrentLocation(8);
                break;
            case "04 7B 88 8A C8 48 80":
                dataService.setCurrentLocation(5);
                break;
            case "04 8D 88 8A C8 48 80":
                dataService.setCurrentLocation(9);
                break;
            case "04 AA 88 8A C8 48 80":
                dataService.setCurrentLocation(10);
                break;
            case "04 C4 FD 12 Q9 34 80":
                dataService.setCurrentLocation(11);
                break;
            case "04 96 88 8A C8 48 80":
                dataService.setCurrentLocation(12);
                break;
            case "04 A1 88 8A C8 48 80":
                dataService.setCurrentLocation(13);
                break;
            case "04 BC 88 8A C8 48 80":
                dataService.setCurrentLocation(16);
                break;
            case "04 C5 88 8A C8 48 80":
                dataService.setCurrentLocation(17);
                break;
            case "04 EC 88 8A C8 48 80":
                dataService.setCurrentLocation(18);
                break;
            case "04 E3 88 8A C8 48 80":
                dataService.setCurrentLocation(19);
                break;
            case "04 DA 88 8A C8 48 80":
                dataService.setCurrentLocation(21);
                break;
            case "04 D0 88 8A C8 48 80":
                dataService.setCurrentLocation(22);
                break;
            case "UNKNOWN":
                dataService.setCurrentLocation(14);
                break;
            default:
                dataService.setCurrentLocation(-1);
                break;
        }
    }
}
