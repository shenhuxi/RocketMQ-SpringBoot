package com.hjmos.springbootrocketmq.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.util.entity.ProduceMessage;
import com.hjmos.springbootrocketmq.rocketmqservice.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
@RequestMapping("/TestRocketMQSend")
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageService produceMessageService;
    @GetMapping("/create")
    public boolean createMessage1(String content ) {
        log.info("创建一个用户消息开始...........");
        produceMessageService.produceMessage(new ProduceMessage("my-topic","aa",content));
        return true;
    }

    @GetMapping("/createMessage")
    public boolean createMessage() {
        log.info("创建一个用户消息开始...........");
        List<User> list = new ArrayList<>();

        for(int i = 0;i< 1000000;i++){
            list.add(new User("jardon"+"-"+i, i));
        }

        list.parallelStream().forEach(u->{
            produceMessageService.produceMessage(new ProduceMessage("my-topic","aa",JSONObject.toJSONString(u)));
        });

        return true;
    }

    @GetMapping("/sendAsyncMsg")
    public String sendAsyncMsg() throws Exception {
        produceMessageService.sendAsyncMsg(new ProduceMessage("my-topic","aa","async"));
        return "成功发送一条异步消息";
    }

    @GetMapping("/sendOneWayMsg")
    public String sendOneWayMsg() throws Exception {
        produceMessageService.sendOneWayMsg(new ProduceMessage("my-topic","aa","oneway"));
        return "成功发送一条单向消息";
    }

    @GetMapping("/sendTransactionMsg")
    public String sendTransactionMsg() throws Exception {
        log.info("发送一条事务消息...........");
        produceMessageService.sendTransactionMsg(new ProduceMessage("my-topic","aa","transaction"));
        return "成功发送一条事务消息";
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
