package be.uantwerpen.sc.configurations;

import be.uantwerpen.sc.RobotCoreLoop;
import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.controllers.CStatusEventHandler;
import be.uantwerpen.sc.services.DataService;
import be.uantwerpen.sc.services.QueueService;
import be.uantwerpen.sc.tools.QueueConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import be.uantwerpen.sc.services.TerminalService;

/**
 * Created by Thomas on 14/04/2016.
 */
@Configuration
public class SystemLoader implements ApplicationListener<ContextRefreshedEvent>
{
    @Autowired
    TerminalService terminalService;
    @Autowired
    QueueService queueService;
    @Autowired
    CStatusEventHandler cStatusEventHandler;
    @Autowired
    CCommandSender cCommandSender;
    @Autowired
    DataService dataService;

    RobotCoreLoop robotCoreLoop;

    //Run after Spring context initialization
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        QueueConsumer queueConsumer = new QueueConsumer(queueService);
        new Thread(queueConsumer).start();
        terminalService.systemReady();

        robotCoreLoop = new RobotCoreLoop();


    }
}
