package com.pci.hjmos.controller;

import com.pci.hjmos.api.produce.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
@RequestMapping("/TestRocketMQSend")
public class TestRocketMQSendController {
    @Autowired
    private ProduceMessageService kafkaProduceMessageServiceImpl;

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
