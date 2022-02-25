package com.duzon.mymosquito.config;

import com.duzon.mymosquito.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    @Autowired
    MqttService mqttService;

    @Value("${mqtt.userName}")
    private String userName;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.serverURI}")
    private String serverURI;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Bean
    public void mqttInit() {
        mqttService.init(userName, password, serverURI, clientId);
        System.out.println("======================= MQTT INIT SUCCESS");
    }

}
