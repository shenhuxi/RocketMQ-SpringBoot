package com.pci.hjmos.util.bean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.List;
/**
 * 监听
 * @author yuyang
 *
 */
public class MessageEvent extends ApplicationEvent {
    private static final long serialVersionUID = -4468405250074063206L;
    private ConsumerRecord msgs;

    public MessageEvent(ConsumerRecord msgs) {
        super(msgs);
        this.setMsgs(msgs);
    }

    public ConsumerRecord getMsgs() {
        return msgs;
    }

    public void setMsgs(ConsumerRecord msgs) {
        this.msgs = msgs;
    }
}
