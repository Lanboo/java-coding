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

现在看看`DriverManager`这个类(基于JDk8)
``` java
public class DriverManager {
    static {
        loadInitialDrivers();
        println("JDBC DriverManager initialized");
    }

    // 1、先看看在系统参数里是不是指定了加载类
    // 2、在使用ServiceLoader工具类来加载Driver的实现类
    private static void loadInitialDrivers() {
        String drivers;
        try {
            drivers = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("jdbc.drivers");
                }
            });
        } catch (Exception ex) {
            drivers = null;
        }

        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {

                ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
                Iterator<Driver> driversIterator = loadedDrivers.iterator();
                
                try{
                    while(driversIterator.hasNext()) {
                        driversIterator.next();
                    }
                } catch(Throwable t) {
                // Do nothing
                }
                return null;
            }
        });

        println("DriverManager.initialize: jdbc.drivers = " + drivers);

        if (drivers == null || drivers.equals("")) {
            return;
        }
        String[] driversList = drivers.split(":");
        println("number of Drivers:" + driversList.length);
        for (String aDriver : driversList) {
            try {
                println("DriverManager.Initialize: loading " + aDriver);
                // 在这里去加载驱动类
                Class.forName(aDriver, true,
                        ClassLoader.getSystemClassLoader());
            } catch (Exception ex) {
                println("DriverManager.Initialize: load failed: " + ex);
            }
        }
    }
}
```

## 3、Demo
![](https://github.com/Lanboo/resource/blob/master/images/java-coding/dubbo/jdk-spi-demo.png?raw=true)
### 3.1、接口定义
``` java
package com.xych.spi.jdk.api;

public interface IXychDriver
{
    public void connection(String path);
}
```

### 3.2、实现方式1
``` java
package com.xych.spi.jdk.mysql;

import com.xych.spi.jdk.api.IXychDriver;

public class MySqlDriver implements IXychDriver
{
    @Override
    public void connection(String path)
    {
        System.out.println("MySQL impl:path=" + path);
    }
}
```
### 3.3、实现方式2
``` java
package com.xych.spi.jdk.oracle;

import com.xych.spi.jdk.api.IXychDriver;

public class OracleDriver implements IXychDriver
{
    @Override
    public void connection(String path)
    {
        System.out.println("Oracle impl:path=" + path);
    }
}
```
### 3.4、使用
``` pom
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xych</groupId>
    <artifactId>spi-jdk-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spi-jdk-server</name>
    <url>http://maven.apache.org</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.xych</groupId>
            <artifactId>spi-jdk-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.xych</groupId>
            <artifactId>spi-jdk-mysql</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```
``` java
package com.xych.spi.jdk.server;

import java.util.ServiceLoader;

import com.xych.spi.jdk.api.IXychDriver;

public class App
{
    public static void main(String[] args)
    {
        ServiceLoader<IXychDriver> serviceLoader = ServiceLoader.load(IXychDriver.class);
        for(IXychDriver xychDriver : serviceLoader)
        {
            xychDriver.connection("xych");
        }
    }
}
/* 运行结果
MySQL impl:path=xych
*/
```
