package com.hjmos.springbootrocketmq.service;

import com.hjmos.springbootrocketmq.entity.KafkaProduceMessage;
import com.hjmos.springbootrocketmq.entity.RocketProduceMessage;

/**
 * @author yuyang
 * 生产消息的服务接口
 */
public interface KafkaProduceMessageService {
    /**
     * 生产消息的唯一对外服务入口
     * @param produceMessage
     * @return
     */
    boolean produceMessage(KafkaProduceMessage produceMessage);
}