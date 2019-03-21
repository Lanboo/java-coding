[toc]
# Linux定时任务
## 1、基本命令
``` shell
# 查看定时任务
> crontab -l
# 编辑定时任务
> crontab -e
```

## 2、定时任务语法

## 3、定时任务注意事项
### 3.1、输出流重定向
> crontab输出不到标准输出和错误输出，应该重定向到文件
``` shell
# 输出到文件
*/2 * * * * ls -al /etc > /tmp/cron.log 2>&1
# 追加方式
*/2 * * * * ls -al /etc >> /tmp/cron.log 2>&1
```
> 如果没有重定向，运行时产生的输出这时就会发往/var/spool/clientmqueue/，可以用mail命令查看，或到目录里查找
