package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.models.map.MapJson;
import be.uantwerpen.sc.tools.Vertex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Niels on 24/04/2016.
 */
@RestController
@RequestMapping(value = "/map/")
public class MapController
{
    @Value("${sc.core.ip : localhost}")
    private String serverIP;

    @Value("#{new Integer(${sc.core.port}) ?: 1994}")
    private int serverPort;

    /*@RequestMapping(method = RequestMethod.GET)
    public String getMap() throws IOException {
        System.out.println("DoSomething");

        // supposed this is your FirstController url.
        //String url = "http://localhost:1994/map/";
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

    @RequestMapping(method = RequestMethod.GET)
    public Map getMap(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseList;
        responseList = restTemplate.getForEntity("http://" + serverIP + ":" + serverPort + "/map/", Map.class);
        Map map = responseList.getBody();
        return map;
    }

    @RequestMapping(value = "json", method = RequestMethod.GET)
    public MapJson getMapJson(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MapJson> responseList;
        responseList = restTemplate.getForEntity("http://" + serverIP + ":" + serverPort + "/map/json", MapJson.class);
        MapJson map = responseList.getBody();
        return map;
    }

    @RequestMapping(method = RequestMethod.GET ,value = "/map2/")
    public Vertex[] getPath(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Vertex[]> responseList;
        responseList = restTemplate.getForEntity("http://" + serverIP + ":" + serverPort + "/map/1/path/24", Vertex[].class);
        Vertex[] list = responseList.getBody();
        return list;
    }
}
