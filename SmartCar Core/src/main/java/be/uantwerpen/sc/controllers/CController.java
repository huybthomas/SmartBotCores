package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Arthur on 24/04/2016.
 */
@RestController
public class CController {

    @Autowired
    DataService dataService;

    @RequestMapping(value="/core/mm", method = RequestMethod.POST)
    @ResponseBody
    public void updateMillis(@RequestBody float millis) {
        dataService.setMillis(millis);
    }

    @RequestMapping(value="/core/tag", method = RequestMethod.POST)
    @ResponseBody
    public void updateMillis(@RequestBody String tag) {
        dataService.setTag(tag);
    }

}
