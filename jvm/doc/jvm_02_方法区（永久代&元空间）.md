[toc]

# 方法区（永久代&元空间）
- 方法区是运行时数据区的一部分，如下图。
- 用于存储类信息、常量、静态变量、即时编译器编译后的代码等

- 方法区是JVM的规范，不是具体实现，永久代（PermGen space）、元空间（Metaspace）是两种JVM的实现方式。
    - HotSpot虚拟机才有PermGen space，其他虚拟机没有的
    - 元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过元空间与永久代之间最大的区别在于：<b>元空间并不在虚拟机中，而是使用本地内存</b>。




JDK版本|永久代|元空间|备注
:-|:-|:-|:-
1.6|有|-|-XX:PermSize=10M -XX:MaxPermSize=10M
1.7|有|-|部分内存从永久代移除，不再存储在永久代
1.8|无|有|-XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M