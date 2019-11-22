package com.hjmos.springbootrocketmq.controller;

import com.hjmos.springbootrocketmq.annotation.ProduceMessage;
import com.hjmos.springbootrocketmq.enums.TransactionEnum;
import com.hjmos.springbootrocketmq.service.impl.ProduceMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/testSend")
@Slf4j
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageServiceImpl produceMessageServiceImpl;

    @GetMapping("/createOrder")
    @ProduceMessage(topic = "TopicDemo2", tag = "white", content = "测试发送信息1", keys ="", orderId = 0, transaction = TransactionEnum.NO)
    public boolean createOrder(String  obj) {
        log.info("生成订单完成，发送消息到RocketMQ");
        return true;
    }

    @GetMapping("/createMessge")
    public boolean createMessge(@RequestParam String  userName) {
        log.info("创建一个用户消息开始...........");
        produceMessageServiceImpl.sendAsyncDefault("user-topic","white",null,userName);
        return true;
    }
}
