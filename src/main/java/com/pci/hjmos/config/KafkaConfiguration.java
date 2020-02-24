package com.pci.hjmos.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfiguration {

    // 原来的方式
//    @Value("${spring.skafka.bootstrap-servers}")
//    private String innerServers;
//    @Value("${spring.skafka.consumer.group-id}")
//    private String innerGroupid;
//    @Value("${spring.skafka.consumer.enable-auto-commit}")
//    private String innerEnableAutoCommit;

    // 使用对象的方式
    @Autowired
    private KafkaProperties kafkaProperties;
    /**
     * 事件监听
     */
    @Autowired
    private ApplicationEventPublisher publisher = null;

    //------------------------------------------- 消费者消费配置 + 非注解监听消费方式 -------------------------------------------
    @Bean
    @Primary
     //理解为默认优先选择当前容器下的消费者工厂
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
    @Bean//第一个消费者工厂的bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        if(kafkaProperties.getBootstrapServers() == null){
            return null;
        }
        String innerServers = kafkaProperties.getBootstrapServers();
        String innerGroupid = (String) kafkaProperties.getConsumer().get("group-id");
        boolean innerEnableAutoCommit = (boolean) kafkaProperties.getConsumer().get("enable-auto-commit");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, innerServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, innerGroupid);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, innerEnableAutoCommit);
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    //非注解监听消费方式(采用spring的注解方式)
    /*@Bean
    public KafkaMessageListenerContainer demoListenerContainer() {
        ContainerProperties properties = new ContainerProperties("topic.quick.bean");
        properties.setGroupId("bean");
        properties.setMessageListener(new MessageListener<Integer,String>() {
            private Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void onMessage(ConsumerRecord<Integer, String> record) {
                publisher.publishEvent(new MessageEvent(record));
                log.info("topic.quick.bean receive : " + record.toString());
            }
        });
        return new KafkaMessageListenerContainer(consumerFactory(), properties);
    }*/
    //------------------------------------------- 消费者批量消费配置  -------------------------------------------
    @Bean
    public Map<String, Object> consumerConfigs2() {
        Map<String, Object> props = new HashMap<>();

        if(kafkaProperties.getBootstrapServers() == null){
            return null;
        }
        String innerServers = kafkaProperties.getBootstrapServers();
        String innerGroupid = (String) kafkaProperties.getConsumer().get("group-id");
        boolean innerEnableAutoCommit = (boolean) kafkaProperties.getConsumer().get("enable-auto-commit");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, innerServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, innerGroupid);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, innerEnableAutoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5); // 设置每次批量消费的数量
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    @Bean("batchContainerFactory")
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory2() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs2()));
        //设置并发量，小于或等于Topic的分区数
        factory.setConcurrency(2);
        //设置为批量监听
        factory.setBatchListener(true);  //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
    //------------------------------------------- 消费者手动确认  -------------------------------------------
    @Bean
    public Map<String, Object> consumerConfigs3() {
        Map<String, Object> props = new HashMap<>();
        if(kafkaProperties.getBootstrapServers() == null){
            return null;
        }
        String innerServers = kafkaProperties.getBootstrapServers();
        String innerGroupid = (String) kafkaProperties.getConsumer().get("group-id");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, innerServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, innerGroupid);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);   // 设置批量获取消息的最大值
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    @Bean("ackContainerFactory")
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory3() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs3()));
        //设置并发量，小于或等于Topic的分区数
        factory.setConcurrency(2);
        //设置为批量监听
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
    //------------------------------------------- 生产者配置  -------------------------------------------
//    private Map<String, Object> senderProps() {
//        Map<String, Object> props = new HashMap<>();
//
//        if(kafkaProperties.getBootstrapServers() == null){
//            return null;
//        }
//        String innerServers = kafkaProperties.getBootstrapServers();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, innerServers);
//        props.put(ProducerConfig.RETRIES_CONFIG, 0);
//        props.put(ProducerConfig.ACKS_CONFIG, "1");    // 必须设置为字符串格式
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        return props;
//    }
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(senderProps());
//    }
//
//    @Bean //kafka发送消息模板
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<String, String>(producerFactory());
//    }
//
//    @Bean //kafka生产者
//    public Producer<String, String> producer() {
//
//        if(kafkaProperties.getBootstrapServers() == null){
//            // 说明没有配置kafka信息
//            Map<String, Object> prop = null;
//            return new KafkaProducer<String, String>(prop);
//        }
//
//        return new KafkaProducer<String, String>(senderProps());
//    }
}
