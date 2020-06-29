# git remote

## 1、显示远程仓库信息
> git remote -v

## 2、映射远程仓库
- git remote add [shortname] [url]
- 可映射多个远程仓库，区分别名即可
``` git
$ git init
$ git remote add origin git@github.com:Lanboo/java-coding.git
$ git remote add gitee git@gitee.com:Lanboo/java-coding.git
```

## 3、同一个别名对应多个远程仓库地址
- `git remote set-url --add <name> <newurl>`
- 目的：git pull、git push时少操作一次

``` git
# 同一个别名对应多个远程仓库地址
$ git remote set-url --add origin git@gitee.com:Lanboo/java-coding.git
```

## 4、修改仓库地址
- `git remote set-url <name> <newurl>`