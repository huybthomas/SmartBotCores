package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.tools.Vertex;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by Niels on 24/04/2016.
 */
@RestController
public class MapController {

    /*@RequestMapping(method = RequestMethod.GET)
    public String getMap() throws IOException {
        System.out.println("DoSomething");

        // supposed this is your FirstController url.
        String url = "http://localhost:1994/map/";
        // create request.
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        // execute your request.
        HttpResponse response = client.execute(request);
        // do whatever with the response.
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        //System.out.println(result.toString());
        return result.toString();
    }*/

    @RequestMapping(method = RequestMethod.GET ,value = "/map/")
    public Map getMap(){
        String coreIP = "localhost:1994";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseList;
        responseList = restTemplate.getForEntity(coreIP.toString()+"map", Map.class);
        Map map = responseList.getBody();
        return map;
    }

    @RequestMapping(method = RequestMethod.GET ,value = "/map2/")
    public Vertex[] getPath(){
        String coreIP = "http://146.175.140.117:1994";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Vertex[]> responseList;
        responseList = restTemplate.getForEntity(coreIP.toString()+"/map/1/path/24", Vertex[].class);
        Vertex[] list = responseList.getBody();
        return list;
    }
}
