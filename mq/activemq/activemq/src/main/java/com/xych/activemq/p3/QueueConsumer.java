package com.xych.activemq.p3;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueConsumer {
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
            // 消息消费者
            MessageConsumer messageConsumer = session.createConsumer(destination);
            // 阻塞式接收消息
            TextMessage message = (TextMessage) messageConsumer.receive();
            System.out.println("QueueConsumer:" + message.getText());
            // message.acknowledge();// 主动确认
            // session.commit();
            session.close();
            connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
