package com.pci.hjmos.api.produce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

public abstract class MQCallback implements Callback , SendCallback{
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if(e==null) onSuccess(new SendResult());
        else onException( e);
    }
}
