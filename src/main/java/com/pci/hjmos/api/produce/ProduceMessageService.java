package com.pci.hjmos.api.produce;

import com.pci.hjmos.util.entity.Result;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Component;

/**
 * 生产消息的服务接口
 * @author zengpeng
 */
@Component
public interface ProduceMessageService {

    /**
     * 发送同步消息
     * @param topic 消息主题
     * @param content 消息内容
     */
    Result sendSyncMsg(String topic, String content) throws Exception;

    /**
     * 发送异步消息
     * @param topic 消息主题
     * @param content 消息内容
     * @param callback 回调方法对象
     */
    void sendAsyncMsg(String topic, String content, MQCallback callback) throws Exception;

    /**
     * 发送单向消息
     * @param topic  消息主题
     * @param content  消息内容
     */
    void sendOneWayMsg(String topic,String content);

}
