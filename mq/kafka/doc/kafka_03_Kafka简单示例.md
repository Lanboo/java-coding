[toc]

# Kafka简单示例
## 1、KafkaProducerDemo
``` java
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
            String message = "message_" + num;
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
```

## 2、KafkaConsumerDemo
``` java
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
        // Key 的反序列化方式
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        // Value 的反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
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
                System.out.println("message receive:" + cRecord.value());
                this.kafkaConsumer.commitAsync();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new KafkaConsumerDemo("test")).start();
    }
}
```

## 3、当前代码的运行情况
> 消费者只会接收连接上Kafka之后的消息，即连接上之前的消息不会接收到。