package com.hjmos.springbootrocketmq.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaReceiver {
	@KafkaListener(topics = {"T123"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----Topic_user------------- record =" + record+" message =" + message);
        }
    }
    //--------------------------------------- 主题转发 star---------------------------------------
    //@KafkaListener(topics = {"streams-plaintext-input"})
    public void wordCountCountsStoreChangelog(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----streams-plaintext-input------------- record =" + record+" message =" + message);
        }
    }

   // @KafkaListener(topics = {"streams-pipe-output"})
    public void wordCountCountsStoreRepartition(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题----streams-pipe-output------------- record =" + record+" message =" + message);
        }
    }
    //--------------------------------------- 主题分割单词 star---------------------------------------
    //@KafkaListener(topics = {"streams-linesplit-input"})
    public void linesplitInput(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题[streams-linesplit-input]--record =" + record+" message =" + message);
        }
    }
   // @KafkaListener(topics = {"streams-linesplit-output"})
    public void linesplitOutPut(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题[streams-linesplit-output]--record =" + record+" message =" + message);
        }
    }
    //--------------------------------------- 主题分单词统计聚合 star---------------------------------------
    //@KafkaListener(topics = {"streams-wordcount-output"})
    public void wordCountOutPut(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"-> kafka消费主题[streams-wordcount-output]--record =" + record+" message =" + message);
        }
    }


}

