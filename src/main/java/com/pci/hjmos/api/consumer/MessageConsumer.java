package com.pci.hjmos.api.consumer;

/**
 * 消费者信息接口
 * @param <T>
 */
public interface MessageConsumer<T> {
    /**
     * @return 消息主题
     */
    public String getTopic();
    /**
     * @return 消费者组
     */
    public String getGroup();
    /**
     * 执行消费方法
     */
    public void handle(T msg);

}
