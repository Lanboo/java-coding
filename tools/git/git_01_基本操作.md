[toc]

# git操作流转图
![各个操作流转图](https://img-blog.csdn.net/20170423160307093)

# 基本操作
## 1、git init
> 将当前目录创建成新的本地仓库

## 2、git remote add [shortname] [url]
> 映射远程仓库<br>
> 可映射多个远程仓库，区分别名即可

``` git
$ git remote add origin git@github.com:Lanboo/java-coding.git
$ git remote add gitee git@gitee.com:Lanboo/java-coding.git

# 显示远程仓库
$ git remote -v
gitee   git@gitee.com:Lanboo/java-coding.git (fetch)
gitee   git@gitee.com:Lanboo/java-coding.git (push)
origin  git@github.com:Lanboo/java-coding.git (fetch)
origin  git@github.com:Lanboo/java-coding.git (push)

# 帮助
$ git remote -h
``` 
## 3、git clone [url]
> 将Server端的某仓库克隆到本地
``` git 
$ git clone git@xxx/xxx.get
```
[git clone命令详解](https://blog.csdn.net/zmzwll1314/article/details/53161958)

## 4、git add 
> 会将文件放到暂存区
``` git 
$ git add gitTemp.txt
```
[git add 命令扩展](https://www.cnblogs.com/skura23/p/5859243.html)

- `add`后的文件，状态`git status`将会进入`unstage`
- `commit`后会进入本地仓库
- `push`后会同步到Server端

## 5、git commit 
> `commit`会将`add`的文件放到本地仓库
``` git
$ git commit -m 'message'
# message是注释内容、log日志
```

## 6、git push [shortname] [branch]
> 提交至指定远程地址的某个分支

``` git
$ git push origin master
$ git push gitee master
```

## 7、git pull [branch]
> 拉取远程分支的新内容到本地分支
``` git
$ git pull origin master
```

## 8、git checkout [branch]
``` git
# 切换到master分支
$ git checkout master
```

## 9、简单合并分支
``` git 
# 将dev分支合并到master分支

# 切换到master分支
$ git checkout master
# 拉取dev分支新内容
$ git pull origin dev
# 提交至master分支
$ git push origin master
```
