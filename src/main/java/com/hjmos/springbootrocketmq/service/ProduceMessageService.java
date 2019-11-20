package com.hjmos.springbootrocketmq.service;

import com.hjmos.springbootrocketmq.annotation.ProduceMessage;

public interface ProduceMessageService {
    /**
     * 发送消息的总入口
     *
     * @param annotation
     */
    void produceMessage(ProduceMessage annotation);
}
