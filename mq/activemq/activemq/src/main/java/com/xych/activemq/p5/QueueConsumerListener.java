package com.xych.activemq.p5;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueConsumerListener {
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
            // 监听器获取消息
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println(Thread.currentThread().getName() + "：by listener：" + textMessage.getText());
                    }
                    catch(JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            //session.commit();
            //session.close();
            // connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
