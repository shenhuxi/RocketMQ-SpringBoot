package com.hjmos.springbootrocketmq.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.entity.ProduceMessage;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageService produceMessageService;

    @GetMapping("/createMessage")
    public boolean createMessage() {
        log.info("创建一个用户消息开始...........");
        User user = new User("jardon", 18);
        ProduceMessage produceMessage = new ProduceMessage("broker-a", "book", JSONObject.toJSONString(user));
        produceMessageService.produceMessage(produceMessage);
        produceMessage = new ProduceMessage("broker-b", "book", JSONObject.toJSONString(user));
        produceMessageService.produceMessage(produceMessage);
        produceMessage = new ProduceMessage("broker-c", "book", JSONObject.toJSONString(user));
        produceMessageService.produceMessage(produceMessage);
        return true;
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
