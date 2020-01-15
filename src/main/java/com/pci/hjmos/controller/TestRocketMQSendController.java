package com.pci.hjmos.controller;

import com.pci.hjmos.api.produce.ProduceMessageService;
import com.pci.hjmos.util.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageService kafkaProduceMessageService;
    @GetMapping("/createKafkaSendSyncMsg")
    public Result createKafkaMessage(String topic, String content) throws Exception {
        return kafkaProduceMessageService.sendSyncMsg(topic,content);
    }
    @GetMapping("/createKafkaSendAsyncMsg")
    public boolean createKafkaMessageBatch(String topic,String content)throws Exception {
        kafkaProduceMessageService.sendAsyncMsg(topic,content,new SendCallback(){
            @Override
            public void onSuccess(SendResult sendResult) {

            }
            @Override
            public void onException(Throwable e) {

            }
        });
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
