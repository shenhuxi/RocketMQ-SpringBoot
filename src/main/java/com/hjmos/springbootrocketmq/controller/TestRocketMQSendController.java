package com.hjmos.springbootrocketmq.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.entity.KafkaProduceMessage;
import com.hjmos.springbootrocketmq.entity.RocketProduceMessage;
import com.hjmos.springbootrocketmq.service.KafkaProduceMessageService;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
public class TestRocketMQSendController {
//    @Autowired
//    private ProduceMessageService produceMessageService;
    @Autowired
    private KafkaProduceMessageService kafkaProduceMessageService;
    @GetMapping("/createRocketMessage")
    public boolean createMessage() {
        log.info("创建一个用户消息开始...........");
        User user = new User("jardon", 18);
        RocketProduceMessage produceMessage = new RocketProduceMessage("AFC-FLOW", "book", JSONObject.toJSONString(user));
        return true;//produceMessageService.produceMessage(produceMessage);
    }
    @GetMapping("/createKafkaMessage")
    public boolean createKafkaMessage(String topic,String content) {
        KafkaProduceMessage produceMessage = new KafkaProduceMessage(topic,content);
        return kafkaProduceMessageService.produceMessage(produceMessage);
    }
}

/**
 * 仅用于测试使用
 */
class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
