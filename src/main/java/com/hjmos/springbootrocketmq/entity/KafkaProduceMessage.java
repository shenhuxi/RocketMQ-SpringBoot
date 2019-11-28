package com.hjmos.springbootrocketmq.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author yuyang
 * 封装消息实体
 */
@Getter
@Setter
@ToString
public class KafkaProduceMessage {
    /**
     * 主题
     * @return
     */
    @NotBlank
    private String topic;

    /**
     * 消息内容
     * @return
     */
    @NotBlank
    private String content;

    public KafkaProduceMessage(@NotBlank String topic, @NotBlank String content) {
        this.topic = topic;
        this.content = content;
    }

    public KafkaProduceMessage() {
    }
}
