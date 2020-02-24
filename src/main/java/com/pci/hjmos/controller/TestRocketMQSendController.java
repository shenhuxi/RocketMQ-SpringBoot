package com.pci.hjmos.controller;

import com.pci.hjmos.api.produce.MQCallback;
import com.pci.hjmos.api.produce.ProduceMessageService;
import com.pci.hjmos.util.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.callback.Callback;

/**
 * 本类只用于测试服务，实际不存在
 */
@RestController()
@Slf4j
//@RequestMapping("/TestRocketMQSend")
public class TestRocketMQSendController {

//    @Autowired
//    private ProduceMessageService kafkaProduceMessageServiceImpl;

    @Autowired
    private ProduceMessageService produceMessageService;

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/sendMessage")
    public String sendMessage() throws Exception {

        log.info("发送一条消息...........");
        String content = "hhhh";
        //发送同步消息
//        Result res = produceMessageService.sendSyncMsg("my-topic", content);
        // 发送异步消息
        produceMessageService.sendAsyncMsg("my-topic", content, new MQCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }
            @Override
            public void onException(Throwable e) {
                System.out.println("发送失败：" + e);
            }
        });

        return "发送一条异步消息："+content;
    }

    @GetMapping("/sendSyncMsg")
    public String sendSyncMsg(String topic,String content) throws Exception {

        log.info("发送一条消息...........");
        // 发送同步消息
        Result result = produceMessageService.sendSyncMsg(topic, content);

        return "发送一条同步消息："+content;
    }

    /**
     * 发送异步消息测试
     * @param topic   消息主题
     * @param content 消息内容
     * @return
     * @throws Exception
     */
    @GetMapping("/sendAsyncMsg")
    public String sendAsyncMsg(String topic,String content) throws Exception {

        // 发送异步消息
        produceMessageService.sendAsyncMsg(topic, content, new MQCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送异步消息成功，处理下一步业务");
            }
            @Override
            public void onException(Throwable e) {
                log.info("发送异步消息失败，抛出异常："+ e);
            }
        });

        return "发送一条异步消息："+content;
    }

    /**
     * 发送一条单向消息
     * @param topic
     * @param content
     * @return
     */
    @GetMapping("/sendOneWayMsg")
    public String sendOneWayMsg(String topic,String content) {

        log.info("发送一条消息...........");
        // 发送同步消息
        produceMessageService.sendOneWayMsg(topic, content);

        return "发送一条单向消息："+content;
    }

}

/**
 * 仅用于测试使用
 */
/*class User {
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
}*/
