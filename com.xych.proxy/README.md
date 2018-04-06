[toc]

## 动态代理的底层原理
### 1、静态代理
> 将`额外要做的事情`和`需要被代理的事情`耦合带一块。


``` java
// 需要做的事情
public class Dao
{
    public void save()
    {
        System.out.println("Dao:保存了一个Pojo信息");
    }
}
```

``` java
// 额外做的事情，比如登录时记录登录ip，某个动作之前判断权限够不够
public class Log
{
    public void doLog()
    {
        System.out.println("Log:记录一些信息");
    }
}
```

``` java
// 代理之后
public class StaticProxy
{
    private Dao dao = null;
    private Log log = null;

    public StaticProxy(Dao dao, Log log)
    {
        super();
        this.dao = dao;
        this.log = log;
    }

    public void save()
    {
        System.out.println("StaticProxy:开始代理");
        this.log.doLog();
        this.dao.save();
        System.out.println("StaticProxy:代理结束了");
    }
}
```

``` java
// 使用
public class StaticProxyTest
{
    public static void main(String[] args)
    {
        StaticProxy staticProxy = new StaticProxy(new Dao(), new Log());
        staticProxy.save();
    }
}
```
> 从上面看出，`StaticProxy`这个代理类是依靠`Dao`和`Log`的，并且这个代理类智能用于`Dao.save`这个事情上，而且额外事情只能是`Log.doLog`这一件事情，这就意味着，每一个代理事件都必须存在这样一个类，必然的会产生n多代理类。

> 不能接受，，，

### 2、动态代理的使用
#### 2.1、基于接口的动态代理（使用的JDK自带的API）

``` java
// 有两件事情要做
public interface IDao
{
    void save();
    void update();
}
```

``` java
// 这两件事情第一种实现方式
public class DaoAImpl implements IDao
{
    @Override
    public void save()
    {
        System.out.println("IDao的A实现类：保存了一个Pojo");
    }

    @Override
    public void update()
    {
        System.out.println("IDao的A实现类：修改了一个Pojo");
    }
}
```

``` java
// 这两件事情第二种实现方式
public class DaoBImpl implements IDao
{
    @Override
    public void save()
    {
        System.out.println("IDao的B实现类：保存了一个Pojo");
    }

    @Override
    public void update()
    {
        System.out.println("IDao的B实现类：修改了一个Pojo");
    }
}
```

``` java
// 这两件事情对应的额外事件
public class Log
{
    public void doSaveLog()
    {
        System.out.println("Log:save");
    }

    public void doUpdateLog()
    {
        System.out.println("Log:update");
    }
}
```

``` java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 实现InvocationHandler，用来它来产生代理类
public class JdkProxy implements InvocationHandler
{
    private Object target = null;
    private Log log = null;

    public <T> JdkProxy(T target, Log log)
    {
        super();
        this.target = target;
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance()
    {
        Class<?> clazz = this.target.getClass();
        /**
         * Proxy.newProxyInstance 用来产生某接口的代理类
         * 第一个参数：被代理类的类加载器
         * 第二个参数：被代理类的所有接口
         * 第三个参数：InvocationHandler，这里面定义了invoke方法。当代理类调用某方法时，实际上执行的是invoke方法
         */
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    /**
     * 这是代理后代理类在调用任意一个方法的入口
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if(method.getName().startsWith("save"))
        {
            System.out.println("JdkProxy:走的是save方法");
            log.doSaveLog();
            return method.invoke(this.target, args);
        }
        else if(method.getName().startsWith("update"))
        {
            System.out.println("JdkProxy:走的是update方法");
            log.doUpdateLog();
            return method.invoke(this.target, args);
        }
        return method.invoke(this.target, args);
    }
}
```

``` java
// 使用
public class JdkProxyTest
{
    public static void main(String[] args)
    {
        IDao daoA = new DaoAImpl();
        IDao daoB = new DaoBImpl();
        Log log = new Log();
        JdkProxy proxyA = new JdkProxy(daoA, log);
        JdkProxy proxyB = new JdkProxy(daoB, log);
        IDao proxyDaoA = proxyA.getInstance();
        IDao proxyDaoB = proxyB.getInstance();
        proxyDaoA.save();
        System.out.println("--------------===========--------------------");
        proxyDaoB.update();
    }
}
/* 运行结果
JdkProxy:走的是save方法
Log:save
IDao的A实现类：保存了一个Pojo
--------------===========--------------------
JdkProxy:走的是update方法
Log:update
IDao的B实现类：修改了一个Pojo
*/
```

