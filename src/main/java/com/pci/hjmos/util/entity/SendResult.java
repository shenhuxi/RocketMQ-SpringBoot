package com.pci.hjmos.util.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author yuyang
 * 封装消息实体
 * ① 稳定性
 * ② 高可用
 * ③ 数据完整性
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
     * 标签
     * @return
     */
    @NotBlank
    private String tag;

    /**
     * 消息内容
     * @return
     */
    @NotBlank
    private String content;

    /**
     *
     * @return
     */
    private String keys;

    /**
     * 支持顺序
     * @return
     */
    private int orderId;

    public SendResult(@NotBlank String topic, @NotBlank String tag, @NotBlank String content) {
        this.topic = topic;
        this.tag = tag;
        this.content = content;
    }

    public SendResult() {
    }
}
