package com.hjmos.springbootrocketmq.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaReceiver {
	@KafkaListener(topics = {"T6"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----Topic_user------------- record =" + record+" message =" + message);
        }
    }
    @KafkaListener(topics = {"wordCountOutput"})
    public void listenwordCountOutput(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----wordCountOutput------------- record =" + record+";value="+record.value()+" ;message =" + message);
        }
    }
    @KafkaListener(topics = {"streams-plaintext-input"})
    public void wordCountCountsStoreChangelog(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----streams-plaintext-input------------- record =" + record+" message =" + message);
        }
    }

    @KafkaListener(topics = {"streams-pipe-output"})
    public void wordCountCountsStoreRepartition(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----streams-pipe-output------------- record =" + record+" message =" + message);
        }
    }
}

