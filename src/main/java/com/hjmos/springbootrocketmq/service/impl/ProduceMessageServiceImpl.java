package com.hjmos.springbootrocketmq.service.impl;

import com.hjmos.springbootrocketmq.annotation.ProduceMessage;
import com.hjmos.springbootrocketmq.enums.TransactionEnum;
import com.hjmos.springbootrocketmq.exception.MqSendException;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProduceMessageServiceImpl implements ProduceMessageService {
    @Autowired
    private DefaultMQProducer defaultProducer;

    @Autowired
    private TransactionMQProducer transactionProducer;

    private RocketSendCallback rocketSendCallback = new RocketSendCallback();

    /**
     * 发送消息的总入口
     * TODO 逻辑需要写完
     * @param produceMessage
     */
    public void produceMessage(ProduceMessage produceMessage) {
        TransactionEnum transaction = produceMessage.transaction();
        //sendOneWay(produceMessage.topic(),produceMessage.tag(),produceMessage.keys(),produceMessage.content());
        sendAsyncDefault(produceMessage.topic(),produceMessage.tag(),produceMessage.keys(),produceMessage.content());
    }

    /**
     * 单边发送
     *
     * @param topic
     * @param tag
     * @param keys
     * @param content
     */
    private void sendOneWay(String topic, String tag, String keys, String content) {
        try {
            Message msg = new Message(topic, tag, keys, content.getBytes());
            defaultProducer.sendOneway(msg);
           defaultProducer.send(msg, (queues, message, queNum) -> {
                int queueNum = Integer.parseInt(queNum.toString());
                return queues.get(queueNum);
            },0);
            this.logMsg(msg);
        } catch (Exception e) {
            log.error("单边发送消息失败", e);
            throw new MqSendException(e);
        }
    }
    /**
     * 异步发送 默认回调函数
     *
     * @param topic
     * @param tag
     * @param keys
     * @param content
     */
    public void sendAsyncDefault(String topic, String tag, String keys, String content) {
        Message msg = new Message(topic, tag, keys, content.getBytes());
        try {
            defaultProducer.send(msg, rocketSendCallback);
        } catch (Exception e) {
            log.error("异步发送消息失败", e);
            throw new MqSendException(e);
        }
        this.logMsg(msg);
    }

    class RocketSendCallback implements SendCallback {

        @Override
        public void onSuccess(SendResult sendResult) {
            log.info("send message success. topic=" + sendResult.getMessageQueue().getTopic() + ", msgId=" + sendResult.getMsgId());
        }

        @Override
        public void onException(Throwable e) {
            log.error("send message failed.", e);
        }
    }



    /**
     * 发送普通消息
     */
    private SendResult sendMsg(String topic, String tags, String keys, String content) {
        Message msg = new Message(topic, tags, keys, content.getBytes());
        try {
            SendResult result = defaultProducer.send(msg);
            this.logMsg(msg, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("同步发送普通消息失败", e);
            throw new MqSendException(e);
        }
    }

    /**
     * 发送事务消息
     *
     */
    public SendResult sendTransactionMsg(String topic, String tags, String keys, String content) {
        Message msg = new Message(topic, tags, keys, content.getBytes());
        try {
            SendResult sendResult = transactionProducer.sendMessageInTransaction(msg, null);
            this.logMsg(msg, sendResult);
            return sendResult;
        } catch (Exception e) {
            log.error("同步发送事务消息失败", e);
            throw new MqSendException(e);
        }


    }

    /**
     * 支持顺序发送消息
     */
    private SendResult sendMsgOrder(String topic, String tags, String keys, String content, int orderId) {
        Message msg = new Message(topic, tags, keys, content.getBytes());
        try {
            SendResult sendResult = defaultProducer.send(msg, (List<MessageQueue> mqs, Message message, Object arg) -> {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                    , orderId);
            this.logMsg(msg, sendResult);
            return sendResult;
        } catch (Exception e) {
            log.error("有顺序发送消息失败", e);
            throw new MqSendException(e);
        }
    }


    /**
     * 打印消息topic等参数方便后续查找问题
     */
    private void logMsg(Message message) {
        log.info("消息队列发送完成:topic={},tag={},msgId={}", message.getTopic(), message.getTags(), message.getKeys());
    }

    /**
     * 打印消息topic等参数方便后续查找问题
     */
    private void logMsg(Message message, SendResult sendResult) {
        log.info("消息队列发送完成:topic={},tag={},msgId={},sendResult={}", message.getTopic(), message.getTags(), message.getKeys(), sendResult);
    }
}
