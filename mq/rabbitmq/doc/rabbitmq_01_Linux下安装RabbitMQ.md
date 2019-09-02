[toc]

# Linux下安装RabbitMQ

## 1、安装依赖Erlang

### 1.1、下载
[Erlang Download](https://www.erlang.org/downloads)、
<br>
[OTP 22.0 Source File](http://erlang.org/download/otp_src_22.0.tar.gz)

``` shell
> cd /root/mq/rabbitmq/erlang
# 下载
> wget http://erlang.org/download/otp_src_22.0.tar.gz
# 解压：这里没有用gzip格式压缩，所以不用加z参数
> tar -xvf otp_src_22.0.tar.gz
```

### 1.2、编译&安装

``` shell
> cd /root/mq/rabbitmq/erlang/otp_src_22.0
# 配置安装路径
> ./configure --prefix=/root/mq/rabbitmq/erlang/otp_22.0
```

PS:[No curses library functions found解决方法](https://blog.csdn.net/NGU2028070003/article/details/85417183)
`yum install ncurses-devel`即可

### 1.3、设置环境变量
``` shell
> vim /etc/profile
export ERLANG_HOME=/hwd/software/erlang
export PATH=$ERLANG_HOME/bin:$PATH
> source /etc/profile
```
- 测试
``` shell
> erl
Erlang/OTP 22 [erts-10.4] [source] [64-bit] [smp:1:1] [ds:1:1:10] [async-threads:1] [hipe]

Eshell V10.4  (abort with ^G)
```
### 2、安装RabbitMQ
[UNIX Build](https://www.rabbitmq.com/install-generic-unix.html)
<br>
[rabbitmq-server-generic-unix-3.7.17.tar.xz](https://dl.bintray.com/rabbitmq/all/rabbitmq-server/3.7.17/rabbitmq-server-generic-unix-3.7.17.tar.xz)

另外注意：RabbitMQ和Erlang版本之间兼容问题：[RabbitMQ and Erlang/OTP Compatibility Matrix](https://www.rabbitmq.com/which-erlang.html#compatibility-matrix)

### 2.1、下载
``` shell
cd /root/mq/rabbitmq
wget https://dl.bintray.com/rabbitmq/all/rabbitmq-server/3.7.17/rabbitmq-server-generic-unix-3.7.17.tar.xz
# yum install xz    安装xz工具
# xz: -z压缩， -d解压，-k保留原文件
xz -d rabbitmq-server-generic-unix-3.7.17.tar.xz
# 解压：这里没有用gzip格式压缩，所以不用加z参数
tar -xvf rabbitmq-server-generic-unix-3.7.17.tar
```

### 2.2、启动
- 启用插件
``` shell
cd rabbitmq_server-3.7.17/sbin
# web 管理界面
./rabbitmq-plugins enable rabbitmq_management
```
- 启动服务
``` shell
> cd rabbitmq_server-3.7.17/sbin
> ./rabbitmq-server -detached
Warning: PID file not written; -detached was passed.
```