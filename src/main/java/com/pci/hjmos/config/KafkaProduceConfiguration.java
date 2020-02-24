package com.pci.hjmos.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaProduceConfiguration {

    // 使用对象的方式
    @Autowired
    private KafkaProperties kafkaProperties;

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();

        String innerServers = kafkaProperties.getBootstrapServers();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, innerServers);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.ACKS_CONFIG, "1");    // 必须设置为字符串格式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    @Bean
    @ConditionalOnExpression("${mq.enabled}==1&&${mq.kafka.enabled}==1")
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    @Bean //kafka发送消息模板
    @ConditionalOnExpression("${mq.enabled}==1&&${mq.kafka.enabled}==1")
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }

    @Bean //kafka生产者
    @ConditionalOnExpression("${mq.enabled}==1&&${mq.kafka.enabled}==1")
    public Producer<String, String> producer() {
        log.info("初始化kafka生产者........");
        if(kafkaProperties.getBootstrapServers() == null){
            // 说明没有配置kafka信息
            Map<String, Object> props = new HashMap<>();
            return new KafkaProducer<String, String>(props);
        }

        return new KafkaProducer<String, String>(senderProps());
    }

}
