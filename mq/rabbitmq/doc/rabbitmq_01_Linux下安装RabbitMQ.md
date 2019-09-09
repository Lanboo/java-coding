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
### 2.3、用户权限
<div style = "font-size:13px;">

[Access and Permissions](https://www.rabbitmq.com/management.html#permissions)
权限|含义|权限范围
:-|:-|:-
none|-|- 无法登录控制台。<br>- 可以通过AMQP做相关事情<br><br>通常是普通的生产者和消费者。
management|普通管理者|- 可登陆管理控制台，无法看到节点信息，也无法对policies进行管理。<br>- 可以通过AMQP做相关事情<br>- 列出自己可以通过AMQP登入的virtual hosts<br>- 查看自己的virtual hosts中的queues, exchanges 和 bindings<br>- 查看和关闭自己的channels和connections<br>- 查看有关自己的virtual hosts的“全局”的统计信息，包含其他用户在这些virtual hosts中的活动。
policymaker|策略制定者|- management可以做的任何事<br>- 查看、创建和删除自己的virtual hosts所属的policies和parameters
monitoring|监控者|- management可以做的任何事<br>- 列出所有virtual hosts，包括他们不能登录的virtual hosts<br>- 查看其他用户的connections和channels<br>- 查看节点级别的数据如clustering和memory使用情况<br>- 查看真正的关于所有virtual hosts的全局的统计信息<br>- 同时可以查看rabbitmq节点的相关信息(进程数，内存使用情况，磁盘使用情况等)
administrator|超级管理员|- policymaker和monitoring可以做的任何事<br>- 创建和删除virtual hosts<br>- 查看、创建和删除users<br>- 查看创建和删除permissions<br>- 关闭其他用户的connections

</div>

- 创建用户
    ``` shell
    > ./rabbitmqctl add_user {user_name} {password}
    ```

- 设置权限
    ``` shell
    > ./rabbitmqctl set_user_tags {user_name} {permission}
    ```

- 查看用户列表
    ``` shell
    > ./rabbitmqctl list_users
    ```

- 删除用户
    ``` shell
    > ./rabbitmqctl delete_user {user_name}
    ```

- 修改用户的密码
    ``` shell
    > ./rabbitmqctl change_password {user_name} {new_password}
    ```

- 为用户赋权
    ``` shell
    # 使用户user1具有vhost1这个virtual host中所有资源的配置、写、读权限以便管理其中的资源
    > ./rabbitmqctl  set_permissions -p vhost1 user1 '.*' '.*' '.*' 

    # 查看权限
    > ./rabbitmqctl list_user_permissions user1
    > ./rabbitmqctl list_permissions -p vhost1

    # 清除权限
    > ./rabbitmqctl clear_permissions [-p VHostPath] User
    ```

## 3、配置文件

[官方文档-配置文件](https://www.rabbitmq.com/configure.html#configuration-files)

> 配置文件默认不存在，需要手动添加

> Linux建议配置路径：$RABBITMQ_HOME/etc/rabbitmq/

> 3.7.0版本后，配置文件支持新格式：[新旧格式区别](https://www.rabbitmq.com/configure.html#config-file-formats)<br>
> 旧格式配置文件名：rabbitmq.config<br>
> 新格式配置文件名：rabbitmq.conf<br>
> 配置文件示例：[rabbitmq.conf.example](https://github.com/rabbitmq/rabbitmq-server/blob/v3.7.x/docs/rabbitmq.conf.example)

> 配置文件各个参数：[config-items](https://www.rabbitmq.com/configure.html#config-items)