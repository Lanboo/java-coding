package com.xych.activemq.p3;

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
            // Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            // Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            // 目的地
            Destination destination = session.createQueue("xych-test-queue");
            // 消息发送者
            MessageProducer messageProducer = session.createProducer(destination);
            // 创建消息
            TextMessage message = session.createTextMessage("Hello World!");
            // 发送消息
            messageProducer.send(message);
            // session.commit();
            session.close();
            connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
