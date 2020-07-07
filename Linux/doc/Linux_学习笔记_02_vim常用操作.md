[toc]

# vim常用操作
> 为了查看日志方便，快速定位关键词。

``` shell
> grep -l key fileName   // 判断fileName是否存在key这个关键字，fileName可以用*
> vim xxx.log            // 打开某个文件
> gg                     // 移动到第一行
> G                      // 移动到最后一行
> ?key                   // 向后搜索key，并高亮显示
> /key                   // 向前搜索key
> ctrl b                 // 往回（向上）翻页
> ctrl f                 // 向前（向下）翻页
> yy                     // 复制一行到剪切板
> p                      // 粘贴到光标之后
> P                      // 粘贴到光标之前
> :q                     // 退出
> :wq                    // 保存并退出
> :q!                    // 强制退出
```

## `E325:ATTENTION`错误解决方法
> 这是由于在编辑该文件的时候异常退出了，因为vim在编辑文件时会创建一个交换文件swap file以保证文件的安全性。


``` shell
# 为了去掉这个警告，需要删除这个swp文件
# . + 文件名称 + .swp
rm .filename.swp
```

[特别全的VIM命令](https://www.cnblogs.com/yangjig/p/6014198.html)
