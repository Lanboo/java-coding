[toc]
# ZkClient_Wathcher机制实现分析
> 基于`0.10`版本

## 1、ZKClient与Zookeeper的类图
![](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/Zookeeper-ZkClient-1.png?raw=true)

## 2、ZkClinet初始化
``` java
public class ZkClient implements Watcher {
    protected final IZkConnection _connection;

    public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout) {
        this(new ZkConnection(zkServers, sessionTimeout), connectionTimeout);
    }

    public ZkClient(final IZkConnection zkConnection, final int connectionTimeout, final ZkSerializer zkSerializer, final long operationRetryTimeout) {
        if (zkConnection == null) {
            throw new NullPointerException("Zookeeper connection is null!");
        }
        _connection = zkConnection;
        _zkSerializer = zkSerializer;
        _operationRetryTimeoutInMillis = operationRetryTimeout;
        _isZkSaslEnabled = isZkSaslEnabled();
        // ZkClient实现了Watcher by xych
        connect(connectionTimeout, this);
    }

    @Override
    public void process(WatchedEvent event) {
        // 暂时省略，稍后分析
    }

    public void connect(final long maxMsToWaitUntilConnected, Watcher watcher) throws ZkInterruptedException, ZkTimeoutException, IllegalStateException {
        boolean started = false;
        acquireEventLock();
        try {
            setShutdownTrigger(false);
            _eventThread = new ZkEventThread(_connection.getServers());
            _eventThread.start();
            // ZkClient实现了Watcher by xych
            _connection.connect(watcher);

            LOG.debug("Awaiting connection to Zookeeper server");
            boolean waitSuccessful = waitUntilConnected(maxMsToWaitUntilConnected, TimeUnit.MILLISECONDS);
            if (!waitSuccessful) {
                throw new ZkTimeoutException("Unable to connect to zookeeper server '" + _connection.getServers() + "' with timeout of " + maxMsToWaitUntilConnected + " ms");
            }
            started = true;
        } finally {
            getEventLock().unlock();

            // we should close the zookeeper instance, otherwise it would keep
            // on trying to connect
            if (!started) {
                close();
            }
        }
    }
}
```
> ZkClient有众多构造方法，上面选取了其中两个<br>
> ZkClient的构造方法主要做了两件事情：
> 1. 创建IZkConnection实例对象
> 2. 调用connect方法，`ZkClient.connect`调用`IZkConnection.connect`方法

### 2.1、IZkConnection的实现类ZkConnection
``` java
public class ZkConnection implements IZkConnection {
    public ZkConnection(String zkServers, int sessionTimeOut) {
        _servers = zkServers;
        _sessionTimeOut = sessionTimeOut;
    }

    @Override
    public void connect(Watcher watcher) {
        _zookeeperLock.lock();
        try {
            if (_zk != null) {
                throw new IllegalStateException("zk client has already been started");
            }
            try {
                LOG.debug("Creating new ZookKeeper instance to connect to " + _servers + ".");
                // ZkClient实现了Watcher by xych
                // 这里实例化Zookeeper，并指定Zookeeper的默认Watcher为ZkClient by xych
                _zk = new ZooKeeper(_servers, _sessionTimeOut, watcher);
            } catch (IOException e) {
                throw new ZkException("Unable to connect to " + _servers, e);
            }
        } finally {
            _zookeeperLock.unlock();
        }
    }
}
```
> 可以看出，ZkConnection的实例化时并没有做什么事情，实例化Zookeeper对象是在connect方法中完成的，同时还指定了默认Watcher为ZkClient。


