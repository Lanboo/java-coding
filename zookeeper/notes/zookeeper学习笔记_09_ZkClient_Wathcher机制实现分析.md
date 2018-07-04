[toc]
# ZkClient_Wathcher机制实现分析
> 基于`0.10`版本

> 目的：
> 1. Listener是怎么代替Watcher？
> 2. Watcher是怎么反复注册的？

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

## subscribeDataChanges
``` java
public class ZkClient implements Watcher {
    private final Map<String, Set<IZkChildListener>> _childListener = new ConcurrentHashMap<String, Set<IZkChildListener>>();
    private final ConcurrentHashMap<String, Set<IZkDataListener>> _dataListener = new ConcurrentHashMap<String, Set<IZkDataListener>>();

    // 为某个节点添加Listener（监控某节点的数据变化）
    public void subscribeDataChanges(String path, IZkDataListener listener) {
        Set<IZkDataListener> listeners;
        synchronized (_dataListener) {
            listeners = _dataListener.get(path);
            if (listeners == null) {
                listeners = new CopyOnWriteArraySet<IZkDataListener>();
                _dataListener.put(path, listeners);
            }
            listeners.add(listener);
        }
        watchForData(path);
        LOG.debug("Subscribed data changes for " + path);
    }

    // 移除某个节点的Listener
    public void unsubscribeDataChanges(String path, IZkDataListener dataListener) {
        //代码省略
    }

    // 为某个节点添加Listener（监控子节点数量的变化，不监控子节点数据的变化）
    public List<String> subscribeChildChanges(String path, IZkChildListener listener) {
        synchronized (_childListener) {
            Set<IZkChildListener> listeners = _childListener.get(path);
            if (listeners == null) {
                listeners = new CopyOnWriteArraySet<IZkChildListener>();
                _childListener.put(path, listeners);
            }
            listeners.add(listener);
        }
        return watchForChilds(path);
    }

    // 移除某个节点的Listener
    public void unsubscribeChildChanges(String path, IZkChildListener childListener) {
        //代码省略
    }

    public void watchForData(final String path) {
        retryUntilConnected(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                _connection.exists(path, true);
                return null;
            }
        });
    }

    public List<String> watchForChilds(final String path) {
        if (_zookeeperEventThread != null && Thread.currentThread() == _zookeeperEventThread) {
            throw new IllegalArgumentException("Must not be done in the zookeeper event thread.");
        }
        return retryUntilConnected(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                exists(path, true);
                try {
                    return getChildren(path, true);
                } catch (ZkNoNodeException e) {
                    // ignore, the "exists" watch will listen for the parent node to appear
                }
                return null;
            }
        });
    }
}
```
> 以`subscribeDataChanges`方法为例，做了两个事情：
> - 将listener放到Map中
> - 


