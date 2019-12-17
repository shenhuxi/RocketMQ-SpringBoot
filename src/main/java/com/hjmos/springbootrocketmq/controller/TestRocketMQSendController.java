package com.hjmos.springbootrocketmq.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.entity.ProduceMessage;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageService produceMessageService;
    static int numb = 0;
    static long star =0;

    @GetMapping("/createMessage")
    public boolean createMessage() {
        log.info("创建一个用户消息开始...........");
//        User user = new User("jardon", 18);
//        JSONObject.toJSONString(user);
        ProduceMessage produceMessage = new ProduceMessage("T7", "book", RandomStringUtils.random(1));
        produceMessageService.produceMessage(produceMessage);
        return true;
    }

    @GetMapping("/manyRequest")
    public boolean manyRequest() {
        log.info("创建一个用户消息开始...........");

        final long number =100000L;
        numb = 0;

        List<String> list = new ArrayList<>();
        for (int i = 1; i <= number; i++) {
            list.add(RandomStringUtils.randomAlphanumeric(100));
            //list.add(String.valueOf(i));
        }

        log.info("执行开始："+System.currentTimeMillis());
        star = System.currentTimeMillis();
        new Thread(() -> {
            list.parallelStream().forEach(u -> {
                produceMessageService.produceMessage(new ProduceMessage("T7", "aa", u));
                addNumber();
            });
        }).start();


        return true;
    }
    private synchronized void addNumber(){
        numb = ++numb;
        if(numb ==100000){
            log.info("执行结束 吞吐量为："+100000*1000/(System.currentTimeMillis()-star));
        }
    }

    @GetMapping("/transactionMQ")
    public boolean transactionMQ() throws MQClientException {
        log.info("创建一个事务消息开始...........");
        produceMessageService.transactionMQ(new ProduceMessage("T5", "Transaction_rollback", "Test RocketMQ Transaction is OK？"));
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
