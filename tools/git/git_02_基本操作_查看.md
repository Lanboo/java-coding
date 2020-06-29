# 基本操作-查看

## 1、git config --list
> 查看所有配置

## 2、git status
> 查看当前项目的文件状态

## 3、git branch -a
> 查看所有本地分支、远程分支

``` git
# 不带参数：列出本地已经存在的分支，并且在当前分支的前面用"*"标记
$ git branch
# 查看远程版本库分支列表
$ git branch -r

# 另外，如果本地看不到远程分支，可以先[git fetch]更新remote索引，之后就可以看到了
# https://blog.csdn.net/liuniansilence/article/details/73832642
# 查看所有分支列表，包括本地和远程
$ git branch -a
```