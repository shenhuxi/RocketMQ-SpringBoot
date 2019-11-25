package com.hjmos.springbootrocketmq.entity;

import lombok.Data;

@Data
public class SubwayOrder {
    private String userName;
    private String orderSerial;
    private long price;
}
