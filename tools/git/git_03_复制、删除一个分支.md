# 1、复制一个分支
``` git 
# 复制分支
$ git checkout -b new_branch old_branch
# 提交到远程
$ git push -u origin new_branch
```

# 2、删除一个分支
``` git
$ git branch -a                              # 查看远程本地所有的分支
$ git branch -d branch_name                  # 删除本地分支
$ git push origin --delete branch_name       # 删除远程分支
```
