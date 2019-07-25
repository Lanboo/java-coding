[toc]
# ActiveMQ Query
## 代码示例
> 略

## P2P(Point to Point)
>运行顺序：
>- 1、运行TopicProducer、TopicConsumer1、TopicConsumer2
>- 2、运行TopicProducer、TopicConsumer2、TopicConsumer1
>- 3、运行TopicConsumer1、TopicProducer、TopicConsumer2
>- 4、运行TopicConsumer1、TopicConsumer2、TopicProducer、TopicProducer、TopicProducer、TopicProducer

>输出结果：
>- 1、TopicConsumer1输出；TopicConsumer2无输出，阻塞，等待Producer的下一个消息
>- 2、TopicConsumer2输出；TopicConsumer1无输出，阻塞，等待Producer的下一个消息
>- 3、TopicConsumer1先阻塞，后输出；TopicConsumer2无输出，阻塞，等待Producer的下一个消息
>- 4、TopicConsumer1、TopicConsumer2依次输出

>Point to Point：
>- 消息只能被一个消费者所消费。