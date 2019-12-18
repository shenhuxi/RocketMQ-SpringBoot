package com.hjmos.springbootrocketmq.service.impl;

import com.hjmos.springbootrocketmq.entity.KafkaProduceMessage;
import com.hjmos.springbootrocketmq.service.KafkaProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class KafkaProduceMessageServiceImpl implements KafkaProduceMessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateOther;
    @Override
    public boolean produceMessage(KafkaProduceMessage produceMessage) {
        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(produceMessage.getTopic(), produceMessage.getContent());
        try {
            SendResult<String, String> stringStringSendResult = send.get();
            log.info("kafkaTemplate发送消息成功"+stringStringSendResult.toString());
        } catch (InterruptedException |ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean produceMessageOther(KafkaProduceMessage produceMessage) {
        ListenableFuture<SendResult<String, String>> send = kafkaTemplateOther.send(produceMessage.getTopic(), produceMessage.getContent());
        try {
            SendResult<String, String> stringStringSendResult = send.get();
            log.info("kafkaTemplateOther 发送消息成功"+stringStringSendResult.toString());
        } catch (InterruptedException |ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }
}
