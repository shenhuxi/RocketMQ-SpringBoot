package com.hjmos.springbootrocketmq.service.impl;

import com.hjmos.springbootrocketmq.entity.KafkaProduceMessage;
import com.hjmos.springbootrocketmq.service.KafkaProduceMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProduceMessageServiceImpl implements KafkaProduceMessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Override
    public boolean produceMessage(KafkaProduceMessage produceMessage) {
        kafkaTemplate.send(produceMessage.getTopic(), produceMessage.getContent());
        return true;
    }
}
