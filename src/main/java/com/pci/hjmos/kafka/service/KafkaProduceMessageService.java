package com.pci.hjmos.kafka.service;

import com.pci.hjmos.util.entity.KafkaProduceMessage;

import java.util.List;

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

    /**
     * 生产批量消息
     * @param produceMessage
     * @return
     */
    boolean produceBatchMessage(List<KafkaProduceMessage> produceMessage);
}
