package com.pci.hjmos.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Properties;
//@Component
public class WordCountStream {
    @SuppressWarnings("unchecked")
    @Bean("app1StreamTopology")
    public void testWordCount() {
        // 配置信息
        Properties props = new Properties();
        //Streams应用Id
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordCount");
        //Kafka集群地址
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "172.23.122.211:9092,172.23.122.211:9093,172.23.122.211:9094");
        //指定序列化和反序列化类型
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //创建一个topology构建器，在kakfa中计算逻辑被定义为连接的处理器节点的拓扑。
        final StreamsBuilder builder = new StreamsBuilder();
        //使用topology构建器创建一个源流，指定源topic
        KStream<String, String> source = builder.stream("wordCountInput");
        // 构建topology
        KStream<String, Long> wordCounts = source
                //把数据按空格拆分成单个单词
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                //过滤掉the这个单词，不统计这个单词
                .filter((key, value) -> (!value.equals("the")))
                //分组
                .groupBy((key, word) -> word)
                //计数，其中'countsStore'是状态存储的名字
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("countsStore"))
                .toStream();

        //将stream写回到Kafka的topic
        Produced<String, String> with = Produced.with(Serdes.String(), Serdes.String());
        wordCounts.to("wordCountOutput", Produced.with(Serdes.String(), Serdes.Long()));
        //创建Streams客户端
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        //启动Streams客户端
        streams.start();
    }
}
