[toc]
# ActiveMQ topic
## 代码示例
> 略

## 时间相关性
>运行顺序：
>- 1、运行TopicProducer、TopicConsumer1、TopicConsumer2
>- 2、运行TopicConsumer1、TopicProducer、TopicConsumer2
>- 3、运行TopicConsumer1、TopicConsumer2、TopicProducer

>输出结果：
>- 1、TopicConsumer1、TopicConsumer2均无输出，且会阻塞，等待Producer的下一个消息
>- 2、TopicConsumer1输出；TopicConsumer2无输出，阻塞，等待Producer的下一个消息
>- 3、TopicConsumer1、TopicConsumer2均输出

>时间相关性：
>- 消费者只能消费监控topic开始之后的消息。
>- 提供者发送至topic的消息，只会被当前监控该topic的消费者所消费。