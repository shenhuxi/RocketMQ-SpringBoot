package com.hjmos.springbootrocketmq.annotation;

import com.hjmos.springbootrocketmq.enums.TransactionEnum;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.lang.annotation.*;

/**
 * 发送消息
 * @author yuyang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Documented
public @interface ProduceMessage {
    String value() default "";
    /**
     * 主题
     * @return
     */
    @NotBlank
    String topic();

    /**
     * 标签
     * @return
     */
    @NotBlank
    String tag();

    /**
     * 消息内容
     * @return
     */
    @NotBlank
    String content();

    /**
     *
     * @return
     */
    String keys();

    /**
     * 支持顺序
     * @return
     */
    int orderId();

    /**
     * 是否使用事务发送
     * @return
     */
    TransactionEnum transaction();





}
