[toc]

# vim常用操作

> 参考：[Centos防火墙设置与端口开放的方法](https://blog.csdn.net/u011846257/article/details/54707864)
``` linux
// 在指定区域打开2181端口
firewall-cmd --zone=public --add-port=2181/tcp
firewall-cmd --zone=public --add-port=2181/tcp --permanent    // 永久生效

// 重启防火墙
firewall-cmd --reload
```