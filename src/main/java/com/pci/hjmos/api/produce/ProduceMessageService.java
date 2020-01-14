package com.hjmos.springbootrocketmq.api.produce;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * 生产消息的服务接口
 * @author zengpeng
 */
public interface ProduceMessageService {
    /**
     * 发送同步消息
     * @param topic 消息主题
     * @param content 消息内容
     */
    SendResult sendSyncMsg(String topic,String content) throws Exception;
    /**
     * 发送同步消息
     * @param topic 消息主题
     * @param content 消息内容
     * @param callback 回调方法对象
     */
    void sendAsyncMsg(String topic, String content, SendCallback callback) throws Exception;

}
