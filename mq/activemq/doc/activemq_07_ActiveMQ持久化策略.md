[toc]

# ActiveMQ持久化策略
> ActiveMQ支持多种不同的持久化方式，主要有以下几种，不过，无论使用哪种持久化方式，消息的存储逻辑都是
一致的。
>- KahaDB存储（默认存储方式）
>- JDBC存储
>- Memory存储
>- LevelDB存储
>- JDBC With ActiveMQ Journal

## 1、KahaDB存储

> KahaDB是目前默认的存储方式,可用于任何场景,提高了性能和恢复能力。<br>
> 消息存储使用一个事务日志和仅仅用一个索引文件来存储它所有的地址。<br>

> KahaDB是一个专门针对消息持久化的解决方案,它对典型的消息使用模式进行了优化。<br>
> 在KahaDb中,数据被追加到data logs中。当不再需要log文件中的数据的时候,log文件会被丢弃。

### 1.1、KahaDB的配置方式
``` xml
<persistenceAdapter>
    <kahaDB directory="${activemq.data}/kahadb"/>
</persistenceAdapter>
```

### 1.2、KahaDB的存储原理
> 在`data/kahadb`这个目录下，会生成四个文件
>- `db.data` 它是消息的索引文件，本质上是B-Tree（B树），使用B-Tree作为索引指向db-*.log里面存储的消息
>- `db.redo` 用来进行消息恢复
>- `db-*.log` 存储消息内容。新的数据以APPEND的方式追加到日志文件末尾。属于顺序写入，因此消息存储是比较快的。默认是32M，达到阀值会自动递增
>- `lock`文件 锁，表示当前获得kahadb读写权限的broker

## 2、JDBC存储
> 使用JDBC持久化方式，数据库会创建3个表：`activemq_msgs`、`activemq_acks`和`activemq_lock`。<br>
>- ACTIVEMQ_MSGS 消息表，queue和topic都存在这个表中
>- ACTIVEMQ_ACKS 存储持久订阅的信息和最后一个持久订阅接收的消息ID
>- ACTIVEMQ_LOCKS 锁表，用来确保某一时刻，只能有一个ActiveMQ broker实例来访问数据库

### 2.1、JDBC存储的配置方式
``` xml
<persistenceAdapter>
    <jdbcPersistenceAdapter dataSource="# MySQL-DS " createTablesOnStartup="true" />
</persistenceAdapter>
```
- `dataSource`指定持久化数据库的bean<br>
- `createTablesOnStartup`是否在启动的时候创建数据表，默认值是true，这样每次启动都会去创建数据表了，一般是第一次启动的时候设置为true，之后改成false
- Mysql持久化Bean配置
    ``` xml
    <bean id="Mysql-DS" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://192.168.11.156:3306/activemq?relaxAutoCommit=true"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>
    ```
- 添加数据库依赖jar

## 3、LevelDB存储
> LevelDB持久化性能高于KahaDB，虽然目前默认的持久化方式仍然是KahaDB。<br>
> 在ActiveMQ 5.9版本提供了基于LevelDB和Zookeeper的数据复制方式，用于Master-slave方式的首选数据复制方案。

> <b>不过，据ActiveMQ官网对LevelDB的表述：LevelDB官方建议使用以及不再支持，推荐使用的是KahaDB</b>

``` xml
<persistenceAdapter>
    <levelDBdirectory="activemq-data"/>
</persistenceAdapter>
```

## 4、Memory消息存储
> 基于内存的消息存储，内存消息存储主要是存储所有的持久化的消息在内存中 <br>
``` xml
<beans>
    <broker brokerName="test-broker" persistent="false" xmlns="http://activemq.apache.org/schema/core">
        <transportConnectors>
            <transportConnector uri="tcp://localhost:61635"/>
        </transportConnectors>
    </broker>
</beans>
```
- persistent=”false”,表示不设置持久化存储，直接存储到内存中

## 5、JDBC Message store with ActiveMQ Journal
> 这种方式克服了JDBC Store的不足，JDBC每次消息过来，都需要去写库和读库。

> `ActiveMQ Journal`，使用高速缓存写入技术，大大提高了性能。

>- 当消费者的消费速度能够及时跟上生产者消息的生产速度时，journal文件能够大大减少需要写入到DB中的消息。<br>
>- 举个例子，生产者生产了1000条消息，这1000条消息会保存到journal文件，如果消费者的消费速度很快的情况下，在journal文件还没有同步到DB之前，消费者已经消费了90%的以上的消息，那么这个时候只需要同步剩余的10%的消息到DB。
>- 如果消费者的消费速度很慢，这个时候journal文件可以使消息以批量方式写到DB。

``` xml
<persistenceFactory>
    <journalPersistenceAdapterFactory dataSource="#Mysql-DS" dataDirectory="activemqdata"/>
</persistenceFactory>
```

> 在服务端循环发送消息。可以看到数据是延迟同步到数据库的

