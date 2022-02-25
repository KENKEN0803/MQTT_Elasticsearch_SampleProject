package com.duzon.mymosquito.controller;

import com.duzon.mymosquito.service.ElasticSearchService;
import com.duzon.mymosquito.service.MqttService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class MyRestController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    MqttService mqttService;

    @PostMapping(value = "/setTopic")
    @ResponseBody
    public Map<String, Object> setTopic(@RequestBody Map<String, Object> paramMap) {
        System.out.println("setTopic => " + paramMap);

        String topic = (String) paramMap.get("topic");

        mqttService.subscribe(topic);

        return null;
    }

    @PostMapping(value = "/send")
    @ResponseBody
    public Map<String, Object> send(HttpServletResponse response,
                                    @RequestBody Map<String, Object> paramMap) {
        System.out.println("send => " + paramMap);

        String topic = (String) paramMap.get("topic");
        String msg = (String) paramMap.get("msg");

        try {
            mqttService.sender(topic, msg);
        } catch (MqttException e) {
            response.setStatus(500);
            e.printStackTrace();
        }

        return null;
    }


    @PutMapping(value = "/createIndex")
    @ResponseBody
    public Map<String, Object> createIndex(HttpServletResponse response,
                                           @RequestBody Map<String, Object> paramMap) {

        String index = (String) paramMap.get("index");
        System.out.println("createIndex => " + paramMap);

        try {
            elasticSearchService.createIndex(index);
        } catch (IOException e) {
            response.setStatus(500);
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping(value = "/getDocument")
    public Map<String, Object> getHistory(@RequestParam String index, @RequestParam String id) {

        System.out.println("getDocument => " + index + id);

        Map<String, Object> resMap = null;

        try {
            resMap = elasticSearchService.getDocument(index, id)
                    .getSourceAsMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resMap;
    }

    @GetMapping(value = "/search")
    public List<Map<String, Object>> search(HttpServletResponse response,
                                            @RequestParam String index,
                                            @RequestParam String searchParam) {

        List<Map<String, Object>> list = new ArrayList<>();

        System.out.println("search index => " + index);
        System.out.println("searchParam => " + searchParam);

        try {
            list = elasticSearchService.searchDocument(index, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(500);
        }

        return list;
    }

}
