package com.pci.hjmos.util.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Result {
    /**
     * code 状态码 200：成功，500：失败
     */
    public int  code;

    /**
     * 提示信息
     */
    public String msg ;

    /**
     * 消息反馈
     */
    public SendResult content;
}
