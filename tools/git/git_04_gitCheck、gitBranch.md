# 1、git checkout

- a、操作文件 
- b、操作分支 <br>
- 一般和`git branch`一起使用 <br>
- [git branch 和 git checkout](https://www.cnblogs.com/qianqiannian/p/6011404.html)

```
//操作文件
git checkout filename  //放弃单个文件的修改
git checkout .  //放弃当前目录下的修改

//操作分支
git checkout master  //将分支切换到master
git checkout -b master  //如果分支存在则只切换分支，若不存在则创建并切换到master分支
                        //repo start是对git checkout -b这个命令的封装，将所有仓库的分支都切换到master，master是分支名
git checkout -b new_branch old_branch // 复制一个已有分支

//查看帮助
git checkout --help
git checkout -h
```

# 2、git branch 
- 一般用于分支的操作，比如创建分支，查看分支等等<br>
- 一般和`git checkout`一起使用<br>
- [git branch 和 git checkout](https://www.cnblogs.com/qianqiannian/p/6011404.html)

```
git branch  //不带参数：列出本地已经存在的分支，并且在当前分支的前面用"*"标记
git branch -r  //查看远程版本库分支列表

//另外，如果本地看不到远程分支，可以先[git fetch]更新remote索引，之后就可以看到了
//https://blog.csdn.net/liuniansilence/article/details/73832642
git branch -a  //查看所有分支列表，包括本地和远程

git branch dev  //创建名为dev的分支，创建分支时需要是最新的环境，创建分支但依然停留在当前分支
git branch -d dev  //删除dev分支，如果在分支中有一些未merge的提交，那么会删除分支失败
git branch -D dev  //强制删除dev分支
git branch -vv   //可以查看本地分支对应的远程分支
git branch -m oldName newName //给分支重命名
```