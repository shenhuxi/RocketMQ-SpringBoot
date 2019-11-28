package com.hjmos.springbootrocketmq.pressuretest.rocket;

import com.hjmos.springbootrocketmq.entity.RocketProduceMessage;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestRocketMQ {
    private final ProduceMessageService produceMessageService;
    @Autowired
    public TestRocketMQ(ProduceMessageService produceMessageService) {
        this.produceMessageService = produceMessageService;
    }

    public Boolean sendManyMassge(){
        List<RocketProduceMessage> list = new ArrayList(5000);
        RocketProduceMessage msg ;
        for (int i = 0; i < 5000; i++) {
            msg= new RocketProduceMessage("AFC-FLOW","book","{id:"+i+"}");
            list.add(msg);
        }
        list.parallelStream()
                .forEach(p -> produceMessageService.produceMessage(p));
        return true;
    }
}
