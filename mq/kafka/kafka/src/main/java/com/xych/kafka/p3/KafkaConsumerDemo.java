package com.xych.kafka.p3;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.RangeAssignor;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaConsumerDemo implements Runnable {
    private KafkaConsumer<Integer, String> kafkaConsumer;

    public KafkaConsumerDemo(String topic) {
        init(topic);
    }

    private void init(String topic) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "xych.online:9092,xych.online:9093,xych.online:9094");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getSimpleName());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // Key 的反序列化方式
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        // Value 的反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RangeAssignor.class.getName());
        this.kafkaConsumer = new KafkaConsumer<>(properties);
        // 订阅
        this.kafkaConsumer.subscribe(Collections.singleton(topic));
    }

    @Override
    public void run() {
        Duration duration = Duration.ofSeconds(1L);
        while(true) {
            ConsumerRecords<Integer, String> consumerRecords = this.kafkaConsumer.poll(duration);
            for(ConsumerRecord<Integer, String> cRecord : consumerRecords) {
                System.out.println(Thread.currentThread().getName() + ":message receive:value=" + cRecord.value() + ",key=" + cRecord.key() + ",offset=" + cRecord.offset());
                this.kafkaConsumer.commitAsync();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new KafkaConsumerDemo("test")).start();
        // new Thread(new KafkaConsumerDemo("test")).start();
    }
}
