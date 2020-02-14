package com.pci.hjmos.kafka.consumer;

import com.pci.hjmos.api.produce.MessageConsumer;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer implements MessageConsumer<String> {
    /**
     * @return 消息主题
     */
    @Override
    public String getTopic() {
        return null;
    }

    /**
     * @return 消费者组
     */
    @Override
    public String getGroup() {
        return null;
    }

    /**
     * 执行消费方法
     * @param msg
     */
    @Override
    public void handle(String msg) {

    }
}
