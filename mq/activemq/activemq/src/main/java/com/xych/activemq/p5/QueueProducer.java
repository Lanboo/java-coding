package com.xych.activemq.p5;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueProducer {
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
            // 消息发送者
            MessageProducer messageProducer = session.createProducer(destination);
            int len = 100;
            for(int i = 0; i < len; i++) {
                // 创建消息
                TextMessage message = session.createTextMessage("Hello World!" + i);
                // 发送消息
                messageProducer.send(message);
            }
            session.commit();
            session.close();
            connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
