package com.hjmos.springbootrocketmq.config;
import com.hjmos.springbootrocketmq.consumer.MQConsumeMsgListenerProcessor;
import com.hjmos.springbootrocketmq.entity.RocketMQProperties;
import com.hjmos.springbootrocketmq.exception.MqSendException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.clouds.common.rocketmq.constants.RocketMQErrorEnum;
import com.clouds.common.rocketmq.consumer.processor.MQConsumeMsgListenerProcessor;
import com.clouds.common.rocketmq.exception.RocketMQException;

import java.util.List;
import java.util.Map;


/**
 * 消费者Bean配置
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@Slf4j
public class MQConsumerConfiguration {
    @Autowired
    private RocketMQProperties rocketMQProperties;
    @Autowired
    private MQConsumeMsgListenerProcessor mqMessageListenerProcessor;

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
        return consumer;
    }


    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() throws MqSendException {
        if (StringUtils.isEmpty(rocketMQProperties.getConsumerList())){
            throw new MqSendException("groupName is null !!!");
        }
        if (StringUtils.isEmpty(namesrvAddr)){
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL,"namesrvAddr is null !!!",false);
        }
        if(StringUtils.isEmpty(topics)){
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL,"topics is null !!!",false);
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.registerMessageListener(mqMessageListenerProcessor);
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        /**
         * 设置消费模型，集群还是广播，默认为集群
         */
        //consumer.setMessageModel(MessageModel.CLUSTERING);
        /**
         * 设置一次消费消息的条数，默认为1条
         */
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        try {
            /**
             * 设置该消费者订阅的主题和tag，如果是订阅该主题下的所有tag，则tag使用*；如果需要指定订阅该主题下的某些tag，则使用||分割，例如tag1||tag2||tag3
             */
            String[] topicTagsArr = topics.split(";");
            for (String topicTags : topicTagsArr) {
                String[] topicTag = topicTags.split("~");
                consumer.subscribe(topicTag[0],topicTag[1]);
            }
            consumer.start();
            log.info("consumer is start !!! groupName:{},topics:{},namesrvAddr:{}",groupName,topics,namesrvAddr);
        }catch (MQClientException e){
            log.error("consumer is start !!! groupName:{},topics:{},namesrvAddr:{}",groupName,topics,namesrvAddr,e);
            throw new MqSendException(e);
        }
        return consumer;
    }
}