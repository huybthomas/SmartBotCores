package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.CCommandSender;
import be.uantwerpen.sc.controllers.MapController;
import be.uantwerpen.sc.controllers.PathController;
import be.uantwerpen.sc.models.map.Path;
import be.uantwerpen.sc.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Thomas on 14/04/2016.
 */
@Service
public class TerminalService
{
    private Terminal terminal;
    @Autowired
    private MapController mapController;
    @Autowired
    private PathController pathController;
    @Autowired
    private CCommandSender sender;
    @Autowired
    private QueueService queueService;
    @Autowired
    private DataService dataService;

    public TerminalService()
    {
        terminal = new Terminal()
        {
            @Override
            public void executeCommand(String commandString)
            {
                parseCommand(commandString);
            }
        };
    }

    public void systemReady()
    {
        terminal.printTerminal(" :: SmartCar Core - 2016 ::  -  Developed by: Huybrechts T., Janssens A., Joosens D., Vervliet N.");
        terminal.printTerminal("Type 'help' to display the possible commands.");
        terminal.activateTerminal();
    }

    private void parseCommand(String commandString)
    {
        String command = commandString.split(" ", 2)[0].toLowerCase();

        switch(command)
        {
            case "navigate":
                try {
                    String command2 = commandString.split(" ", 2)[1].toLowerCase();

                    String start = command2.split(" ", 2)[0].toLowerCase();
                    String end = command2.split(" ", 2)[1].toLowerCase();
                    if (start == end) {
                        terminal.printTerminal("Start cannot equal end.");
                    } else if (start == "" || end == "") {
                        terminal.printTerminal("Usage: navigate start end");
                    } else {
                        try {
                            int startInt = Integer.parseInt(start);
                            int endInt = Integer.parseInt(end);
                            startPathPlanning(startInt, endInt);
                        } catch (NumberFormatException e) {
                            terminal.printTerminalError(e.getMessage());
                            terminal.printTerminal("Usage: navigate start end");
                        }
                    }
                }catch(ArrayIndexOutOfBoundsException e){
                    terminal.printTerminal("Usage: navigate start end");
                }
                break;
            case "path":
                try {
                    String command2 = commandString.split(" ", 2)[1].toLowerCase();

                    String start = command2.split(" ", 2)[0].toLowerCase();
                    String end = command2.split(" ", 2)[1].toLowerCase();
                    if (start == end) {
                        terminal.printTerminal("Start cannot equal end.");
                    } else if (start == "" || end == "") {
                        terminal.printTerminal("Usage: navigate start end");
                    } else {
                        try {
                            int startInt = Integer.parseInt(start);
                            int endInt = Integer.parseInt(end);
                            getPath(startInt, endInt);
                        } catch (NumberFormatException e) {
                            terminal.printTerminalError(e.getMessage());
                            terminal.printTerminal("Usage: navigate start end");
                        }
                    }
                }catch(ArrayIndexOutOfBoundsException e){
                    terminal.printTerminal("Usage: navigate start end");
                }
                break;
            case "random":
                try {
                    getRandomPath();
                }catch(ArrayIndexOutOfBoundsException e){
                    terminal.printTerminal("Usage: navigate start end");
                }
                break;
            case "sendcommand":
                try {
                    String command2 = commandString.split(" ", 2)[1].toUpperCase();
                    sender.sendCommand(command2);
                }catch(ArrayIndexOutOfBoundsException e){
                    terminal.printTerminal("Usage: navigate start end");
                }
                break;
            case "domusic":
                try {
                    //sender.sendCommand("DRIVE FOLLOWLINE");
                    sender.sendCommand("SPEAKER UNMUTE");
                    sender.sendCommand("SPEAKER PLAY QMusic");
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    sender.sendCommand("SPEAKER PLAY moon");
                }catch(ArrayIndexOutOfBoundsException e){
                    terminal.printTerminal("Usage: navigate start end");
                }
                break;
            case "checkqueue":
                try {
                    System.out.println(queueService.getContentQueue().toString());
                }catch(ArrayIndexOutOfBoundsException e){
                    terminal.printTerminal("error");
                }
                break;
            case "exit":
                exitSystem();
                break;
            case "help":
            case "?":
                printHelp("");
                break;
            default:
                terminal.printTerminalInfo("Command: '" + command + "' is not recognized.");
                break;
        }
    }

    private void exitSystem()
    {
        System.exit(0);
    }

