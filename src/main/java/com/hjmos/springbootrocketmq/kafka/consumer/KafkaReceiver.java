package com.hjmos.springbootrocketmq.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class KafkaReceiver {
    //------------------- 单个消费 -------------------
	@KafkaListener(topics = {"T6"},groupId = "user")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"->user组 kafka消费主题【"+record.topic()+" 分区："+record.partition()+ "】"+"record =" + record+"+ message =" + message);
        }
    }
    //------------------- 批量消费+监听分区消费 -------------------
    @KafkaListener(id = "batch",clientIdPrefix = "batch",containerFactory = "batchContainerFactory",topicPartitions = {
            @TopicPartition(topic = "T.batch",partitions = {"1"})})//初始消费位置,partitionOffsets = @PartitionOffset(partition = "0",initialOffset = "8")
    public void batchListener(List<ConsumerRecord<?, ?>> data) {
        log.info("主题：T.batch分区:"+data.get(0).partition()+";接收到的消息数 : "+data.size());
        for (ConsumerRecord s : data) {
            log.info(s.value().toString());
        }
    }
    //------------------- 手动确认消费 -------------------
    @KafkaListener(id = "ackListener",clientIdPrefix = "T.ack",containerFactory = "ackContainerFactory",topicPartitions = {
            @TopicPartition(topic = "T.ack",partitions = {"1"})})
    public void ackListener(List<ConsumerRecord<?, ?>> data, Acknowledgment ack) {
        log.info("主题：T.ack，分区:"+data.get(0).partition()+";接收到的消息数 : "+data.size());
        for (ConsumerRecord s : data) {
            log.info(s.value().toString());
        }
        //ack.acknowledge();//如果注释掉每次启动都会重新消费，应为所有消息没有确认
        //重新定位到该数据的偏移量上，重复消费
        //consumer.seek(new TopicPartition("topic.quick.ack",record.partition()),record.offset() );
    }
    //------------------- 分组消费，组内负载 -------------------
    @KafkaListener(topics = {"T5"},groupId = "order",id = "order001")
    public void listen2(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"->order组[order001] kafka消费主题【"+record.topic()+" 分区："+record.partition()+ "】"+"record =" + record+"+ message =" + message);
        }
    }
    @KafkaListener(topics = {"T5"},groupId = "order",id = "order002")
    public void listen3(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("线程="+Thread.currentThread()+"->order组[order002] kafka消费主题【"+record.topic()+" 分区："+record.partition()+ "】"+"record =" + record+"+ message =" + message);
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

