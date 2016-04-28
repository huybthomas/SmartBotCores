package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.CController;
import be.uantwerpen.sc.controllers.MapController;
import be.uantwerpen.sc.tools.NavigationParser;
import be.uantwerpen.sc.tools.Terminal;
import be.uantwerpen.sc.tools.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Thomas on 14/04/2016.
 */
@Service
public class TerminalService
{
    private Terminal terminal;

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
                terminal.printTerminal("'exit' : shutdown the core.");
                terminal.printTerminal("'help' / '?' : show all available commands.\n");
                break;
        }
    }

    private void startPathPlanning(int start, int end){
        terminal.printTerminal("Starting pathplanning from point " + start + " to " + end);
        //get Map from server
        //Send map + start + end to pathplanning
        MapController mapController = new MapController();

        Vertex[] list = mapController.getPath();
        List<Vertex> list2 = Arrays.asList(list);
        NavigationParser navigationParser = new NavigationParser(list2);
    }
}