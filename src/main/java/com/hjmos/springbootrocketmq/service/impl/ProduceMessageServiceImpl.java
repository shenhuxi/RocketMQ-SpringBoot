package com.hjmos.springbootrocketmq.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.entity.ProduceMessage;
import com.hjmos.springbootrocketmq.exception.MqSendException;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hjmos.springbootrocketmq.producer.TransactionExecuterImpl;

/**
 * @author yuyang
 * 生产消息的服务实现
 */
@Slf4j
@Service
public class ProduceMessageServiceImpl implements ProduceMessageService {
    @Autowired
    private DefaultMQProducer defaultProducer;

    @Autowired
    private TransactionMQProducer transactionProducer;

    private RocketSendCallback rocketSendCallback = new RocketSendCallback();


    /**
     * 发送消息的方法入口
     *
     * @param produceMessage
     * @return
     */
    @Override
    public boolean produceMessage(ProduceMessage produceMessage) {
        return produceMessageCore(produceMessage);
    }

    @Override
    public boolean transactionMQ(ProduceMessage produceMessage) throws MQClientException {
        @NotBlank String topic = produceMessage.getTopic();
        @NotBlank String content = produceMessage.getContent();
        @NotBlank String tag = produceMessage.getTag();
        Message msg = new Message(topic, tag, content.getBytes());
        SendResult sendResult = transactionProducer.sendMessageInTransaction(msg, new TransactionExecuterImpl(),"tq");
        this.logMsg(msg, sendResult);
        return true;
    }

    /**
     * 组装消息的核心方法
     * TODO 暂时实现普通的方式
     *
     * @return
     */
    private boolean produceMessageCore(ProduceMessage produceMessage) {
        @NotBlank String topic = produceMessage.getTopic();
        @NotBlank String content = produceMessage.getContent();
        @NotBlank String tag = produceMessage.getTag();
        String keys = produceMessage.getKeys();
//        if (!verifyJson(content)) {
//            throw new MqSendException("消息内容不是json格式");
//        }

        try {
            sendMsg(topic, tag, keys, content);
        } catch (Exception e) {
            log.error("发送普通消息失败", e);
            throw new MqSendException(e);
        }
        return true;
    }

    /**
     * 校验json字符串
     *
     * @param json
     */
    private boolean verifyJson(String json) {
        try {
            JSONObject.parseObject(json);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    /**
     * 单边发送
     *
     * @param topic
     * @param tag
     * @param keys
     * @param content
     */
    private void sendOneWay(String topic, String tag, String keys, String content) throws Exception {
        Message msg = new Message(topic, tag, keys, content.getBytes());
        defaultProducer.sendOneway(msg);
        defaultProducer.send(msg, (queues, message, queNum) -> {
            int queueNum = Integer.parseInt(queNum.toString());
            return queues.get(queueNum);
        }, 0);
        this.logMsg(msg);
    }

    /**
     * 异步发送 默认回调函数
     *
     * @param topic
     * @param tag
     * @param keys
     * @param content
     */
    private void sendAsyncDefault(String topic, String tag, String keys, String content) throws Exception {
        Message msg = new Message(topic, tag, keys, content.getBytes());
        defaultProducer.send(msg, rocketSendCallback);
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
    private SendResult sendMsg(String topic, String tags, String keys, String content) throws Exception {
        Message msg = new Message(topic, tags, keys, content.getBytes());
        SendResult result = defaultProducer.send(msg);
        //this.logMsg(msg, result);
        return result;
    }

    /**
     * 发送事务消息
     */
    private SendResult sendTransactionMsg(String topic, String tags, String keys, String content) throws Exception {
        Message msg = new Message(topic, tags, keys, content.getBytes());
        SendResult sendResult = transactionProducer.sendMessageInTransaction(msg, null);
        this.logMsg(msg, sendResult);
        return sendResult;
    }

    /**
     * 支持顺序发送消息
     */
    private SendResult sendMsgOrder(String topic, String tags, String keys, String content, int orderId) throws Exception {
        Message msg = new Message(topic, tags, keys, content.getBytes());
        SendResult sendResult = defaultProducer.send(msg, (List<MessageQueue> mqs, Message message, Object arg) -> {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
                , orderId);
        //this.logMsg(msg, sendResult);
        return sendResult;
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
