package com.hjmos.springbootrocketmq.service;

import com.hjmos.springbootrocketmq.entity.ProduceMessage;

/**
 * @author yuyang
 * 生产消息的服务接口
 */
public interface ProduceMessageService {
    /**
     * 生产消息的唯一对外服务入口
     * @param produceMessage
     * @return
     */
    boolean produceMessage(ProduceMessage produceMessage);
}
