package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.services.DataService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by Thomas on 01/06/2016.
 */
@Service
public class MqttJobSubscriber
{
    @Autowired
    private DataService dataService;

    @Value("${mqtt.ip:localhost}")
    private String mqttIP;

    @Value("#{new Integer(${mqtt.port}) ?: 1883}")
    private int mqttPort;

    @Value("${mqtt.username:default}")
    private String mqttUsername;

    @Value("${mqtt.password:default}")
    private String mqttPassword;

    private String brokerURL;

    //We have to generate a unique Client id.
    private MqttClient mqttSubscribeClient;

    public MqttJobSubscriber()
    {

    }

    public boolean initialisation()
    {
        String clientId = "-1";

        //IP / port-values are initialised at the end of the constructor
        brokerURL = "tcp://" + mqttIP + ":" + mqttPort;

        if(dataService.getRobotID() != null)
        {
            clientId = dataService.getRobotID().toString();
        }
        else
        {
            //ClientID unknown
            return false;
        }

        try
        {
            mqttSubscribeClient = new MqttClient(brokerURL, clientId);
            start();
        }
        catch(MqttException e)
        {
            System.err.println("Could not connect to MQTT Broker!");
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void start()
    {
        try
        {
            mqttSubscribeClient.setCallback(new MqttJobSubscriberCallback(this));
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setUserName(mqttUsername);
            connectOptions.setPassword(mqttPassword.toCharArray());
            mqttSubscribeClient.connect(connectOptions);

            //Subscribe to all subtopics of bots
            mqttSubscribeClient.subscribe("BOT/" + dataService.getRobotID() + "/JOB");
        }
        catch(MqttException e)
        {
            System.err.println("Could not subscribe to topics of MQTT service!");
            e.printStackTrace();
            //System.exit(1);
        }
    }
}
