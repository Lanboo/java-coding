# Kafka数据生产流程
![](https://upload-images.jianshu.io/upload_images/2595955-afed948bbea5c1d9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/431/format/webp)

## 1、指定分区
``` java
ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, 0, null, message);
kafkaProducer.send(record);
```

## 2、分区器`Partitioner`
``` java
// org.apache.kafka.clients.producer.Partitioner
public interface Partitioner extends Configurable, Closeable {
    /**
     * @param topic The topic name
     * @param key The key to partition on (or null if no key)
     * @param keyBytes The serialized key to partition on( or null if no key)
     * @param value The value to partition on or null
     * @param valueBytes The serialized value to partition on or null
     * @param cluster The current cluster metadata
     */
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster);
    public void close();
}

// org.apache.kafka.clients.producer.internals.DefaultPartitioner
public class DefaultPartitioner implements Partitioner {

    private final ConcurrentMap<String, AtomicInteger> topicCounterMap = new ConcurrentHashMap<>();

    public void configure(Map<String, ?> configs) {}

    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // 获取Kafka集群中该topic下的分区
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (keyBytes == null) {
            int nextValue = nextValue(topic);
            // 获取有效分区。PS：当某个节点宕机后，分区数就有可能发生变化
            List<PartitionInfo> availablePartitions = cluster.availablePartitionsForTopic(topic);
            if (availablePartitions.size() > 0) {
                int part = Utils.toPositive(nextValue) % availablePartitions.size();
                return availablePartitions.get(part).partition();
            } else {
                // no partitions are available, give a non-available partition
                return Utils.toPositive(nextValue) % numPartitions;
            }
        } else {
            // hash the keyBytes to choose a partition
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    // 每个Topic维护一个计数器，以实现轮询分区的目的。
    private int nextValue(String topic) {
        AtomicInteger counter = topicCounterMap.get(topic);
        if (null == counter) {
            counter = new AtomicInteger(ThreadLocalRandom.current().nextInt());
            AtomicInteger currentCounter = topicCounterMap.putIfAbsent(topic, counter);
            if (currentCounter != null) {
                counter = currentCounter;
            }
        }
        return counter.getAndIncrement();
    }

    public void close() {}
}
```
另外，分发器可以自己实现分发规则，同时指定分发器就行
``` java
Properties properties = new Properties();
...
// 指定分发器为DefaultPartitioner.class
properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());
this.kafkaProducer = new KafkaProducer<>(properties);
```