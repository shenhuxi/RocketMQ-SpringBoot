package com.pci.hjmos.kafka.service.impl;

import com.pci.hjmos.api.produce.ProduceMessageService;
import com.pci.hjmos.util.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProduceMessageServiceImpl implements ProduceMessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    /**
     * 发送同步消息
     *
     * @param topic   消息主题
     * @param content 消息内容
     * @return 消息发送结果
     */
    @Override
    public Result sendSyncMsg(String topic, String content) throws Exception {
        return null;
    }

    /**
     * 发送同步消息
     *
     * @param topic    消息主题
     * @param content  消息内容
     * @param callback 回调方法对象
     */
    @Override
    public void sendAsyncMsg(String topic, String content, SendCallback callback) throws Exception {

    }
}
