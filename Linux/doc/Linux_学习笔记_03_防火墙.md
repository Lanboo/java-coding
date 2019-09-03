[toc]

> 参考：[Centos防火墙设置与端口开放的方法](https://blog.csdn.net/u011846257/article/details/54707864)

# 1、firewalld
## 1.1、启动&停止
``` shell
# 启动、停止防火墙
systemctl start firewalld
systemctl stop firewalld

# 设置、禁止开机启动
systemctl enable firewalld
sytemctl disable firewalld

# 重新加载防火墙
firewall-cmd --reload

# 查看防火墙状态
systemctl status firewalld
firewall-cmd --state

# 查看版本
firewall-cmd --version

# 查看帮助
firewall-cmd --help
```
## 1.2、查看端口信息
``` shell
# 查看所有信息
firewall-cmd --list-all
# 查看开放的端口信息
firewall-cmd --list-ports
# 查看指定区域下的端口号
firewall-cmd --zone=public --list-ports
```
## 1.3、关闭&打开端口
``` shell
# 在指定区域打开2181端口
firewall-cmd --zone=public --add-port=2181/tcp
firewall-cmd --zone=public --add-port=2181/tcp --permanent    # 永久生效

# 说明：
# –zone 作用域
# –add-port=2181/tcp 添加端口，格式为：端口/通讯协议
# –permanent #永久生效，没有此参数重启后失效

# 删除指定区域下的端口号
firewall-cmd --zone=public --remove-port=2181/tcp
firewall-cmd --zone=public --remove-port=2181/tcp --permanent    # 永久生效

# 重启防火墙
firewall-cmd --reload
```

# 2、iptables