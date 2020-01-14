package com.pci.hjmos.util.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author zengpeng
 * 封装消息实体
 */
@Getter
@Setter
@ToString
public class SendResult {
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

    public SendResult(@NotBlank String topic,  @NotBlank String content) {
        this.topic = topic;
        this.content = content;
    }

    public SendResult() {
    }
}
