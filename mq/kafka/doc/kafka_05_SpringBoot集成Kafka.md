# SpringBoot集成Kafka

## 1、pom.xml依赖
``` xml
<dependencies>
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>2.3.0</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
        <version>2.2.8.RELEASE</version>
    </dependency>
</dependencies>
```

``` xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.1.7.RELEASE</version>
        <scope>provided</scope>
        <exclusions>
            <exclusion>
                <artifactId>tomcat-embed-el</artifactId>
                <groupId>org.apache.tomcat.embed</groupId>
            </exclusion>
            <exclusion>
                <artifactId>tomcat-embed-websocket</artifactId>
                <groupId>org.apache.tomcat.embed</groupId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-jasper</artifactId>
        <version>8.5.43</version>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.8</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.25</version>
    </dependency>
</dependencies>
```

## 2、application.properties
``` properties
spring.kafka.bootstrap-servers=xych.online:9092

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.IntegerSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

spring.kafka.consumer.group-id=test
spring.kafka.consumer.enable-auto-commit=true
spring.kafka.consumer.auto-commit-interval=1000
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.IntegerDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

## 3、Producer端
``` java
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Component
@RequestMapping("kafka")
@Slf4j
public class KafkaProducerDemo {
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @PostMapping("/send")
    @ResponseBody
    public Object send(String message) throws Exception {
        ListenableFuture<SendResult<Integer, String>> future = this.kafkaTemplate.send("test", message);
        SendResult<Integer, String> sendResult = future.get();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        log.info("Producer:topic={},value={},offset={},partition={}", recordMetadata.topic(), message, recordMetadata.offset(), recordMetadata.partition());
        return recordMetadata.toString();
    }
}
```

## 4、Listener
``` java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaConsumerDemo {
    @KafkaListener(topics = { "test" })
    public void listen(ConsumerRecord<Integer, String> record) throws Exception {
        log.info("Consumer:topic={},value={},offset={}", record.topic(), record.value(), record.offset());
    }
}
```