    private void printHelp(String command)
    {
        switch(command)
        {
            default:
                terminal.printTerminal("Available commands:");
                terminal.printTerminal("-------------------");
                terminal.printTerminal("'navigate {start} {end}': navigates the robot from point {start} to {end}");
                terminal.printTerminal("'path {start} {end}': get the path from the server");
                terminal.printTerminal("'random {start}': get random path from the server from start");
                terminal.printTerminal("'simulate {true/false}': activate he simulator");
                terminal.printTerminal("'checkQueue': check content of the queue");
                terminal.printTerminal("'exit' : shutdown the core.");
                terminal.printTerminal("'help' / '?' : show all available commands.\n");
                break;
        }
    }

    private void startPathPlanning(int start, int end){
        terminal.printTerminal("Starting pathplanning from point " + start + " to " + end);
        //get Map from server
        //Send map + start + end to pathplanning

       /* Vertex[] list = mapController.getPath();
        List<Vertex> list2 = Arrays.asList(list);
        NavigationParser navigationParser = new NavigationParser(list2);
        navigationParser.parseMap();*/
        IPathplanning pathplanning = new PathplanningService();
        NavigationParser navigationParser = new NavigationParser(pathplanning.Calculatepath(mapController.getMap(),start,end));
        for (DriveDir command : navigationParser.parseMap()){
            //queueService.insertJob(command.toString());
        }
        System.out.println(navigationParser.parseMap().toString());
    }

    private void getPath(int start, int end){
        Path path = pathController.getPath(start, end);
        System.out.println(path.toString());
    }

    private void getRandomPath(){
        int currentLocation = dataService.getCurrentLocation();
        if(currentLocation < 0) {
            currentLocation = 4;
            dataService.setLookingCoordiante("N");
        }
        IPathplanning pathplanning = new PathplanningService();
        List<Vertex> path = pathController.getRandomPath(currentLocation).getPath();
        int i = 0;
        for(Edge e : path.get(0).getAdjacencies()){
            if(e.getTarget() == path.get(1).getId()){
                break;
            }
            i++;
        }
        NavigationParser navigationParser = new NavigationParser(path);
        if(dataService.getLookingCoordiante().equals(path.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection())){
            //dataService.setLookingCoordiante(path.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection());
            System.out.println(navigationParser.parseMap().toString());

        }else{
            Queue<DriveDir> commands = new LinkedList<DriveDir>();
            commands.add(relDir(dataService.getLookingCoordiante(), path.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection()));
            //NavigationParser navigationParser = new NavigationParser(path);
            System.out.println(commands.toString());
            System.out.println(navigationParser.parseMap().toString());
            switch (path.get(0).getAdjacencies().get(i).getLinkEntity().getLid()){
                case 15:
                    dataService.setLookingCoordiante("E");
                    break;
                case 24:
                    dataService.setLookingCoordiante("N");
                    break;
                case 27:
                    dataService.setLookingCoordiante("E");
                    break;
                case 43:
                    dataService.setLookingCoordiante("E");
                    break;
                case 51:
                    dataService.setLookingCoordiante("N");
                    break;
                default:
                    dataService.setLookingCoordiante(path.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection());
            }
        }

        dataService.setCurrentLocation(path.get(1).getId());
    }

    private DriveDir relDir(String startDir, String stopDir){
        switch(startDir)
        {
            //From NORTH
            case "N":
                switch(stopDir)
                {
                    //Go EAST
                    case "E":
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn LEFT
                    //Go SOUTH
                    case "S":
                        return new DriveDir(DriveDirEnum.TURN);   //Go STRAIGHT
                    //Go WEST
                    case "W":
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn RIGHT

                }

                //From EAST
            case "E":
                switch(stopDir)
                {
                    //Go NORTH
                    case "N":
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn RIGHT
                    //Go SOUTH
                    case "S":
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn LEFT
                    //Go WEST
                    case "W":
                        return new DriveDir(DriveDirEnum.TURN);   //Go STRAIGHT
                }

                //From SOUTH
            case "S":
                switch(stopDir)
                {
                    //Go NORTH
                    case "N":
                        return new DriveDir(DriveDirEnum.TURN);   //Go STRAIGHT
                    //Go EAST
                    case "E":
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn RIGHT
                    //Go WEST
                    case "W":
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn LEFT

                }

                //From WEST
            case "W":
                switch(stopDir)
                {
                    //Go NORTH
                    case "N":
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn LEFT

                    //Go EAST
                    case "E":
                        return new DriveDir(DriveDirEnum.TURN);   //Go STRAIGHT

                    //Go SOUTH
                    case "S":
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn RIGHT

                }

        }

        //Invalid direction
        return null;
    }
}