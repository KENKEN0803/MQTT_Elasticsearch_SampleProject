package com.duzon.mymosquito.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttService implements MqttCallback {

    @Autowired
    private ElasticSearchService elasticSearchService;

    private MqttClient client;

    public MqttService init(String userName, String password, String serverURI, String clientId) {
        MqttConnectOptions option = new MqttConnectOptions();
        option.setCleanSession(true);
        option.setKeepAliveInterval(30);
        option.setUserName(userName);
        option.setPassword(password.toCharArray());
        try {
            client = new MqttClient(serverURI, clientId);

            client.setCallback(this);
            client.connect(option);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("======================= connectionLost");
        throwable.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        HashMap<String, String> jsonBody = new HashMap<>();

        long startTime = System.currentTimeMillis();
        String msg = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);

        System.out.println("======================= messageArrived");
        System.out.println(topic);
        System.out.println(msg);

        jsonBody.put("topic", topic);
        jsonBody.put("msg", msg);

        JSONObject json = new JSONObject(jsonBody);

        try {
            elasticSearchService.createDocument(topic, "received message " + startTime, json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("======================= deliveryComplete");
        try {
            System.out.println(iMqttDeliveryToken.getMessage());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean subscribe(String... topics) {
        try {
            if (topics != null) {
                for (String topic : topics) {
                    client.subscribe(topic, 0);
                }
            }
            System.out.println("======================= MQTT Subscribe SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sender(String topic, String msg) throws MqttPersistenceException, MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        client.publish(topic, message);
        System.out.println("======================= MQTT Publish SUCCESS");
    }

}
