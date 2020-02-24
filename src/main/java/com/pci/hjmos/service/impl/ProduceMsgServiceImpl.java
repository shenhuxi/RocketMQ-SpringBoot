package com.pci.hjmos.service.impl;

import com.pci.hjmos.api.produce.MQCallback;
import com.pci.hjmos.api.produce.ProduceMessageService;
import com.pci.hjmos.config.RocketMQProperties;
import com.pci.hjmos.util.constant.CodeConstant;
import com.pci.hjmos.util.constant.MsgConstant;
import com.pci.hjmos.util.entity.Result;
import com.pci.hjmos.util.entity.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
//@EnableConfigurationProperties(RocketMQProperties.class)
@Primary
public class ProduceMsgServiceImpl implements ProduceMessageService {

    @Autowired
    private DefaultMQProducer rocketMqProducer;

    @Autowired
    private Producer<String, String> kafkaProducer;

    // 直接从配置文件中获取
//    @Value("${rocketmq.namesrvAddr}")
//    private String namesrvAddr;

    // 先放在配置类中，从配置类中获取
    @Autowired
    private RocketMQProperties rocketMqProperties;

    @Override
    public Result sendSyncMsg(String topic, String content) throws Exception {

        ResultData data = new ResultData();
        try {
            String namesrvAddr = rocketMqProperties.getNamesrvAddr();
            if(!StringUtils.isEmpty(namesrvAddr)){
                Message msg = new Message(topic, topic, content.getBytes());
                SendResult sendResult = rocketMqProducer.send(msg);
                log.info("发送一条消息到rocketmq");
                data = new ResultData(topic, content,sendResult.getOffsetMsgId());
            }else {
                // 第一种方式，使用 kafkaProducer发送消息
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, content);
                RecordMetadata recordMetadata = kafkaProducer.send(record).get();
//                System.out.println("offset:"+recordMetadata.offset());    // 16
//                System.out.println("hasOffset:"+recordMetadata.hasOffset());  // true
                data = new ResultData(topic, content,String.valueOf(recordMetadata.offset()));

                // 第一种方式，使用 kafkaTemplate 发送消息
//                ListenableFuture<org.springframework.kafka.support.SendResult<String, String>> send =
//                        kafkaTemplate.send(topic, content);
//                System.out.println("发送一条消息到kafka，主题："+topic+",内容："+content+
//                        ",回显："+send.get().getProducerRecord().toString());
//                data = new ResultData(topic, content,send.get().getProducerRecord().toString());
            }
            return new Result(CodeConstant.RETCODE_200, MsgConstant.SUCCESS,data);
        }catch (Exception e){
            return new Result(CodeConstant.RETCODE_500, MsgConstant.ERROR,data);
        }
    }

    /**
     * 发送异步消息
     * @param topic 消息主题
     * @param content 消息内容
     * @param callback 回调方法对象
     * @throws Exception
     */
    @Override
    public void sendAsyncMsg(String topic, String content, MQCallback callback) throws Exception {
        try {
            String namesrvAddr = rocketMqProperties.getNamesrvAddr();
            if(!StringUtils.isEmpty(namesrvAddr)){
                // 说明有配置rocketmq,则发送消息到rocketmq中
                Message msg = new Message(topic, topic, content.getBytes());
                rocketMqProducer.send(msg,callback);
                log.info("发送一条异步消息到rocketmq");
            }else {
                ProducerRecord producerRecord = new ProducerRecord(topic, content);
                kafkaProducer.send(producerRecord,callback);
                log.info("发送一条异步消息到kafka");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendOneWayMsg(String topic, String content) {
        try {
            String namesrvAddr = rocketMqProperties.getNamesrvAddr();
            if(!StringUtils.isEmpty(namesrvAddr)){
                // 说明有配置rocketmq,则发送消息到rocketmq中
                Message msg = new Message(topic, topic, content.getBytes());
                rocketMqProducer.sendOneway(msg);
                log.info("发送一条单向消息到rocketmq");
            }else {
                ProducerRecord producerRecord = new ProducerRecord(topic, content);
                kafkaProducer.send(producerRecord);
                log.info("发送一条单向消息到kafka");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
