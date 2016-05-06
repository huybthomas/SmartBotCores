package be.uantwerpen.sc;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.controllers.CStatusEventHandler;
import be.uantwerpen.sc.controllers.MapController;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.PathplanningService;
import be.uantwerpen.sc.services.QueueService;
import be.uantwerpen.sc.services.TerminalService;
import be.uantwerpen.sc.tools.*;
import org.apache.tomcat.jni.Thread;
import org.springframework.beans.factory.annotation.Autowired;

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

    public RobotCoreLoop(QueueService queueService,MapController mapController, PathplanningType pathplanningType){
        this.queueService = queueService;
        this.mapController = mapController;
        this.pathplanningType = pathplanningType;
        //Setup type
        Terminal.printTerminalInfo(pathplanningType.getType().name());

        //Start driving
        start();
    }

    private void start(){
        //Drive forward
        queueService.insertJob("DRIVE FOLLOWLINE");
        //Read tag
        queueService.insertJob("READ TAG");

        //TODO Update location on server (Also on DataService)

        //Setup interface for correct mode
        setupInterface();

        //Use pathplanning (Described in Interface)
        NavigationParser navigationParser = new NavigationParser(pathplanning.Calculatepath(mapController.getMap(),23,18));
        for (DriveDir command : navigationParser.parseMap()){
            queueService.insertJob(command.toString());
        }
        Terminal.printTerminal(navigationParser.parseMap().toString());
    }

    private void setupInterface(){
        pathplanning = new PathplanningService();
    }

    @Override
    public void run(){
        while(!java.lang.Thread.currentThread().isInterrupted()) {

        }
    }
}
