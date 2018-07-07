[toc]

#### Zookeeper学习

##### 1、[学习笔记_01_分布式简介](notes/zookeeper学习笔记_01_分布式简介.md)
分布式特性、分布式的问题、CAP、BASE

##### 2、[初步认识Zookeeper](notes/zookeeper学习笔记_02_初步认识zookeeper.md)
zookeeper是什么、能做什么、特性、单机版安装、集群版安装

##### 3、[zoo.cfg配置简介](notes/zookeeper学习笔记_03_zoo.cfg配置简介.md)
zookeeper配置文件zoo.cfg简介

##### 4、[Zookeeper客户端的使用](notes/zookeeper学习笔记_04_客户端的使用.md)
zookeeper中的一些概念、客户端的使用

##### 5、[Zookeeper的原生API的使用](notes/zookeeper学习笔记_05_原生API的使用.md)
原生API的使用，包括：
1. 建立连接、创建、修改、删除
2. exists、getData、getChildren
3. Watcher事件监控

##### 6、[Zookeeper开源客户端ZkClient和Curator简介](notes/zookeeper学习笔记_06_Zookeeper开源客户端ZkClient和Curator简介.md)
1. Zookeeper API不足之处
2. ZkClient简介
3. Curator简介

##### 7、[Zookeeper开源客户端ZKClient的使用](notes/zookeeper学习笔记_07_ZkClient的使用.md)
1. 建立连接、创建、获取、修改、删除


##### 8、[Zookeeper开源客户端ZKClient的使用_Watcher事件](notes/zookeeper学习笔记_08_ZkClient的使用_Watcher事件.md)
1. Listener
2. Watcher触发 

##### 9、[ZkClient_Wathcher机制实现分析](notes/zookeeper学习笔记_09_ZkClient_Wathcher机制实现分析.md)
1. 首先，实例化ZkClient对象时，通过ZkConnection指定Zookeeper的默认Watcher为ZkClient
2. 其次，在`subscribeXxx`方法中，为某节点添加Listener，以及向服务器注册默认Watcher
3. 最后，ZkClient实现Watcher接口，在`process`方法中触发Listener和Watcher的反复注册






<br><br><br><br><br><br>学自咕泡学院