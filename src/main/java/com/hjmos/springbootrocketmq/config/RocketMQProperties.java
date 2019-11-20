package com.hjmos.springbootrocketmq.config;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@PropertySource("classpath:/application.yml")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "imc.rocketmq")
@Validated
@Setter
@Getter
@ToString
public class RocketMQProperties {

    @NotBlank
    private String namesrvAddr = "127.0.0.1";

    private List<ConsumerConfig> consumerList;

    private String producerGroupName;
    private String transactionProducerGroupName;
    private String consumerGroupName;
    private String producerInstanceName;
    private String consumerInstanceName;
    private String producerTranInstanceName;
    private int consumerBatchMaxSize;
    private boolean consumerBroadcasting;
    private boolean enableHistoryConsumer;
    private boolean enableOrderConsumer;
    private List<String> subscribe = new ArrayList<String>();


    @Data
    @Validated
    public static class ConsumerConfig {

        private boolean enable = false;

        private String name = UUID.randomUUID().toString();

        private String namesrvAddr;

        @NotBlank
        private String groupName;

        @NotBlank
        private String topic;

        private String tags = "*";

        private boolean order = false;

    }
}
