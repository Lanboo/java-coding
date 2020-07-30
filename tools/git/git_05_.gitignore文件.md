# `.gitignore`文件
## `.gitignore`文件，忽略指定文件、文件夹
> 首先，用`touch .gitignore`命令生成`.gitignore`文件，编辑改文件即可。

## `.gitignore`全局设置
> 在`C:\Users\xiaowei.wei\.gitconfig`里配置，如下
``` gitignore
[core]
    excludesfile = C:/Users/xiaowei.wei/.gitignore
[user]
    name = Lanboo
    email = xxxxx@qq.com
```

[Github官方准备的.gitignore文件](https://github.com/github/gitignore)

Java常用的：
``` shell
# Java
target
/**/target
bin/
*.class
.classpath
*.classpath
.project
*.project
/.settings
.settings
*.settings
.factorypath

#Intellij
.idea/
*.iml
/**/*.iml

# VSCode
*/.factorypath
```