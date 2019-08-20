package com.xych.kafka.springboot;

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
