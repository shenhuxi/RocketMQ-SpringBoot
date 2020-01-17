package com.pci.hjmos.util.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zengpeng
 * 封装消息实体
 */
@Getter
@Setter
@ToString
public class ResultData implements Serializable {
    /**
     * 主题
     */
    @NotBlank
    private String topic;
    /**
     * 消息内容
     */
    @NotBlank
    private String content;

    private String offsetMsgId;

    public ResultData(@NotBlank String topic, @NotBlank String content) {
        this.topic = topic;
        this.content = content;
    }

    public ResultData(@NotBlank String topic, @NotBlank String content, String offsetMsgId) {
        this.topic = topic;
        this.content = content;
        this.offsetMsgId = offsetMsgId;
    }

    public ResultData() {
    }
}
