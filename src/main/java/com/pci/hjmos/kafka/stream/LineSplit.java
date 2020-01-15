package com.pci.hjmos.kafka.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class LineSplit {
 
    public static void main(String[] args) throws Exception {
        // 配置信息
        Properties props = new Properties();
        //Streams应用Id
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
        //Kafka集群地址
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "172.23.122.211:9092,172.23.122.211:9093,172.23.122.211:9094");
        //指定序列化和反序列化类型
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream("streams-plaintext-input");
        source.flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .to("streams-linesplit-output");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);
//         attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-linesplit-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            streams.start();
            latch.await();
            log.info("程序开始执行了....");
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}