package com.pci.hjmos.rocketmqservice.service;

import com.pci.hjmos.util.entity.ProduceMessage;
import org.apache.rocketmq.client.producer.SendResult;

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

    /**
     * 发送同步消息
     * @param produceMessage
     */
    SendResult sendSyncMsg(ProduceMessage produceMessage) throws Exception;

    void sendAsyncMsg(ProduceMessage produceMessage) throws Exception;

    void sendOneWayMsg(ProduceMessage produceMessage) throws Exception;

    SendResult sendTransactionMsg(ProduceMessage produceMessage) throws Exception;

    SendResult sendMsgOrder(ProduceMessage produceMessage,int orderId) throws Exception;

}