> `JdkProxy`实现`InvocationHandler`，它的作用是定义一个代理类产生<b>后</b>，代理类每调用一个方法，就会执行`invoke`方法

<h6>注意，`JdkProxy`的`getInstance`方法只是方便代码，如下写法也是正确的。</h6>

``` java
IDao daoA = new DaoAImpl();
Log log = new Log();
IDao proxyDaoA = (IDao) Proxy.newProxyInstance(IDao.class.getClassLoader(), 
                                               new Class[] { IDao.class }, 
                                               new JdkProxy(daoA, log));
proxyDaoA.save();
```
<h6>我想说的是，我们应该重点关注invoke方法，getInstance只是一个不中要的的方法，完全可以抽取到一个工厂类里</h6>

这里画一下类图吧。

![proxy-jdk-1](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-jdk-1.png?raw=true)



#### 2.2、基于类继承关系的动态代理（使用的cglib.jar自带的API）

> 注意：cglib.jar依赖asm.jar

``` java
// IDao、DaoAImpl、DaoBImpl、Log   //这些类的代码与Jdk代理是一样的
```

``` java
import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor
{
    private Object target = null;
    private Log log = null;

    public <T> CglibProxy(T target, Log log)
    {
        super();
        this.target = target;
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance()
    {
        //如果出现异常：Exception in thread "main" java.lang.NoClassDefFoundError: org/objectweb/asm/Type
        //原因：因为很多字节码操作都用到asm.jar，故引入即可
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable
    {
        if(method.getName().startsWith("save"))
        {
            System.out.println("CglibProxy:走的是save方法");
            log.doSaveLog();
            return method.invoke(this.target, args);
        }
        else if(method.getName().startsWith("update"))
        {
            System.out.println("CglibProxy:走的是update方法");
            log.doUpdateLog();
            return method.invoke(this.target, args);
        }
        return method.invoke(this.target, args);
    }
}
```
<h6>同样，我们应该重点关注intercept方法，getInstance完全可以抽取到一个工厂类里</h6>

``` java
public class CglibProxyTest
{
    public static void main(String[] args)
    {
        DaoAImpl daoA = new DaoAImpl();
        DaoBImpl daoB = new DaoBImpl();
        Log log = new Log();
        CglibProxy proxyA = new CglibProxy(daoA, log);
        CglibProxy proxyB = new CglibProxy(daoB, log);
        DaoAImpl proxyDaoA = proxyA.getInstance();
        DaoBImpl proxyDaoB = proxyB.getInstance();
        proxyDaoA.save();
        System.out.println("--------------===========--------------------");
        proxyDaoB.update();
    }
}
/* 运行结果
CglibProxy:走的是save方法
Log:save
IDao的A实现类：保存了一个Pojo
--------------===========--------------------
CglibProxy:走的是update方法
Log:update
IDao的B实现类：修改了一个Pojo
*/
```

#### 2.3、 提个问题？
在`InvocationHandler.invoke`和`MethodInterceptor.intercept`方法中，第一个参数指的是什么？
（后面有解答）

### 3、代理出的类到底是什么？
#### 3.1、JDK动态代理
![JDK-proxyDaoA](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-jdk-calss-a.png?raw=true) ![JDK-proxyDaoB](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-jdk-calss-b.png?raw=true)

留意下图中的h变量，等下讲

#### 3.2、Cglib动态代理
![Cglib-proxyDaoA](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-cglib-calss-a.png?raw=true) ![Cglib-proxyDaoB](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-cglib-calss-b.png?raw=true)

#### 3.3、使用总结

|类型|特点|
|:-|:-|
|JDK|1、使用的是接口关系<br>2、不能用于没有接口的类中|
|Cglib|1、使用的是继承关系<br>2、不能用于被`final`修饰的类|

