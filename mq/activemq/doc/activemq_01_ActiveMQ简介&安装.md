[toc]

# ActiveMQ
> ActiveMQ是完全基于JMS规范实现的一个消息中间件产品。是Apache开源基金会研发的消息中间件。ActiveMQ主要应用在分布式系统架构中，帮助构建高可用、高性能、可伸缩的企业级面向消息服务的系统

## 1、ActiveMQ特性
1. 多语言：`java`、`C`、`C++`、`C#`、`Ruby`、`Perl`、`Python`、`PHP`
2. 多协议：`openwire`、`stomp`、`REST`、`ws`、`notification`、`XMPP`、`AMQP`
3. 完全支持`jms1.1`和`J2ee1.4`规范
4. 对Spring的支持，ActiveMQ可以很容易内嵌到Spring模块中

## 2、启动
> 下载：[官网下载](http://activemq.apache.org/activemq-5150-release.html)

> 两个版本：`Windows`、`Unix/Linux/Cygwin`

### 2.1、Windows
双击/bin/win64/activemq.bat即可
### 2.2、Linux
> ``` shell
> # 1、复制压缩包至服务器
> # 2、解压
> tar -zxvf apache-activemq-5.15.0-bin.tar.gz
> # 3、启动，bin目录下
> # 3.1、普通启动
> sh avtivemq start
> # 3.2、指定目录文件启动
> sh avtivemq start > /tmp/activemq.log
> # 4、检查是否启动
> netstat -an | grep 61616
> # 5、关闭ActiveMQ
> sh avtivemq stop
> ```
## 3、ActiveMQ管理平台
> 地址：[http://127.0.0.1:8161/](http://127.0.0.1:8161/) <br>
> 默认账户密码：admin admin