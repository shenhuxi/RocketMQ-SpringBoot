package com.pci.hjmos.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yuyang
 * 读取配置文件信息
 */
@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {
    private String namesrvAddr;
    private String producerGroupName;
    private String transactionProducerGroupName;
    private String producerInstanceName;
    private String producerTranInstanceName;
    private boolean enableHistoryConsumer;
    private List<Map<String,Object>> consumerList = new ArrayList<>();

}
