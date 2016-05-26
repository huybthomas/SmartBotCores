package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.tools.Edge;
import be.uantwerpen.sc.tools.NavigationParser;
import be.uantwerpen.sc.tools.PathplanningType;
import be.uantwerpen.sc.tools.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Arthur on 24/04/2016.
 */
@Service
public class DataService {

    public String serverIP = "146.175.140.86:1994";

    private Long robotID;

    private int millis;
    private int linkMillis;

    public int getNextNode() {
        return nextNode;
    }

    public void setNextNode(int nextNode) {
        this.nextNode = nextNode;
    }

    private int nextNode = -1;

    public int getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(int prevNode) {
        this.prevNode = prevNode;
    }

    private int prevNode = -1;

    public int hasPermission() {
        return hasPermission;
    }

    public void setPermission(int hasPermission) {
        this.hasPermission = hasPermission;
    }

    private int hasPermission = -1;

    public boolean robotBusy = false;

    public String trafficLightStatus;

    public Map map = null;
    public NavigationParser navigationParser = null;

    private String tag = "NO_TAG";
    private int currentLocation = -1;

    public Long getRobotID() {
        return robotID;
    }

    public void setRobotID(Long robotID) {
        this.robotID = robotID;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(int currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getMillis() {return millis;}
    public void setMillis(int millis) {this.millis = millis;}

    public int getLinkMillis() {
        return linkMillis;
    }

    public void setLinkMillis(int linkMillis) {
        this.linkMillis = linkMillis;
    }

    public String getTag() {return tag;}
    public void setTag(String tag) {this.tag = tag;}

    private String LookingCoordiante;

    public String getLookingCoordiante() {
        return LookingCoordiante;
    }

    public void setLookingCoordiante(String lookingCoordiante) {
        LookingCoordiante = lookingCoordiante;
    }

    public void nextLink(){
        if(map != null && navigationParser != null) {
            int start = navigationParser.list.get(0).getId();
            int end = navigationParser.list.get(1).getId();
            nextNode = end;
            prevNode = start;
            int lid = -1;
            //find link from start to end
            for (Edge e : navigationParser.list.get(0).getAdjacencies()) {
                if (e.getTarget() == end) {
                    lid = e.getLinkEntity().getLid();
                    linkMillis = e.getLinkEntity().getLength();
                    Terminal.printTerminal("New Link Distance: " + linkMillis);
                }
            }

            Terminal.printTerminal("Current Link: " + lid);

            //delete entry from navigationParser
            navigationParser.list.remove(0);
            RestTemplate rest = new RestTemplate();
            rest.getForObject("http://" + serverIP + "/bot/" + robotID + "/lid/" + lid, Integer.class);
        }
    }
}
