package com.pci.hjmos.kafka.service.impl;

import com.pci.hjmos.util.entity.KafkaProduceMessage;
import com.pci.hjmos.kafka.service.KafkaProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class KafkaProduceMessageServiceImpl implements KafkaProduceMessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Override
    public boolean produceMessage(KafkaProduceMessage produceMessage) {
        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(produceMessage.getTopic(), produceMessage.getContent());
        try {
            SendResult<String, String> stringStringSendResult = send.get();
            log.info("发送消息成功"+stringStringSendResult.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean produceBatchMessage(List<KafkaProduceMessage> produceMessages) {
        for (KafkaProduceMessage produceMessage : produceMessages) {
            ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(produceMessage.getTopic(), produceMessage.getContent());
        }
        return false;
    }
}
