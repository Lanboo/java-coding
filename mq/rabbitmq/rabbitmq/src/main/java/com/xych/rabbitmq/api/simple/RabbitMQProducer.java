package com.xych.rabbitmq.api.simple;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RabbitMQProducer extends BaseRabbitMQ {
    public static void main(String[] args) throws InterruptedException {
        new RabbitMQProducer().run();
        Thread.sleep(1000);
    }

    @Override
    public void doRun() {
        try {
            String msg = "Simple Msg_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            System.out.println(msg);
            channel.basicPublish(EXCHANGE, ROUTING_KEY, null, msg.getBytes());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
