package com.hjmos.springbootrocketmq.controller;

import com.hjmos.springbootrocketmq.annotation.ProduceMessage;
import com.hjmos.springbootrocketmq.enums.TransactionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/testSend")
@Slf4j
public class TestRocketMQSend {

    @GetMapping("/createOrder")
    @ProduceMessage(topic = "TopicDemo_Send01", tag = "TagSynchronous_01", content = "测试发送信息1", keys ="", orderId = 0, transaction = TransactionEnum.NO)
    public boolean createOrder(String  obj) {
        log.info("生成订单完成，发送消息到RocketMQ");
        return true;
    }
}
