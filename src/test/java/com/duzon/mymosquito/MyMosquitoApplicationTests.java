package com.duzon.mymosquito;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyMosquitoApplicationTests {

	final String AWS = "tcp://ec2-3-35-47-67.ap-northeast-2.compute.amazonaws.com:1883";
	final String LOCAL = "tcp://localhost:1883";



	@Test
	public void test() {
		try {
			MqttClient client = new MqttClient(LOCAL, "clientID");
			client.connect();
			MqttMessage message = new MqttMessage();
			message.setPayload("send my message!!".getBytes());
			client.publish("test1", message);
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

}