### 4、反编译JDK动态代理生成的Class字节码
#### 4.1、JDK动态代理中`$Proxy0`这个类怎么来的？
参考：[JDK动态代理实现原理](http://rejoy.iteye.com/blog/1627405)


（下面代码基于JDK1.8.0_152"）
``` java
// com.xych.proxy.jdkproxy.JdkProxy.getInstance()
Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
```

``` java
// java.lang.reflect.Proxy.newProxyInstance(ClassLoader, Class<?>[], InvocationHandler)
Class<?> cl = getProxyClass0(loader, intfs);
```

``` java
// java.lang.reflect.Proxy.getProxyClass0(ClassLoader, Class<?>...)
return proxyClassCache.get(loader, interfaces);
```

定位一下proxyClassCache
``` java
private static final WeakCache<ClassLoader, Class<?>[], Class<?>>
        proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());
```

看看ProxyClassFactory(这个源码在OpenJDK里)
参考[JDK动态代理的底层实现之Proxy源码分析](http://www.cnblogs.com/liuyun1995/p/8157098.html)
另外，想知道`WeakCache`是的原理，可以看一下[WeakCache缓存的实现机制](http://www.cnblogs.com/liuyun1995/p/8144676.html)
``` java
byte[] proxyClassFile = ProxyGenerator.generateProxyClass(proxyName,interfaces, accessFlags);
```
#### 4.2、反编译
由4.1可知，'Proxy'最终借助的是'ProxyGenerator'生成的字节码，而且是字节数组
所以这里我们把字节数组写到文件中，反编译，看看源码
``` java
public class JDProxyClass
{
    public static void main(String[] args) throws Exception
    {
        byte[] data = ProxyGenerator.generateProxyClass("$Proxy0", new Class[] { IDao.class });
        FileOutputStream os = new FileOutputStream("D:/$Proxy0.class");
        os.write(data);
        os.flush();
        os.close();
    }
}
```
使用[JD-GUI](http://jd.benow.ca/)反编译
(多说一句，截止20180406，JD-GUI官网有两个版本，分别是1.4.0和0.3.6，本人使用1.4.0反编译不出来，最后使用的是0.3.6版本的)

``` java
import com.xych.proxy.jdkproxy.IDao;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements IDao
{
    private static Method m1;
    private static Method m4;
    private static Method m2;
    private static Method m0;
    private static Method m3;

    public $Proxy0(InvocationHandler paramInvocationHandler) throws Throwable
    {
        super(paramInvocationHandler);
    }

    public final boolean equals(Object paramObject)
    {
        try
        {
            return ((Boolean) this.h.invoke(this, m1, new Object[] { paramObject })).booleanValue();
        }
        catch(Error | RuntimeException localError)
        {
            throw localError;
        }
        catch(Throwable localThrowable)
        {
            throw new UndeclaredThrowableException(localThrowable);
        }
    }

    public final void save()
    {
        try
        {
            this.h.invoke(this, m4, null);
            return;
        }
        catch(Error | RuntimeException localError)
        {
            throw localError;
        }
        catch(Throwable localThrowable)
        {
            throw new UndeclaredThrowableException(localThrowable);
        }
    }

    public final String toString()
    {
        try
        {
            return (String) this.h.invoke(this, m2, null);
        }
        catch(Error | RuntimeException localError)
        {
            throw localError;
        }
        catch(Throwable localThrowable)
        {
            throw new UndeclaredThrowableException(localThrowable);
        }
    }

    public final int hashCode()
    {
        try
        {
            return ((Integer) this.h.invoke(this, m0, null)).intValue();
        }
        catch(Error | RuntimeException localError)
        {
            throw localError;
        }
        catch(Throwable localThrowable)
        {
            throw new UndeclaredThrowableException(localThrowable);
        }
    }

    public final void update()
    {
        try
        {
            this.h.invoke(this, m3, null);
            return;
        }
        catch(Error | RuntimeException localError)
        {
            throw localError;
        }
        catch(Throwable localThrowable)
        {
            throw new UndeclaredThrowableException(localThrowable);
        }
    }

    static
    {
        try
        {
            m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
            m4 = Class.forName("com.xych.proxy.jdkproxy.IDao").getMethod("save", new Class[0]);
            m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
            m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
            m3 = Class.forName("com.xych.proxy.jdkproxy.IDao").getMethod("update", new Class[0]);
            return;
        }
        catch(NoSuchMethodException localNoSuchMethodException)
        {
            throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
        }
        catch(ClassNotFoundException localClassNotFoundException)
        {
            throw new NoClassDefFoundError(localClassNotFoundException.getMessage());
        }
    }
}
```
> 首先，`IDao`接口定义了两个方法`save`、`update`，加上`Object`类的`equals`、`toString`、`hashCode`这三个方法，总共5个方法，即上面代码中的m1-m5成员变量，并在'静态代码块'里初始化了这5个成员变量。

> 其次，`$Proxy0`继承`Proxy`继承，实现`IDao`接口。这里注意一下，在`Proxy`里有一个成员变量`h`。
> `protected InvocationHandler h;`

> 第三，`$Proxy0`重写`Object`类的三个公有方法，实现了`IDao`接口的两个方法，并且使`Proxy`的`h`成员变量，统一调用`invoke`方法。
此处可以回答2.3的问题。

另外，关于Cglib的原理，跟着差不多，都是在内存中生成字节码->加载->new处代理类对象，使用的时候，调用的是代理类中被重写的方法。

### 5、手动实现一套动态代理
本人比较懒，这里就没有实现。
说下逻辑吧。
从4.2的分析来看，可以猜测JDK动态代理大体上的过程如下
1. 生成class二进制文件
2. 加载此二进制到JVM虚拟机中，即使之成为Class的一个对象
3. 根据反射，创建代理类的实例对象
4. 调用者使用

所以这里，我们可以这样做：
1. 仿照4.2的反编译的源码，String拼接出一个代理类的Java源代码
2. 将生成的源代码输出到本地磁盘，保存成`.java`文件
3. 编译java文件，生成`.class`文件
4. 加载class文件
5. 根据反射，创建实例对象
6. 调用者使用 

步骤1、2、4、5，这些都是经常接触的，就不多说了，这里说下步骤3，怎么将`.java`文件编程生成`.class`文件。
``` java
File f; //指向Java文件
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
StandardJavaFileManager fileManager  = compiler.getStandardFileManager(null, null, null);
Iterable<? extends JavaFileObject> iterable = fileManager.getJavaFileObjects(f);
JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, iterable);
task.call();
fileManager.close();
```

### 6、JDK动态代理类图的扩展

从2.1可以知道，在本文中，JDK代理的类图是下面的样子。

![proxy-jdk-1](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-jdk-1.png?raw=true)

另外，JDK动态代理的类图还有一种形式,（比如MyBatis中Mapper的代理）

![proxy-jdk-1](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-jdk-2.png?raw=true)

我们在来看一下JdkProxy这个类
``` java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 实现InvocationHandler，用来它来产生代理类
public class JdkProxy implements InvocationHandler
{
    private Object target = null;
    private Log log = null;

    public <T> JdkProxy(T target, Log log)
    {
        super();
        this.target = target;
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance()
    {
        Class<?> clazz = this.target.getClass();
        /**
         * Proxy.newProxyInstance 用来产生某接口的代理类
         * 第一个参数：被代理类的类加载器
         * 第二个参数：被代理类的所有接口
         * 第三个参数：InvocationHandler，这里面定义了invoke方法。当代理类调用某方法时，实际上执行的是invoke方法
         */
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);// 2处
    }

    /**
     * 这是代理后代理类在调用任意一个方法的入口
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if(method.getName().startsWith("save"))
        {
            System.out.println("JdkProxy:走的是save方法");
            log.doSaveLog();
            return method.invoke(this.target, args);        // 1处  1.1
        }
        else if(method.getName().startsWith("update"))
        {
            System.out.println("JdkProxy:走的是update方法");
            log.doUpdateLog();
            return method.invoke(this.target, args);        // 1处  1.2
        }
        return method.invoke(this.target, args);            // 1处  1.3
    }
}
```
在`JdkProxy.invoke`中，使用了`target`这个Object类型的成员变量，而且这个成员变量是在构造方法中赋的值。
`target`既然是一个对象，而且在`1处`被method（可以看下4.2）使用了，那么`target`肯定是`2处`第二个参数的实现类。
那么，问：如果`target`不是`2处`第二个参数的实现类，那么`1处`怎么写？

可以在1.1这样写：`return target.update(args);`


再来看第二种类图：

![proxy-jdk-1](https://github.com/Lanboo/resource/blob/master/images/JavaCoding/proxy-jdk-2.png?raw=true)

如果JdkProxy中传入的target跟IDao没有实现关系，在那在`1处`我们应该想办法接着调用。



