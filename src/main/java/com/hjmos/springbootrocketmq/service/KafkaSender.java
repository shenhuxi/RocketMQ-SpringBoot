package com.hjmos.springbootrocketmq.service;

public interface KafkaSender {
    void send(String topic,Integer partition,String key,String message);
}
