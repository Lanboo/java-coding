[toc]

# ActiveMQ简单示例

## 1、Point to Point
### 1.1、生产者
``` java {.line-numbers .highlight=24}
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
            // 创建消息
            TextMessage message = session.createTextMessage("Hello World!");
            // 发送消息
            messageProducer.send(message);
            session.commit();
            session.close();
            connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
```
### 1.2、消费者
``` java {.line-numbers .highlight=22}
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueConsumer1 {
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
                System.out.println("QueueConsumer1：" + message.getText());
            }
            //session.commit();
            //session.close();
            // connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
```


## 2、Topic

> 时间相关性：
>- 消费者只能消费监控topic开始之后的消息。
>- 提供者发送至topic的消息，只会被当前监控该topic的消费者所消费。

### 2.1、生产者
``` java {.line-numbers .highlight=22}
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicProducer {
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
            Destination destination = session.createTopic("xych-test-topic");
            // 消息发送者
            MessageProducer messageProducer = session.createProducer(destination);
            // 创建消息
            TextMessage message = session.createTextMessage("Hello World!");
            // 发送消息
            messageProducer.send(message);
            session.commit();
            session.close();
            connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
```

### 2.2、消费者
``` java {.line-numbers .highlight=22}
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicConsumer1 {
// public class TopicConsumer2 {
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
            Destination destination = session.createTopic("xych-test-topic");
            // 消息消费者
            MessageConsumer messageConsumer = session.createConsumer(destination);
            // 阻塞式接收消息
            TextMessage message = (TextMessage) messageConsumer.receive();
            System.out.println("TopicConsumer1:" + message.getText());
            // System.out.println("TopicConsumer2:" + message.getText());
            session.commit();
            session.close();
            connection.close();
        }
        catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
```

### 2.3、时间相关性验证
>运行顺序：
>- 1、运行TopicProducer、TopicConsumer1、TopicConsumer2
>- 2、运行TopicConsumer1、TopicProducer、TopicConsumer2
>- 3、运行TopicConsumer1、TopicConsumer2、TopicProducer

>输出结果：
>- 1、TopicConsumer1、TopicConsumer2均无输出，且会阻塞，等待Producer的下一个消息
>- 2、TopicConsumer1输出；TopicConsumer2无输出，阻塞，等待Producer的下一个消息
>- 3、TopicConsumer1、TopicConsumer2均输出
