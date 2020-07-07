[toc]

# Redis单实例安装

### 1、下载 & 解压缩
[reids官网](https://redis.io/)、[Redis中文官网](http://www.redis.cn/)、[Redis下载](https://redis.io/download)

> 安装目录：/root/nosql/redis

``` shell
cd /root/nosql/redis
wget http://download.redis.io/releases/redis-6.0.5.tar.gz
tar -zxvf redis-6.0.5.tar.gz
```
### 2、安装gcc依赖
> Redis是C语言编写的，编译需要

``` shell
yum install gcc
```

#### 2.1、gcc版本问题
``` shell
# 查看gcc的版本，centos7默认是4.8.5.我这里的就是4.8.5
gcc -v
```

- 升级gcc版本
    ``` shell
    yum -y install centos-release-scl
    yum -y install devtoolset-9-gcc devtoolset-9-gcc-c++ devtoolset-9-binutils

    scl enable devtoolset-9 bash
    ```
    PS：scl命令启用只是临时的，退出xshell或者重启就会恢复到原来的gcc版本。<br>
    如果要长期生效的话，执行如下：
    ``` shell
    echo "source /opt/rh/devtoolset-9/enable" >>/etc/profile
    ```

### 3、编译依赖(非必要)
``` shell
cd redis-6.0.5/deps
# 把deps目录下的文件夹都编译一下
make hiredis jemalloc linenoise lua
```

### 4、编译 & 安装
``` shell
cd redis-6.0.5
# 编译
# make MALLOC=libc
make
# 安装
make install
```
[关于redis源码的内存分配,jemalloc,tcmalloc,libc](https://blog.csdn.net/libaineu2004/article/details/79400357)
<br>
[Redis MALLOC 参数的使用](https://www.jianshu.com/p/a6f5994773f1)

### 5、修改配置文件
> `redis-6.0.5/redis.conf`

- 后台启动
    ```
    daemonize on
    ```
    改成
    ```
    daemonize yes
    ```
- IP绑定
    ```
    bind 127.0.0.1 
    ```
    改成
    ```
    bind 0.0.0.0
    ```
    或者 注释掉
- 需要密码访问
    ``` shell
    # 取消requirepass的注释
    requirepass yourpassword
    ```

### 6、启动Redis服务
``` shell
redis-6.0.5/src/redis-server redis-6.0.5/redis.conf
```
1. 建议配置alias
2. 将`redis-6.0.5/src`的相关命令复制到`redis-6.0.5/bin`(需自行创建)

### 7、进入客户端
``` shell
redis-6.0.5/src/redis-cli
```

### 8、停止redis（在客户端中）
``` shell
redis> shutdown
```
或
``` shell
ps -aux | grep redis
kill -9 xxxx
```

附赠一个Win7可用的Redis可视化客户端（redis-desktop-manager-0.8.3.3850）
链接：https://pan.baidu.com/s/1m6QoUaU0AKLfiXGhSNDJww
提取码：ewa0

### 9、防火墙端口暴露
Redis 端口：6379

[firewalld相关操作](/Linux/doc/Linux_学习笔记_03_防火墙.md#13关闭打开端口)