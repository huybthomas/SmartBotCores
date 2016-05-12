package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.QueueService;
import be.uantwerpen.sc.services.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Niels on 11/05/2016.
 */
@RestController
public class SimulationController {

    @Autowired
    private QueueService queueService;
    @Autowired
    private SimulationService simulationService;

    @RequestMapping(value = "/command/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> commandListener(@RequestBody String command, UriComponentsBuilder ucBuilder){

        if(command == null){
            return new ResponseEntity("No Job", HttpStatus.NO_CONTENT);
        }else{
            System.out.println("command = " + command);
            queueService.insertJob(command);
            return new ResponseEntity("ok",HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/simulate/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> commandListener(@RequestBody boolean simulate, UriComponentsBuilder ucBuilder){
        simulationService.setActiveSimulator(simulate);
        System.out.println("Robot simulate mode = " + simulate);
        return new ResponseEntity("ok",HttpStatus.OK);
    }
}
