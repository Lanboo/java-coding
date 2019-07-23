[toc]
# 我的阿里云Nginx配置
> 1、反向代理Tomcat：7250端口，未对外暴露该接口<br>
> 2、Https<br>
> 3、http2.0<br>

``` vim

#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    gzip  on;
    gzip_min_length 1k;
    gzip_buffers 4 16k;
    #gzip_http_version 1.0;
    gzip_comp_level 2;
    gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png font/ttf font/otf image/svg+xml font/woff font/woff2;
    gzip_vary off;
    gzip_disable "MSIE [1-6]\.";

    server {
        listen       80;
        server_name  localhost;
        charset utf-8;

        #access_log  logs/host.access.log  main;

        # 重定向至Https Server
        rewrite ^(.*)$ https://$host$1 permanent;

        location / {
            proxy_pass http://127.0.0.1:7250;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
    # HTTPS server
    #
    server {
        listen       443 ssl http2;
        server_name  localhost;
        ssl_certificate      /root/pfx/nginx/www.xych.online.pem;
        ssl_certificate_key  /root/pfx/nginx/www.xych.online.key;

        #ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;

        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
        ssl_prefer_server_ciphers  on;

        location / {
            proxy_pass http://127.0.0.1:7250;
        }
    }
}

```