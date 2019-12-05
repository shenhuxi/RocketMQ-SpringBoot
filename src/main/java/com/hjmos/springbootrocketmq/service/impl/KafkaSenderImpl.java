package com.hjmos.springbootrocketmq.service.impl;

import com.hjmos.springbootrocketmq.entity.KafkaSendResultHandler;
import com.hjmos.springbootrocketmq.service.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaSenderImpl implements KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaSendResultHandler kafkaSendResultHandler;

    @Override
    public void send(String topic,Integer partition,String key,String message){
        sendToKafka(topic,partition,key,message);
    }

    //发送消息方法
    private void sendToKafka(String topic,Integer partition,String key,String message) {

        kafkaTemplate.setProducerListener(kafkaSendResultHandler);
//        kafkaTemplate.send(topic, message);
        kafkaTemplate.send(topic,2,"33",message);
////        kafkaTemplate.send(topic,"20",message);
//        kafkaTemplate.send(topic,40,"aa",message);




    }
}
