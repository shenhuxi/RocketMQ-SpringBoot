package com.hjmos.springbootrocketmq.controller;

import com.hjmos.springbootrocketmq.annotation.ProduceMessage;
import com.hjmos.springbootrocketmq.entity.SubwayOrder;
import com.hjmos.springbootrocketmq.enums.TransactionEnum;
import com.hjmos.springbootrocketmq.service.impl.ProduceMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/testSend")
@Slf4j
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageServiceImpl produceMessageServiceImpl;

    @PostMapping("/createOrder")
    @ProduceMessage(topic = "user-topic", tag = "white", content = "#order.userName", keys ="", orderId = 0, transaction = TransactionEnum.NO)
    public boolean createOrder(@RequestBody SubwayOrder order) {
        log.info("生成订单完成，发送消息到RocketMQ");
        return true;
    }

    @GetMapping("/createMessge")
    @ProduceMessage(topic = "user-topic", tag = "white", content = "#userName", keys ="", orderId = 0, transaction = TransactionEnum.NO)
    public boolean createMessge(@RequestParam String  userName) {
        log.info("创建一个用户消息开始...........");
       // produceMessageServiceImpl.sendAsyncDefault("user-topic","white",null,userName);
        return true;
    }
}
