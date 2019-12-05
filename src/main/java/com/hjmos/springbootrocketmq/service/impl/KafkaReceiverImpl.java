package com.hjmos.springbootrocketmq.service.impl;

import com.hjmos.springbootrocketmq.service.KafkaReceiver;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaReceiverImpl implements KafkaReceiver {

//    @KafkaListener(topics = {"my-kafka"}, groupId = "test-consumer-group")
    public void listen01(ConsumerRecord<?, ?> record) {
        log.info("消费者01开始消费------------------------------");
        log.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            log.info("record={},message={}",record,message);
//        }

        log.info("消费者01结束消费------------------------------");

    }

    @KafkaListener(groupId = "test-consumer-group",
            topicPartitions = {
                    @TopicPartition(topic = "my-kafka", partitions = {"1", "2"}),
                    @TopicPartition(topic = "kafka-test", partitions = {"2", "4"})
            }
    )
    public void listen02(ConsumerRecord<?, ?> record) {
        log.info("消费者02开始消费------------------------------");
        log.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            log.info("record={},message={}",record,message);
//        }
        log.info("消费者02结束消费------------------------------");

    }


}
