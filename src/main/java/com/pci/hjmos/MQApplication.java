package com.pci.hjmos;

import com.pci.hjmos.api.consumer.MessageConsumer;
import com.pci.hjmos.util.SpringUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class MQApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MQApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        initConsumer();
    }

    private void initConsumer() {
        //获取继承了MessageConsumer接口的消费者配置类
        Map<String, MessageConsumer> beansOfType = SpringUtil.getBeansOfType(MessageConsumer.class);
    }


}
