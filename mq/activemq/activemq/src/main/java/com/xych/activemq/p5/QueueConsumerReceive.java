package com.xych.activemq.p5;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueConsumerReceive {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        Connection connection = null;
        try {
            // 连接
            connection = connectionFactory.createConnection();
            connection.start();
            // 会话
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            // 目的地
            Destination destination = session.createQueue("xych-test-queue");
            // 消息消费者
            MessageConsumer messageConsumer = session.createConsumer(destination);
            while(true) {
                // 阻塞式接收消息
                TextMessage message = (TextMessage) messageConsumer.receive();
                System.out.println(Thread.currentThread().getName() + "：by receive：" + message.getText());
            }
            // session.commit();
            // session.close();
            // connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
