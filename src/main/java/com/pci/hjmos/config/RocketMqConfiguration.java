package com.pci.hjmos.config;

import javax.annotation.PostConstruct;

import com.pci.hjmos.util.bean.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuyang
 * 加载配置信息并初始化
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@Slf4j
public class RocketMqConfiguration {

    @Autowired
    private RocketMQProperties rocketMQProperties;

    /**
     * 事件监听
     */
    @Autowired
    private ApplicationEventPublisher publisher = null;

    private static boolean isFirstSub = true;

    private static long startTime = System.currentTimeMillis();

    /**
     * 容器初始化的时候 打印参数
     */
    @PostConstruct
    public void init() {
    }

    /**
     * 创建普通消息发送者实例
     *
     * @return
     * @throws MQClientException
     */
    @Bean
    public DefaultMQProducer defaultProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(
                rocketMQProperties.getProducerGroupName());
        producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
        producer.setInstanceName(rocketMQProperties.getProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.start();
        log.info("rocketmq producer server is starting....");
        return producer;
    }

    /**
     * 创建支持消息事务发送的实例
     *
     * @return
     * @throws MQClientException
     */
    @Bean
    public TransactionMQProducer transactionProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(
                rocketMQProperties.getTransactionProducerGroupName());
        producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
        producer.setInstanceName(rocketMQProperties
                .getProducerTranInstanceName());
        producer.setRetryTimesWhenSendAsyncFailed(10);
        // 事务回查最小并发数
        producer.setCheckThreadPoolMinSize(2);
        // 事务回查最大并发数
        producer.setCheckThreadPoolMaxSize(2);
        // 队列数
        producer.setCheckRequestHoldMax(2000);
        producer.start();
        log.info("rocketmq transaction producer server is starting....");
        return producer;
    }

    /**
     * 初始化消费者
     */
    @Bean
    public void initConsumerInstance() {
        List<Map<String, Object>> consumerList = rocketMQProperties.getConsumerList();
        if (consumerList.size() > 0) {
            consumerList.stream().forEach(map -> {
                try {
                    pushConsumer(map);
                } catch (MQClientException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 创建消息消费的实例
     *
     * @return
     * @throws MQClientException
     */
    private DefaultMQPushConsumer pushConsumer(Map consumerConfig) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerConfig.get("groupName").toString());
        consumer.setNamesrvAddr(consumerConfig.get("namesrvAddr").toString());

        //判断是否是广播模式
        if (Boolean.parseBoolean(consumerConfig.get("consumerBroadCasting").toString())) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }
        //设置批量消费
        consumer.setConsumeMessageBatchMaxSize(Integer.parseInt(consumerConfig.get("consumerBatchMaxSize").toString()) == 0 ? 1 : Integer.parseInt(consumerConfig.get("consumerBatchMaxSize").toString()));

        //获取topic和tag
        consumer.subscribe(consumerConfig.get("topic").toString(), consumerConfig.get("tags").toString());


        if (Boolean.parseBoolean(consumerConfig.get("enableOrderConsumer").toString())) {
            // 顺序消费
            consumer.registerMessageListener(new MessageListenerOrderly() {
                @Override
                public ConsumeOrderlyStatus consumeMessage(
                        List<MessageExt> msgs, ConsumeOrderlyContext context) {
                    try {
                        context.setAutoCommit(true);
                        msgs = filterMessage(msgs);
                        if (msgs.size() == 0)
                            return ConsumeOrderlyStatus.SUCCESS;
                        publisher.publishEvent(new MessageEvent(msgs, consumer));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
        } else {
            //并发消费
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(
                        List<MessageExt> msgs,
                        ConsumeConcurrentlyContext context) {
                    try {
                        //过滤消息
                        msgs = filterMessage(msgs);
                        if (msgs.size() == 0)
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        publisher.publishEvent(new MessageEvent(msgs, consumer));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);

                    try {
                        consumer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    log.info("rocketmq consumer server is starting....");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        return consumer;
    }

    /**
     * 消息过滤
     *
     * @param msgs
     * @return
     */
    private List<MessageExt> filterMessage(List<MessageExt> msgs) {
        if (isFirstSub && !rocketMQProperties.isEnableHistoryConsumer()) {
            msgs = msgs.stream()
                    .filter(item -> startTime - item.getBornTimestamp() < 0)
                    .collect(Collectors.toList());
        }
        if (isFirstSub && msgs.size() > 0) {
            isFirstSub = false;
        }
        return msgs;
    }

}

