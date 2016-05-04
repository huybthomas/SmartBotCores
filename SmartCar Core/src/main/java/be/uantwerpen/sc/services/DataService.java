package be.uantwerpen.sc.services;

import be.uantwerpen.sc.tools.PathplanningType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Arthur on 24/04/2016.
 */
@Service
public class DataService {

    private float millis;

    public boolean robotBusy = false;

    public String trafficLightStatus;

    private String tag;

    public float getMillis() {return millis;}
    public void setMillis(float millis) {this.millis = millis;}

    public String getTag() {return tag;}
    public void setTag(String tag) {this.tag = tag;}
}
