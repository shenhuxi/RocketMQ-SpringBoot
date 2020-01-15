package com.pci.hjmos.util.entity;

import java.io.Serializable;

public class Result implements Serializable {
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
    ResultData content;

    public Result(int code, String msg, ResultData content) {
        this.code = code;
        this.msg = msg;
        this.content = content;
    }
}
