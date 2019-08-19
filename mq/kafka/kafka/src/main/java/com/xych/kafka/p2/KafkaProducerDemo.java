package com.xych.kafka.p2;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerDemo implements Runnable {
    private KafkaProducer<Integer, String> kafkaProducer;
    private String topic;

    public KafkaProducerDemo(String topic) {
        this.topic = topic;
        init();
    }

    private void init() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "www.xych.online:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, this.getClass().getSimpleName());
        properties.put(ProducerConfig.ACKS_CONFIG, "-1");
        // Key 的序列化方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        // Value 的序列化方式
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.kafkaProducer = new KafkaProducer<>(properties);
    }

    @Override
    public void run() {
        int num = 0;
        while(num < 50) {
            String message = "message_offset" + num;
            System.out.println("begin send message:" + message);
            kafkaProducer.send(new ProducerRecord<Integer, String>(topic, message));
            num++;
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new KafkaProducerDemo("test")).start();
    }
}
