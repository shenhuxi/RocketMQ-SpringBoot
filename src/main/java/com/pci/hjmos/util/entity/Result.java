package com.pci.hjmos.util.entity;


public class Result {
    /**
     * 状态码 200：成功，500：失败
     */
    int  code;

    /**
     * 提示信息
     */
    String msg ;

    /**
     * 消息反馈
     */
    SendResult content;
}
