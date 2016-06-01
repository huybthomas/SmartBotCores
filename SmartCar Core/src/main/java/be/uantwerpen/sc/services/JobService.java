package be.uantwerpen.sc.services;

import be.uantwerpen.sc.RobotCoreLoop;
import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.models.Job;
import be.uantwerpen.sc.tools.DriveDir;
import be.uantwerpen.sc.tools.NavigationParser;
import be.uantwerpen.sc.tools.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * Created by Thomas on 01/06/2016.
 */
@Service
public class JobService
{
    @Autowired
    private DataService dataService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private CCommandSender sender;

    private RobotCoreLoop robotCoreLoop;

    public void setRobotCoreLoop(RobotCoreLoop robotCoreLoop)
    {
        this.robotCoreLoop = robotCoreLoop;
    }

    public void parseJob(String job) throws ParseException
    {
        if(!job.startsWith("Job{jobId=") || job.split(", ", 2).length <= 1)
        {
            //Not a valid job string
            throw new ParseException("Can not parse job from: " + job + "\nInvalid type!", 0);
        }

        try
        {
            String jobDescription = job.split(", ", 2)[1];

            if(!jobDescription.startsWith("jobDescription='"))
            {
                //Not a valid job string
                throw new ParseException("Can not parse job from: " + job + "\nInvalid field!", 0);
            }

            Job parsedJob = new Job(0, jobDescription.split("'", 3)[1]);

            performJob(parsedJob);
        }
        catch(Exception e)
        {
            //Could not parse job from string
            throw new ParseException("Can not parse job from: " + job + "\nInvalid format!", 0);
        }
    }

    private void performJob(Job job)
    {
        String jobDescription = job.getJobDescription();

        System.out.println("JOB DESCRIPTION: " + jobDescription);

        switch(jobDescription.split(" ", 2)[0].toLowerCase())
        {
            case "navigate":
                try {
                    String end = jobDescription.split(" ", 2)[1].toLowerCase();
                    try {
                        int endInt = Integer.parseInt(end);
                        startPathPlanning(endInt);
                    } catch (NumberFormatException e) {
                        Terminal.printTerminalError(e.getMessage());
                        Terminal.printTerminalInfo("Usage: navigate start end");
                    }
                }catch(ArrayIndexOutOfBoundsException e){
                    Terminal.printTerminalInfo("Usage: navigate start end");
                }
                break;
            case "playaudio":
                sender.sendCommand("SPEAKER UNMUTE");
                sender.sendCommand("SPEAKER PLAY QMusic");
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                sender.sendCommand("SPEAKER PLAY cantina");
                break;
            default:
                System.out.println("Unknown job description: " + jobDescription);
        }
    }

    private void startPathPlanning(int end){
        Terminal.printTerminal("Starting pathplanning from point " + dataService.getCurrentLocation() + " to " + end);
        dataService.navigationParser = new NavigationParser(robotCoreLoop.pathplanning.Calculatepath(dataService.map,dataService.getCurrentLocation(), end));
        //Parse Map
        //dataService.navigationParser.parseMap();
        dataService.navigationParser.parseRandomMap(dataService);

        //Setup for driving
        dataService.setNextNode(dataService.navigationParser.list.get(1).getId());
        dataService.setPrevNode(dataService.navigationParser.list.get(0).getId());
        queueService.insertJob("DRIVE FOLLOWLINE");
        queueService.insertJob("DRIVE FORWARD 50");

        //Process map
        for (DriveDir command : dataService.navigationParser.commands) {
            queueService.insertJob(command.toString());
        }
    }
}
