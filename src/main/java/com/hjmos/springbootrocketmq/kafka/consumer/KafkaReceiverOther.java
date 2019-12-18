package com.hjmos.springbootrocketmq.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaReceiverOther {
	@KafkaListener(topics = {"T-Other"},groupId = "user",containerFactory = "kafkaListenerContainerFactoryOther")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"->user组 kafka消费主题【"+record.topic()+" 分区："+record.partition()+ "】"+"record =" + record+"+ message =" + message);
        }
    }

}

