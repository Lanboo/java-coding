# Redis为什么这么快？
1. 纯内存的KV结构的数据类型
2. 单线程
3. 异步非阻塞I/O  多路复用


## 2.为什么是单线程的(单线程怎么做到这么快的呢)？
1. 避免线程创建和线程销毁的消耗
2. 避免上线文的切换
3. 避免多线程的资源竞争

### 2.1.单线程会不会浪费CPU资源（现在的计算机基本上都是多核）？
单线程已经够用了，CPU不是Redis的性能瓶颈，内存和网络带宽才是性能瓶颈

为了最大程度地利用CPU，您可以在同一框中启动多个Redis实例，并将它们视为不同的服务器。

[官方：Redis is single threaded. How can I exploit multiple CPU / cores?](https://redis.io/topics/faq#redis-is-single-threaded-how-can-i-exploit-multiple-cpu--cores)
