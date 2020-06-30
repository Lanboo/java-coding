# Redis诞生
参考：[Redis的诞生](https://blog.csdn.net/chengqiuming/article/details/79113358)


## 1、创建者
- 出生于西西里岛的意大利人antirez（笔名）发明的。
- 个人网站：http://invece.org
> 早年是系统管理员，2004到2006年做嵌入式工作，之后接触web，2007年和朋友共同创建一个网站`LLOOGG.com`,并为了解决这个网站的负载问题，而在2009年开发了Redis数据库。

> LOG => LLOOGG

## 2、`LLOOGG.com`
1. LLOOGG.com网站是一个访客信息网站，网站可以通过javascript脚步，将访客IP地址、所属的国家、阅览信息、访问网页地址传送给LLOOGG.com网站。
2. 然后LLOOGG.com会将这些浏览数据通过web页面实时展示给用户，并存储最新的5到10000条浏览记录，以便进行查阅，就是说可以设置查看最近多少条。


