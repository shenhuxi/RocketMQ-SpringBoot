package com.hjmos.springbootrocketmq.consumer;

import java.util.List;


import com.hjmos.springbootrocketmq.bean.MessageEvent;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听消息进行消费
 */
@Component
public class ConsumerService {
    @EventListener(condition = "#event.msgs[0].topic=='user-topic' && #event.msgs[0].tags=='white'")
    public void rocketmqMsgListener(MessageEvent event) {
        try {
            List<MessageExt> msgs = event.getMsgs();
            for (MessageExt msg : msgs) {
                System.err.println("线程："+Thread.currentThread().getName()+"，消费消息:"+new String(msg.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventListener(condition = "#event.msgs[0].topic=='user-topic2'")
    public void rocketmqMsgListener2(MessageEvent event) {
        try {
            List<MessageExt> msgs = event.getMsgs();
            for (MessageExt msg : msgs) {
                System.err.println("线程："+Thread.currentThread().getName()+"，消费主题user-topic2消息:"+new String(msg.getBody())+ "--ID："+msg.getMsgId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
