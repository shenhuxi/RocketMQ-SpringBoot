package com.pci.hjmos.rocketmqservice;

import com.pci.hjmos.api.produce.MQCallback;
import com.pci.hjmos.api.produce.ProduceMessageService;
import com.pci.hjmos.util.constant.CodeConstant;
import com.pci.hjmos.util.constant.MsgConstant;
import com.pci.hjmos.util.entity.ResultData;
import com.pci.hjmos.util.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 生产消息的服务实现
 */
@Slf4j
@Service
@Primary
public class RocketProduceMessageServiceImpl implements ProduceMessageService {
    @Autowired
    private DefaultMQProducer defaultProducer;
    /**
     * 发送同步消息
     *
     * @param topic   消息主题
     * @param content 消息内容
     */
    @Override
    public Result sendSyncMsg(String topic, String content) throws Exception {
        ResultData data = new ResultData();
        try {
            Message msg = new Message(topic, topic, content.getBytes());
            SendResult sendResult = defaultProducer.send(msg);
            data = new ResultData(topic, content,sendResult.getOffsetMsgId());

            return new Result(CodeConstant.RETCODE_200, MsgConstant.SUCCESS,data);
        }catch (Exception e){
            return new Result(CodeConstant.RETCODE_500, MsgConstant.ERROR,data);
        }
    }

    /**
     * 发送异步消息
     *
     * @param topic    消息主题
     * @param content  消息内容
     * @param callback 回调方法对象
     */
    @Override
    public void sendAsyncMsg(String topic, String content, MQCallback callback) throws Exception {
        try {
            Message msg = new Message(topic, topic, content.getBytes());
            defaultProducer.send(msg, callback);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
