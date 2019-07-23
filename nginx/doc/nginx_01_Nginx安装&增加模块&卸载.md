[toc]
# 1、Nginx 安装
> Linux版本：CentOS <br>
> Nginx版本：1.16.0<br>

> 基于源码安装 <br>
> 大致步骤：下载源码 -> 设置参数 -> 编译安装<br>

## 1.1、下载
> 下载链接：[nginx: download](http://nginx.org/en/download.html)<br>
> Stable version   稳定版本<br>


> ``` shell
> cd /root/nginx
> # 下载到当前目录
> wget http://nginx.org/download/nginx-1.16.0.tar.gz
> # 解压  
> tar -zxvf nginx-1.16.0.tar.gz
> # 重命名
> mv nginx-1.16.0 nginx-1.16.0-build
> # 创建安装目录，备用
> mkdir nginx-1.16.0
> ```

## 1.2、安装依赖
> `yum -y install gcc pcre-devel zlib-devel openssl openssl-devel`

## 1.3、安装Nginx
> ``` shell
> # 打开源码目录 
> cd nginx-1.16.0-build
> # 设置安装目录
> ./configure --prefix=/root/nginx/nginx-1.16.0
> # 编译 & 安装
> make && make install
> ```

## 1.4、启动Nginx
> ``` shell
> cd /root/nginx/nginx-1.16.0/sbin
> # 启动
> ./nginx
> ./nginx -c /root/nginx/nginx-1.16.0/conf/nginx.conf
> # 重新加载配置文件
> ./nginx -s reload
> # 停止服务
> ./nginx -s stop
> ```

# 2、增加模块
> 这里增加SSL模块，用于https访问
> 开启http2.0
## 2.1、查看当前配置
> ``` shell
> > cd /root/nginx/nginx-1.16.0/sbin
> > ./nginx -V
> 
> nginx version: nginx/1.16.0
> built by gcc 4.8.5 20150623 (Red Hat 4.8.5-36) (GCC) 
> built with OpenSSL 1.0.2k-fips  26 Jan 2017
> TLS SNI support enabled
> configure arguments: --prefix=/root/nginx/nginx-1.16.0
> ```
## 2.2、备份当前版本
> ``` shell
> cd /root/nginx/nginx-1.16.0/sbin
> mv nginx nginx.bak
> ```
## 2.3、源码目录下重新编译（注意不能安装）
> ``` shell
> cd /root/nginx/nginx-1.16.0-build
> # 重设编译参数
> ./configure --prefix=/root/nginx/nginx-1.16.0 --with-http_ssl_module --with-http_v2_module
> # 只编译（不能安装，否则安装目录下的配置都会被覆盖）
> make
> # 复制`nginx`至安装目录
> cp objs/nginx /root/nginx/nginx-1.16.0/sbin
> ```
## 2.4、重启
> 不能重新加载配置，要重启

> ``` shell
> cd /root/nginx/nginx-1.16.0/sbin
> ./nginx -s stop
> ./nginx
> ```

# 3、卸载
> 删除所有的Nginx相关文件即可 <br>
> 注：只是简单的搜索+删除

> 另外，如果是同`yum`命令安装的，可以直接卸载：`yum remove nginx`

> ``` shell
> # 停止Nginx
> ./root/nginx/nginx-1.16.0/sbin/nginx -s stop
> # 根目录搜索
> sudo find / -name nginx
> # rm -rf 依次删除即可
> rm -rf 
> ```

