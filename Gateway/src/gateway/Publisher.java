package gateway;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Publisher {

	public static void publish(String[] lista ) {
			
		
        String topic        = "WeatherStation_VO";
        int qos             = 2;
        String broker       = "tcp://iot.eclipse.org:1883";
        String clientId     = "Publisher";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
        	MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            for(int i = 0; i < 21; i++) {
            	String msg = lista[i];
            	System.out.println("Publishing message: "+ msg);
            	MqttMessage message = new MqttMessage(msg.getBytes());
            	message.setQos(qos);
            	sampleClient.publish(topic+"/"+Integer.toString(i), message);
            	System.out.println("Message published");
            }
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
