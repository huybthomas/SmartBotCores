package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Arthur on 28/04/2016.
 */
public class NavigationParser {

    @Autowired
    private DataService dataService;
    public List<Vertex> list;
    public Queue<DriveDir> commands = new LinkedList<DriveDir>();

    public NavigationParser(List<Vertex> list){
        this.list = list;
    }

    public Queue<DriveDir> parseMap(){
        if(list.isEmpty()){
            Terminal.printTerminalError("Cannot parse empty map");
        }else{
            //First part is always driving forward.
            commands.add(new DriveDir(DriveDirEnum.FOLLOW));
            //Second part is parsing the rest of the map
            Vertex current = list.get(0);
            Vertex previous = list.get(0);
            Vertex next = list.get(1);
            for(int i = 2; i < list.size(); i++){
                previous = current;
                current = next;
                next = list.get(i);
                direction start = findStartDir(current);
                direction stop = findStopDir(next);
                DriveDir relDir = getNextRelDir(start, stop);
                //Pass crossroad
                commands.add(relDir);
                //Drive followLine
                commands.add(new DriveDir(DriveDirEnum.FOLLOW));
            }
        }
        return commands;
    }

    private direction findStartDir(Vertex current){
        int i = current.getPrevious().getAdjacencies().indexOf(current);
        String dirString = current.getPrevious().getAdjacencies().get(i).getLinkEntity().getStopDirection();
        direction dir = getDirection(dirString);
        return dir;
    }

    private direction findStopDir(Vertex next){
        int i = next.getPrevious().getAdjacencies().indexOf(next);
        String dirString = next.getPrevious().getAdjacencies().get(i).getLinkEntity().getStartDirection();
        direction dir = getDirection(dirString);
        return dir;
    }

    private direction getDirection(String dirString){
        switch(dirString){
            case "N":
                return direction.NORTH;
            case "E":
                return direction.EAST;
            case "S":
                return direction.SOUTH;
            case "W":
                return direction.WEST;
            default:
                return direction.NORTH;
        }
    }

    private DriveDir getNextRelDir(direction startDir, direction stopDir){
        //Calculate relative direction
        switch(startDir)
        {
            //From NORTH
            case NORTH:
                switch(stopDir)
                {
                    //Go EAST
                    case EAST:
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn LEFT
                    //Go SOUTH
                    case SOUTH:
                        return new DriveDir(DriveDirEnum.FORWARD);   //Go STRAIGHT
                    //Go WEST
                    case WEST:
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn RIGHT
                    
                }
                
            //From EAST
            case EAST:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn RIGHT
                    //Go SOUTH
                    case SOUTH:
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn LEFT
                    //Go WEST
                    case WEST:
                        return new DriveDir(DriveDirEnum.FORWARD);   //Go STRAIGHT
                }
                
            //From SOUTH
            case SOUTH:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return new DriveDir(DriveDirEnum.FORWARD);   //Go STRAIGHT
                    //Go EAST
                    case EAST:
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn RIGHT
                    //Go WEST
                    case WEST:
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn LEFT
                    
                }
                
            //From WEST
            case WEST:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return new DriveDir(DriveDirEnum.LEFT);   //Turn LEFT
                    
                    //Go EAST
                    case EAST:
                        return new DriveDir(DriveDirEnum.FORWARD);   //Go STRAIGHT
                    
                    //Go SOUTH
                    case SOUTH:
                        return new DriveDir(DriveDirEnum.RIGHT);   //Turn RIGHT
                    
                }
                
        }

        //Invalid direction
        return null;
    }

    public Queue<DriveDir> parseRandomMap(DataService dataService) {
        if (list.isEmpty()) {
            Terminal.printTerminalError("Cannot parse empty map");
        } else {
            int i = 0;
            for (Edge e : list.get(0).getAdjacencies()) {
                if (e.getTarget() == list.get(1).getId()) {
                    break;
                }
                i++;
            }
            if (dataService.getLookingCoordiante().equals(list.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection())) {
                //dataService.setLookingCoordiante(path.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection());
                commands.add(new DriveDir(DriveDirEnum.FORWARD));
                commands.add(new DriveDir(DriveDirEnum.FOLLOW));//System.out.println(parseMap().toString());
            } else {
                //Queue<DriveDir> commands = new LinkedList<DriveDir>();
                commands.add(relDirRandom(dataService.getLookingCoordiante(), list.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection()));
                commands.add(new DriveDir(DriveDirEnum.FOLLOW));
                //NavigationParser navigationParser = new NavigationParser(path);
                //System.out.println(commands.toString());
                //System.out.println(parseMap().toString());
                switch (list.get(0).getAdjacencies().get(i).getLinkEntity().getLid()) {
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
                        dataService.setLookingCoordiante(list.get(0).getAdjacencies().get(i).getLinkEntity().getStartDirection());
                }
            }

            //dataService.setCurrentLocation(list.get(1).getId());
        }
        return commands;
    }

    private DriveDir relDirRandom(String startDir, String stopDir){
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

enum direction{
    NORTH,
    EAST,
    SOUTH,
    WEST
}