[toc]
<style>
.xych-table td,.xych-table th
{
    padding: 4px;
}
</style>
#常用命令

## 1、基本命令
<div class="xych-table" style="font-size:16px;">

命令|说明|其他
:-|:-|:- 
`mkdir dirName`|创建文件夹
`tar -zxvf fileName`|解压`.tar.gz`文件|`-zxvf` 可以记成“至 小威风”
`rm -rf file`|递归删除文件或文件夹，<b>不可恢复</b>

</div>
## 命令详解

### `rm`命令
参考：[rm命令](http://man.linuxde.net/rm)

- 语法<br>
`rm (选项)(参数)`

- 选项
<div class="xych-table" style="font-size:16px;">

选项|说明
:-:|:-
`-d`|直接把欲删除的目录的硬连接数据删除成0，删除该目录；
`-f`|强制删除文件或目录；
`-i`|删除已有文件或目录之前先询问用户；
`-r`、`-R`|递归处理，将指定目录下的所有文件与子目录一并处理；
`--preserve-root`|不对根目录进行递归操作；
`-v`|显示指令的详细执行过程。

- 参数<br>
文件：指定被删除的文件列表，如果参数中含有目录，则必须加上`-r`或者`-R`选项。

- 实例
``` shell
> rm -i test example
Remove test ?n（不删除文件test)
Remove example ?y（删除文件example)
```
</div>