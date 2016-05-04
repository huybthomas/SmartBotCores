package be.uantwerpen.sc;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.controllers.CStatusEventHandler;
import be.uantwerpen.sc.controllers.MapController;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.PathplanningService;
import be.uantwerpen.sc.services.QueueService;
import be.uantwerpen.sc.services.TerminalService;
import be.uantwerpen.sc.tools.DriveDir;
import be.uantwerpen.sc.tools.IPathplanning;
import be.uantwerpen.sc.tools.NavigationParser;
import be.uantwerpen.sc.tools.Terminal;
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

    public RobotCoreLoop(QueueService queueService,MapController mapController){
        this.queueService = queueService;
        this.mapController = mapController;
        start();
    }

    private void start(){
        //Drive forward
        queueService.insertJob("DRIVE FOLLOWLINE");
        //Read tag
        queueService.insertJob("READ TAG");

        //TODO Update location on server (Also on DataService)

        IPathplanning pathplanning = new PathplanningService();
        NavigationParser navigationParser = new NavigationParser(pathplanning.Calculatepath(mapController.getMap(),23,18));
        for (DriveDir command : navigationParser.parseMap()){
            queueService.insertJob(command.toString());
        }
        Terminal.printTerminal(navigationParser.parseMap().toString());
    }

    @Override
    public void run(){
        while(!java.lang.Thread.currentThread().isInterrupted()) {

        }
    }
}
