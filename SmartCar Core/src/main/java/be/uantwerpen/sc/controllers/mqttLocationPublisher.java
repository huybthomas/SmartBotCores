package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.services.DataService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Arthur on 9/05/2016.
 */
@Service
public class mqttLocationPublisher
{
    @Autowired
    private DataService dataService;

    @Value("${mqtt.ip}")
    private String mqttIP;

    @Value("#{new Integer(${mqtt.port})}")
    private int mqttPort;

    @Value("${mqtt.username}")
    private String mqttUsername;

    @Value("${mqtt.password}")
    private String mqttPassword;

    public void publishLocation(Integer location)
    {
        String content      = location.toString();
        int qos             = 2;
        String topic        = "BOT/" + dataService.getRobotID() + "/Location";
        String broker       = "tcp://" + mqttIP + ":" + mqttPort;
        String clientId = "-1";

        if(dataService.getRobotID() != null)
        {
            clientId = dataService.getRobotID().toString();
        }

        MemoryPersistence persistence = new MemoryPersistence();

        if(dataService.getRobotID() != null)
        {
            try
            {
                MqttClient client = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setUserName(mqttUsername);
                connOpts.setPassword(mqttPassword.toCharArray());
                //System.out.println("Connecting to broker: "+broker);
                client.connect(connOpts);
                //System.out.println("Connected");
                //System.out.println("Publishing message: " + content);
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                client.publish(topic, message);
                //System.out.println("Message published");
                client.disconnect();
            }
            catch (MqttException me)
            {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
        }
    }

    public void close()
    {
        try
        {

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Disconnected");
    }
}
