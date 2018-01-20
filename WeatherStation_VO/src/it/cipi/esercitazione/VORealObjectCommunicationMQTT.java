
package it.cipi.esercitazione;

import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import org.eclipse.paho.client.mqttv3.MqttCallback;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class VORealObjectCommunicationMQTT implements MqttCallback {

	MqttClient client;
	String topic        = "WeatherStation_VO";
    int qos             = 2;
    String broker       = "tcp://iot.eclipse.org:1883";
    String clientId     = "JavaSample1";

	MemoryPersistence persistence = new MemoryPersistence();
	HashMap<String,String> data = new HashMap<String,String>();


	

	public void doDemo() {

		try {

			client = new MqttClient(broker, clientId, persistence);

			MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            client.connect(connOpts);

			client.setCallback(this);

			client.subscribe(topic + "/#");

			MqttMessage message = new MqttMessage();

			message.setPayload("A single message from my computer fff"

					.getBytes());

			//client.publish("MQTT Examples", message);

		} catch (MqttException e) {

			e.printStackTrace();

		}

	}

	@Override

	public void connectionLost(Throwable cause) {

		// TODO Auto-generated method stub

	}

	@Override

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("Messaggio arrivato");
		new SSSConnection(message.toString()).start();
		new OnReceiveNewData().start();

	}
	
	public HashMap<String,String> getData()
	{
		for(int i=0;i<this.data.size();i++)
			System.out.println(this.data.get(String.valueOf(i+1)));
		return this.data;
	}
	
	@Override

	public void deliveryComplete(IMqttDeliveryToken token) {

		// TODO Auto-generated method stub

	}

}