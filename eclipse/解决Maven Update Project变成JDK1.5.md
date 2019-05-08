# 解决Maven Update Project变成JDK1.5

参考：[解决maven update project 后项目jdk变成1.5](https://blog.csdn.net/Jay_1989/article/details/52687934)


## 方式1：在项目的pom.xml文件中添加如下配置
``` xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>2.3.2</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 方式2：在Maven配置文件setting.xml中配置
``` xml
<profiles>
    <profile>
        <id>jdk-1.8</id>
        <activation>
            <activeByDefault>true</activeByDefault>
            <jdk>1.8</jdk>
        </activation>
        <properties>
            <maven.compiler.source>1.8</maven.compiler.source>
            <maven.compiler.target>1.8</maven.compiler.target>
            <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
        </properties>
    </profile>
</profiles>
```
> 注意：<br>
> 1、需要在Eclipse中`Preferences - Maven - User Settings`配置setting.xml中设置。<br>
> 2、单独使用Maven需要在`%M2_HOME%/conf/settings.xml`中配置。


> 推荐第二种方式，减少改动量。