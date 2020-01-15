package com.pci.hjmos.kafka.service.impl;

import com.pci.hjmos.api.produce.MQcallback;
import com.pci.hjmos.api.produce.ProduceMessageService;
import com.pci.hjmos.util.constant.CodeConstant;
import com.pci.hjmos.util.constant.MsgConstant;
import com.pci.hjmos.util.entity.Result;
import com.pci.hjmos.util.entity.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.rocketmq.client.producer.SendCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
public class KafkaProduceMessageServiceImpl implements ProduceMessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private Producer<String, String> producer;
    /**
     * 发送同步消息
     *
     * @param topic   消息主题
     * @param content 消息内容
     * @return 消息发送结果
     */
    @Override
    public Result sendSyncMsg(String topic, String content) throws Exception {
        ResultData data ;
        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(topic, content);
        data = new ResultData(topic, content,send.get().getProducerRecord().toString());
        return new Result(CodeConstant.RETCODE_200, MsgConstant.SUCCESS,data);
    }

    /**
     * 发送同步消息
     *
     * @param topic    消息主题
     * @param content  消息内容
     * @param callback 回调方法对象
     */
    @Override
    public void sendAsyncMsg(String topic, String content, MQcallback callback) throws Exception {
        producer.send(new ProducerRecord(topic,callback),new MQcallback(){
            @Override
            public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {

            }

            @Override
            public void onException(Throwable e) {

            }

            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                log.info("异步发送成功："+recordMetadata.toString());
            }
        });
    }
}
