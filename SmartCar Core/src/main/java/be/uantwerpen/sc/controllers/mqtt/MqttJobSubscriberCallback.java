package be.uantwerpen.sc.controllers.mqtt;

import be.uantwerpen.sc.services.JobService;
import be.uantwerpen.sc.tools.Terminal;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Thomas on 01/06/2016.
 */
public class MqttJobSubscriberCallback implements MqttCallback
{
    JobService jobService;
    MqttJobSubscriber subscriber;

    public MqttJobSubscriberCallback(MqttJobSubscriber subscriber, JobService jobService)
    {
        this.subscriber = subscriber;
        this.jobService = jobService;
    }

    @Override
    public void connectionLost(Throwable cause)
    {
        //This is called when the connection is lost. We could reconnect here.
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception
    {
        //TODO Process message
        Terminal.printTerminal("Topic: " + topic + ", Message: " + mqttMessage);
        String payloadString = new String(mqttMessage.getPayload());

        try
        {
            jobService.parseJob(payloadString);
        }
        catch(Exception e)
        {
            System.err.println("Could not parse job from message: " + payloadString);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {

    }
}
