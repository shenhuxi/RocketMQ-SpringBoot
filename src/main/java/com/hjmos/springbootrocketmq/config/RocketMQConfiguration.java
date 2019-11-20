package com.hjmos.springbootrocketmq.config;

import javax.annotation.PostConstruct;

import com.hjmos.springbootrocketmq.bean.MessageEvent;
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
import java.util.stream.Collectors;

/**
 * @author yuyang
 * <p>
 * 通过使用指定的文件读取类 来加载配置文件到字段中
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties01.class)
@Slf4j
public class RocketMQConfiguration {

    @Autowired
    private RocketMQProperties01 rocketMQProperties01;
    @Autowired
    private RocketMQProperties rocketMQProperties;

    //事件监听
    @Autowired
    private ApplicationEventPublisher publisher = null;

    private static boolean isFirstSub = true;

    private static long startTime = System.currentTimeMillis();

    /**
     * 容器初始化的时候 打印参数
     */
    @PostConstruct
    public void init() {
        System.err.println(rocketMQProperties01.getNamesrvAddr());
        System.err.println(rocketMQProperties01.getProducerGroupName());
        System.err.println(rocketMQProperties01.getConsumerBatchMaxSize());
        System.err.println(rocketMQProperties01.getConsumerGroupName());
        System.err.println(rocketMQProperties01.getConsumerInstanceName());
        System.err.println(rocketMQProperties01.getProducerInstanceName());
        System.err.println(rocketMQProperties01.getProducerTranInstanceName());
        System.err.println(rocketMQProperties01.getTransactionProducerGroupName());
        System.err.println(rocketMQProperties01.isConsumerBroadcasting());
        System.err.println(rocketMQProperties01.isEnableHistoryConsumer());
        System.err.println(rocketMQProperties01.isEnableOrderConsumer());
        System.out.println(rocketMQProperties01.getSubscribe().get(0));

        System.out.println(rocketMQProperties.getConsumerGroupName());
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
                rocketMQProperties01.getProducerGroupName());
        producer.setNamesrvAddr(rocketMQProperties01.getNamesrvAddr());
        producer.setInstanceName(rocketMQProperties01.getProducerInstanceName());
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
                rocketMQProperties01.getTransactionProducerGroupName());
        producer.setNamesrvAddr(rocketMQProperties01.getNamesrvAddr());
        producer.setInstanceName(rocketMQProperties01
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

//    /**
//     * 创建支持消息事务发送的实例
//     * @param transactionListener
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public TransactionMQProducer transactionProducer(TransactionListener transactionListener) throws Exception {
//        TransactionMQProducer producer = new TransactionMQProducer(rocketMQProperties.getTransactionProducerGroupName());
//        producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
//        producer.setInstanceName(System.currentTimeMillis() + "");
//        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                Thread thread = new Thread(r);
//                thread.setName("client-transaction-msg-check-thread");
//                return thread;
//            }
//        });
//
//        producer.setExecutorService(executorService);
//        producer.setTransactionListener(transactionListener);
//        producer.start();
//        log.info("rocketmq transaction producer server is starting....");
//        return producer;
//    }



    /**
     * 创建消息消费的实例
     *
     * @return
     * @throws MQClientException
     */
    @Bean
    public DefaultMQPushConsumer pushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(
                rocketMQProperties01.getConsumerGroupName());
        consumer.setNamesrvAddr(rocketMQProperties01.getNamesrvAddr());
        consumer.setInstanceName(rocketMQProperties01.getConsumerInstanceName());

        //判断是否是广播模式
        if (rocketMQProperties01.isConsumerBroadcasting()) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }
        //设置批量消费
        consumer.setConsumeMessageBatchMaxSize(rocketMQProperties01
                .getConsumerBatchMaxSize() == 0 ? 1 : rocketMQProperties01
                .getConsumerBatchMaxSize());

        //获取topic和tag
        List<String> subscribeList = rocketMQProperties01.getSubscribe();
        for (String sunscribe : subscribeList) {
            consumer.subscribe(sunscribe.split(":")[0], sunscribe.split(":")[1]);
        }

        // 顺序消费
        if (rocketMQProperties01.isEnableOrderConsumer()) {
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
        }
        // 并发消费
        else {
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
        if (isFirstSub && !rocketMQProperties01.isEnableHistoryConsumer()) {
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

