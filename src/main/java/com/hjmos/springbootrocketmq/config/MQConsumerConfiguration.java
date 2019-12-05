package com.hjmos.springbootrocketmq.config;

import com.hjmos.springbootrocketmq.consumer.MQConsumeMsgListenerProcessor;
import com.hjmos.springbootrocketmq.entity.RocketMQProperties;
import com.hjmos.springbootrocketmq.exception.MqSendException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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

    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() throws MqSendException {
        if (StringUtils.isEmpty(rocketMQProperties.getConsumerList())){
            throw new MqSendException("groupName is null !!!");
        }
        List<Map<String, Object>> consumerList = rocketMQProperties.getConsumerList();
        Map<String, Object> consumerProperties = consumerList.get(0);
        if (StringUtils.isEmpty(consumerProperties.get("namesrvAddr"))){
            throw new MqSendException("namesrvAddr is null !!!");
        }
        if(StringUtils.isEmpty(consumerProperties.get("topic"))){
            throw new MqSendException("topics is null !!!");
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerProperties.get("groupName").toString());
        consumer.setNamesrvAddr(consumerProperties.get("namesrvAddr").toString());
        consumer.setConsumeThreadMin((Integer) consumerProperties.get("consumeThreadMin"));
        consumer.setConsumeThreadMax((Integer) consumerProperties.get("consumeThreadMax"));
        consumer.registerMessageListener(mqMessageListenerProcessor);

        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        //设置消费模型，集群还是广播，默认为集群
        //consumer.setMessageModel(MessageModel.CLUSTERING);

        // 设置一次消费消息的条数，默认为1条
        consumer.setConsumeMessageBatchMaxSize((Integer) consumerProperties.get("consumerBatchMaxSize"));
        try {
            /**
             * 设置该消费者订阅的主题和tag，如果是订阅该主题下的所有tag，则tag使用*；如果需要指定订阅该主题下的某些tag，则使用||分割，例如tag1||tag2||tag3
             */
            String[] topicTagsArr = consumerProperties.get("topic").toString().split(";");
            for (String topicTags : topicTagsArr) {
                String[] topicTag = topicTags.split("~");
                consumer.subscribe(topicTag[0],topicTag[1]);
            }
            consumer.start();
            log.info("consumer is start !!! groupName:{},topics:{},namesrvAddr:{}");
        }catch (MQClientException e){
            log.error("consumer is start !!! groupName:{},topics:{},namesrvAddr:{}",e);
            throw new MqSendException(e);
        }
        return consumer;
    }
}