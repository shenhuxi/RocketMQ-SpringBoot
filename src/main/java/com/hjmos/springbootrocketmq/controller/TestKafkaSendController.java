package com.hjmos.springbootrocketmq.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.service.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/testKafkaSend")
public class TestKafkaSendController {

    @Autowired
    private KafkaSender kafkaSender;

    @GetMapping("/createMyKafkaMessage")
    public void createMyKafkaMessage() {
        List<String> list = new ArrayList();
        for(int i = 0;i < 1;i++){
            User user = new User("kobe",i);
            list.add(JSONObject.toJSONString(user));
        }

        log.info("创建消息开始...........");
        long start = System.currentTimeMillis();
        list.parallelStream().forEach((u)->{
            kafkaSender.send("my-kafka",1,"22",u);
        });
        long end = System.currentTimeMillis();
        log.info("创建消息结束...........,用时为 ："+ (end-start)/1000 + "秒");

    }
    @GetMapping("/createKafkaTestMessage")
    public void createKafkaTestMessage(){
        List<String> list = new ArrayList();
        for(int i = 0;i < 1;i++){
            User user = new User("jardon",i);
            list.add(JSONObject.toJSONString(user));
        }

        log.info("创建消息开始...........");
        long start = System.currentTimeMillis();
        list.parallelStream().forEach((u)->{
            kafkaSender.send("kafka-test",1,"22",u);
        });
        long end = System.currentTimeMillis();
        log.info("创建消息结束...........,用时为 ："+ (end-start)/1000 + "秒");
    }





}
