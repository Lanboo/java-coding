[toc]
# dubbo容错机制
转自：[Dubbo服务集群容错配置（四）](https://blog.csdn.net/u014401141/article/details/71307820)

## 1、dubbo配置
``` xml
<dubbo:service cluster="failover" />
<dubbo:reference cluster="failover" />
<dubbo:consumer cluster="failover" />
<dubbo:provider cluster="failover" />
```
- 默认值：failover
- 可选：`failover`、`failfast`、`failsafe`、`failback`、`forking`

## 2.1、failover 失败自动切换
>- 失败自动切换：当出现失败，重试其它服务器。
>- 通常用于读操作，但重试会带来更长延迟。
>- 可通过retries="2"来设置重试次数(不含第一次)。

>- 重试会带来更长的延迟。

## 2.2、failfast 快速失败
>- 快速失败：只发起一次调用，失败立即报错。
>- 通常用于非幂等性的写操作，比如新增记录。

>- 如果有机器正在重启，可能会出现调用失败。

## 2.3、failsafe 失败安全
>- 失败安全：出现异常时，直接忽略。
>- 通常用于写入审计日志等操作。
>- 此设置会使`mork`机制失效

>- 调用信息丢失。

## 2.4、failback 失败自动恢复
>- 失败自动恢复，后台记录失败请求，定时重发。
>- 通常用于消息通知操作。

>- 不可靠，重启后信息丢失。

## 2.5、forking 并行调用
>- 并行调用多个服务器，只要一个成功即返回。
>- 可通过forks="2"来设置最大并行数。

>- 浪费资源。

## 3、总
![](https://raw.githubusercontent.com/Lanboo/resource/master/images/java-coding/dubbo/dubbo-01-%E9%9B%86%E7%BE%A4%E5%AE%B9%E9%94%99.jpg)