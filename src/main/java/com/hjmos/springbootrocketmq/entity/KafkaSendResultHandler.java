package com.hjmos.springbootrocketmq.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * 生产者消息回调
 */
@Component
@Slf4j
public class KafkaSendResultHandler implements ProducerListener {
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
//        log.info("Message send success : " + producerRecord.toString());
        log.info("数据所在分区：partition = {},数据指针: offset = {}",recordMetadata.partition(),recordMetadata.offset());
    }

    @Override
    public void onError(ProducerRecord producerRecord, Exception exception) {
        log.info("Message send error : " + producerRecord.toString());
    }
}
