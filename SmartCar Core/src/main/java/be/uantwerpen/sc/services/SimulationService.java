package be.uantwerpen.sc.services;

import org.springframework.stereotype.Service;

/**
 * Created by Niels on 11/05/2016.
 */
@Service
public class SimulationService {

    private boolean activeSimulator;

    public boolean isActiveSimulator() {
        return activeSimulator;
    }

    public void setActiveSimulator(boolean activeSimulator) {
        this.activeSimulator = activeSimulator;
    }
}
