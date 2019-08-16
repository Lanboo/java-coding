package com.xych.kafka.p1;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaConsumerDemo implements Runnable {
    private KafkaConsumer<Integer, String> kafkaConsumer;

    public KafkaConsumerDemo(String topic) {
        init(topic);
    }

    private void init(String topic) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "www.xych.online:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getSimpleName());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // Key 的序列化方式
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        // Value 的序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        this.kafkaConsumer = new KafkaConsumer<>(properties);
        this.kafkaConsumer.subscribe(Collections.singleton(topic));
    }

    @Override
    public void run() {
        Duration duration = Duration.ofSeconds(1L);
        while(true) {
            ConsumerRecords<Integer, String> consumerRecords = this.kafkaConsumer.poll(duration);
            for(ConsumerRecord<Integer, String> cRecord : consumerRecords) {
                System.out.println("message receive:" + cRecord.value());
                this.kafkaConsumer.commitAsync();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new KafkaConsumerDemo("test")).start();
    }
}
