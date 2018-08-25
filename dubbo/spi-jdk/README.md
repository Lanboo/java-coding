# JDK-SPI
## 1、SPI
> `SPI`是`Service Provider Interface`的缩写，可以使用它扩展框架和更换的组件。<br>
> `JDK`提供了`java.util.ServiceLoader`工具类，在使用某个服务接口时，它可以帮助我们查找该服务接口的实现类，加载和初始化，<b>前提条件是基于它的约定</b>。

>约定：<br>
>- 在jar包的`META-INF/services/`目录里同时创建一个以服务接口`全限定类名`命名的文件。
>- 该文件内容是接口的实现类的全限定类名。

## 2、JDBC中SPI的使用
``` java
public class MySQLConnect
{
    private final static String url ="jdbc:mysql://localhost:3306/test";
    private final static String username = "root";
    private final static String password = "root";

    public static Connection getConnection() throws SQLException {
        // JDBC4.0以前需要主动加载驱动类
        // Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());
    }
}
```
以上代码是经典的数据库连接方式。<br>
以上代码并没有声明MySql的数据库连接驱动<br>
但是在基于SPI的模式中，已经声明了。
![](https://github.com/Lanboo/resource/blob/master/images/java-coding/dubbo/jdk-spi-jdbc-mysql.png?raw=true)