[toc]

### 八种基础数据类型的大小，以及他们的封装类
<div style = "font-size:13px">

基本类型|大小|最大值|最小值|默认值|封装类
:-|:-|:-|:-|:-|:-
byte|8bit|`-2^7`=`-128`|`2^7-1`=`127`|0|Byte
short|16bit|`-2^15`=`-32768`|`2^15-1`=`32767`|0|Short
int|32bit|`-2^31`=`-2178783648`|`2^31-1`=`2147483647`|0|Integer
long|64bit|`-2^63`=`-9,223,372,036,854,775,808`|`2^63 -1`=`9,223,372,036,854,775,807`|0L|Long
float|32bit|`1.4E-45`|`3.4028235E38`|0.0f|Float
double|64bit|`4.9E-324`|`1.7976931348623157E308`|0.0d|Double
char|16bit|\u0000（即0）|\uffff（即65,535）|-|Character
boolean|-|-|-|false|Boolean

</div>

### 引用数据类型
### switch是否能用String做参数
> 能。JDK1.7时，支持String作为switch的的参数；<br>
> 实现原理：利用hash+equals（如下注释内容）<br>
> 目前支持：char、byte、short、int、Character、Byte、Short、Integer、String
- 源码：基于1.8.0_201
    ``` java
    public class App {
        public static synchronized void main(String[] args) throws Exception {
            String str = "";
            switch(str) {
                case "AA":
                    System.out.println("AAAAA");
                    break;
                case "BB":
                    System.out.println("BBBBBB");
                    break;
                default:
                    System.out.println("CCCCC");
                    break;
            }
        }
    }
    ```
- 编译：javap -l -p -c App.class
    ```
    public static synchronized void main(java.lang.String[]) throws java.lang.Exception;
    Code:
        0: ldc           #19                 // String
        2: astore_1
        3: aload_1
        4: dup
        5: astore_2
        6: invokevirtual #21                 // Method java/lang/String.hashCode:()I
        9: lookupswitch  { // 2
                    2080: 36
                    2112: 48
                default: 82
            }
        36: aload_2
        37: ldc           #27                 // String AA
        39: invokevirtual #29                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z
        42: ifne          60
        45: goto          82
        48: aload_2
        49: ldc           #33                 // String BB
        51: invokevirtual #29                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z
        54: ifne          71
        57: goto          82
        60: getstatic     #35                 // Field java/lang/System.out:Ljava/io/PrintStream;
        63: ldc           #41                 // String AAAAA
        65: invokevirtual #43                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        68: goto          90
        71: getstatic     #35                 // Field java/lang/System.out:Ljava/io/PrintStream;
        74: ldc           #49                 // String BBBBBB
        76: invokevirtual #43                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        79: goto          90
        82: getstatic     #35                 // Field java/lang/System.out:Ljava/io/PrintStream;
        85: ldc           #51                 // String CCCCC
        87: invokevirtual #43                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        90: return
    ```
- 反编译工具：Java Decompiler 0.3.6
    ``` java
    public class App {
        public static synchronized void main(String[] args) throws Exception {
            String str = "";
            String str1;
            switch((str1 = str).hashCode()) {
                case 2080:   // AA 的 hashCode返回值
                    if(str1.equals("AA")) {  // 防止hash冲突
                        break;
                    }
                    break;
                case 2112:
                    if(!str1.equals("BB")) {
                        break label82;
                        System.out.println("AAAAA");
                        return;
                    }
                    else {
                        System.out.println("BBBBBB");
                    }
                    break;
            }
            label82:
            System.out.println("CCCCC");
        }
    }
    ```

### equals 与 == 的区别
> equals：值比较<br>

> == ：引用比较<br>
> Integer的==比较：因`java.lang.Integer.IntegerCache.cache`维护了缓存，在[-128,127]的范围内，会valueOf、自动装箱会直接返回已有对象的引用
```
Integer a = 12;
Integer b = Integer.valueOf(12);
Integer c = new Integer(12);
System.out.println(a==b);   // true
System.out.println(a==c);   // false
System.out.println(b==c);   // false
```
### 自动装箱
### 常量池
##### 类文件的常量池：
- 主次版本号之后就是常量池入口；<br>
- 常量池可以理解为Class文件之中的资源仓库，它是Class文件结构中与其他项目关联最多的数据项目，也是占用空间最大的数据项目之一，也是第一个出现的表类型数据项目。<br>
- 常量池中主要存放两大类常量：<b>字面量（Literal）</b>、<b>符号引用（Symbolic References）</b>
- 字面量（Literal）：比较接近Java语言层面的常量概念，如文本字符串、声明为final的常量值等。
- 符号引用（Symbolic References）：属于编译原理方面的概念，包括下面三类常量：
    - 类和接口的全限定名称（Fully Qualified Name）
    - 字段的名称和描述符
    - 方法的名称和描述符
##### 运行时常量池：
- 运行时常量池是方法区的一部分。
- 用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池中存放。
- 运行时常量池相对于Class文件常量池的另一个重要特征就是具备动态性，允许运行期间也可将常量放入池中，这种特性被开发人员利用比较多的就是String类的intern()方法。
- 方法区演变
    > ![方法区演变](etc/JVM方法区的演变.png)
### Object有哪些公用方法
- protected:`Object clone()`、`void finalize()`：
- `toString`、`getClass`
- `hashCode`、`equals`
- `wait()`、`wait(long)`、`wait(long,int)`
- `notify()`、`notifyAll()`
### Java的四种引用：强弱软虚;应用场景
#### 1.强弱软虚
- 强引用（Strong Reference）
    - 指程序代码中普遍存在的，类似`Object obj = new Object()`这种类似的引用。
    - 只要强引用还在，垃圾收集器<b>永远不会</b>回收被引用的对象
- 软引用（Soft Reference）
    - 用来描述一些还有用但非必须的对象。
    - 在系统将要<b>发生内存溢出异常之前</b>，这些对象将会被回收。
    - JDK1.2之后，提供了SoftReference来实现软引用。
    - 可以指定Queue，来获取对象被回收的时机。
- 弱引用（Weak Reference）
    - 用来描述非必须的对象。
    - 被引用的对象只能活到<b>下次GC</b>。
    - 可以指定Queue，来获取对象被回收的时机。
- 虚引用（Phantom Reference）
    - 称为幽灵引用或者幻影引用，它是最弱的一种引用关系。
    - 顾名思义，就是形同虚设，一个对象仅持有虚引用，那么它就和<b>没有任何引用一样</b>，随时都有可能被垃圾回收器回收。
    - 虚引用主要用来跟踪对象被垃圾回收器回收的活动。
    - 虚引用<b>必须</b>和引用队列 （ReferenceQueue）联合使用。
    - 当试图通过虚引用的`get()`方法取得强引用时，总是会失败。
    - 当虚引用所引用的对象已经执行完finalize函数的时候，就会把对象加到queue里面。
#### 2.软引用、弱引用的实现方式
``` java
public class App {

    static ReferenceQueue<String> refQueue = new ReferenceQueue<>();

    public static synchronized void main(String[] args) throws Exception {
        String str = new String("BBB");  // 对象强引用
        WeakReference<String> weakRef = new WeakReference<>(str, refQueue);
        str = null; // BBB外部的强引用置空，现在BBB的强引用只有Reference#referent
        System.out.println("GC前:弱引用对象=" + weakRef);
        System.out.println("GC前:弱引用对象的referent属性=" + weakRef.get());
        System.gc(); // 弱引用对象weakRef，在GC前，会把Reference#referent，即BBB没有任何引用指向它，那么GC可达性分析时就会被标记为可回收对象
        System.out.println("GC后:弱引用对象=" + weakRef);
        System.out.println("GC后:弱引用对象的referent属性=" + weakRef.get()); // 因Reference#referent被置空了，所以为空
        Reference<String> ref = (Reference<String>) refQueue.poll();
        System.out.println("GC后:引用队列获取的Reference对象=" + ref);
        System.out.println("GC后:引用队列获取的Reference对象的referent属性=" + ref.get());
    }
}

/*
GC前:弱引用对象=java.lang.ref.WeakReference@15db9742
GC前:弱引用对象的referent属性=BBB
GC后:弱引用对象=java.lang.ref.WeakReference@15db9742
GC后:弱引用对象的referent属性=null
GC后:引用队列获取的Reference对象=java.lang.ref.WeakReference@15db9742
GC后:引用队列获取的Reference对象的referent属性=null
*/
```
#### 3.虚引用
``` java
public class App {

    static ReferenceQueue<String> refQueue = new ReferenceQueue<>();

    public static synchronized void main(String[] args) throws Exception {
        // 因为PhantomReference重写了get()方法，故这里反射获取referent的值
        Field field = Reference.class.getDeclaredField("referent");
        field.setAccessible(true);
        
        String str = new String("BBB");  // 对象强引用
        PhantomReference<String> phantomRef = new PhantomReference<>(str, refQueue);
        str = null; // BBB外部的强引用置空，现在BBB的强引用只有Reference#reference
        System.out.println("GC前:虚引用对象=" + phantomRef);
        System.out.println("GC前:虚引用对象的referent属性=" + field.get(phantomRef));
        System.gc(); 
        System.out.println("GC后:虚引用对象=" + phantomRef);
        System.out.println("GC后:虚引用对象的referent属性=" + field.get(phantomRef)); // 因Reference#reference被置空了，所以
        Reference<String> ref = (Reference<String>) refQueue.poll();
        System.out.println("GC后:引用队列获取的Reference对象=" + ref);
        System.out.println("GC后:引用队列获取的Reference对象的referent属性=" + field.get(ref));
    }
}

/*
GC前:虚引用对象=java.lang.ref.PhantomReference@6d06d69c
GC前:虚引用对象的referent属性=BBB
GC后:虚引用对象=java.lang.ref.PhantomReference@6d06d69c
GC后:虚引用对象的referent属性=BBB
GC后:引用队列获取的Reference对象=java.lang.ref.PhantomReference@6d06d69c
GC后:引用队列获取的Reference对象的referent属性=BBB
*/
```
##### 结论
> 相比软引用、弱引用，在GC时，虚引用的`referent`属性并不会被置空，所以在出引用队列之前（Reference#clear()或者把虚引用对象本身不可达），被引用的对象一直不会被回收的。

### hashCode的作用
- 主要用于查找的快捷性，利用哈性算法（散列算法），快速的定位某个元素在集合中的位置，比如HashMap

### 重写equals后为什么要重写hashCode
- 自定义对象因属性众多，往往会重写equals方法，自己决定两个对象是怎么样相等的
- 另外，一般情况下，当两个对象相等，那么这两个对象的hashCode也要相等，所以要重写
    - 不然就是Object#hashCode方法，这是一个native方法，JVM会根据内存地址hash计算而来，两个对象的内存地址肯定是不一样的，所以JVM计算而来的hash值往往也不一样

### HashMap的hashCode的作用
在JDK1.7中，HashMap内是数据+链表的方式存储KV值的，在JDK1.8中，是以数组+链表/红黑色的方式存储KV值，不管怎样，都是先定位数组下标，在用equals方法比较K值后获取V值的。

而对象的hashCode就是为了计算数组下标的，计算方式如下
1. 先获取Key的hashCode：`h = key == null? 0 : key.hashCode();`
2. 在与高16位异或运算：`h = h ^ (h >>> 16) `
3. 模除数组长度：`i = (n - 1) & h`，其中，n=数组长度
### 为什么重载hashCode方法
- 一般来说，当两个对象equals相等了，那么hashCode的值也是相等的，所以重写equals方法后，也要重写hashCode方法
- 在集合中（比如HashMap），先利用hashCode定位位置，位置冲突之后用equals方法比较，不重写hashCode的，往往位置都不一样，那么就不会冲突，从而导致集合中存在多个相同的元素。
### ArrayList、LinkedList、Vector的区别
- ArrayList、Vector基于数组实现，扩容时复制整个数组数据到新的数组里
- LinkedList基于链表实现，在数据量大的情况下，add、remove性能较好
- Vector方法上有`synchronized`关键字，所以是线程安全的
- 都允许存放`null`值
### String、StringBuffer、StringBuilder
- StringBuffer、StringBuilder的父类：AbstractStringBuilder
- StringBuffer、StringBuilder的内部数组会自动扩容
- String的内部字符数组也被final修饰了，故不可变
- StringBuffer的方法有`synchronized`关键字，所以是线程安全的
- JDK1.5之后，对String的+运算进行了编译期的优化，会自动编译成StringBuilder，并使用append()
### Map、Set、List、Queue、Stack的特点与用法
- Map
    - K-V形式，键值对，其中K不能重复，Java中，K、V只能为引用类型不能是基本数据类型
    - 某些映射实现可以确保顺序，如TreeMap类
    - 另一些映射不能保证顺序，如HashMap类
    - keySet()、values()、entrySet()
- Set
    - 不允许重复的集合对象
    - TreeSet是有序的
    - iterator()单向访问
- List
    - 允许重复且有序的集合对象
    - 可访问任意包括在内的元素
    - 可在任意位置上增删元素
    - iterator()单向访问
    - listIterator()双向访问
- Queue
    - 先进先出
    - 用offer()加入元素
    - 用poll()来获取移除元素
    - 用element()或peek()获得前端元素；
- Stack
    - 先进后出、后进先出
    - 是一个类，继承Vector，所以是线程安全的
    - push、pop、peek、empty、search等方法
### HashMap和Hashtable的区别
- HashMap线程不安全,Hashtable线程安全
- HashMap继承自AbstractMap类；Hashtable继承自Dictionary类，Dictionary类是一个已经被废弃的类
- HashMap的KV都允许为null，Hashtable的KV都不允许为null
- hashCode计算方式不一致
    - HashMap
        1. 先获取Key的hashCode：`h = key == null? 0 : key.hashCode();`
        2. 在与高16位异或运算：`h = h ^ (h >>> 16) `
        3. 模除数组长度：`i = (n - 1) & h`，其中，n=数组长度
    - Hashtable
        - `index = (key.hashCode() & 0x7FFFFFFF) % tab.length`
- 扩容：HashMap扩容为2n，Hashtable扩容为2n+1
- hash冲突时，JDK1.8中，HashMap可以是红黑树，Hashtable还是链表

### JDK7和JDK8中HashMap的实现
[Java7/8 中的 HashMap 和 ConcurrentHashMap 全解析（上）](https://mp.weixin.qq.com/s/Gdu5D05cOizl1juG-NHLvg)

[Java7/8 中的 HashMap 和 ConcurrentHashMap 全解析（下）](https://mp.weixin.qq.com/s/240B5tg_ykwuEJVrOOYNtg)
### HashMap和ConcurrentHashMap的区别，HashMap的底层源码
[Java7/8 中的 HashMap 和 ConcurrentHashMap 全解析（上）](https://mp.weixin.qq.com/s/Gdu5D05cOizl1juG-NHLvg)

[Java7/8 中的 HashMap 和 ConcurrentHashMap 全解析（下）](https://mp.weixin.qq.com/s/240B5tg_ykwuEJVrOOYNtg)
### ConcurrentHashMap能完全代替Hashtable吗
Hashtable虽然性能上不如ConcurrentHashMap，但并不能完全被取代，两者的迭代器的一致性不同的，Hashtable的迭代器是强一致性的，而concurrenthashmap是弱一致的。 ConcurrentHashMap的get，clear，iterator 都是弱一致性的。 Doug Lea 也将这个判断留给用户自己决定是否使用ConcurrentHashMap。
### HashMap为什么不是线程安全的
没有加锁，当多个线程同时操作同一位置的数据时，就会发生数据不一致问题
### 如何使HashMap线程安全

### 多并发情况下，HashMap是否会产生死循环
### TreeMap、HashMap、LindedHashMap的区别
### Collection包结构、与Collections的区别
### try-catch-finally，try中有return，finally是否执行
### Excption与Error的包结构，OOM你遇到哪些情况，SOF有哪些情况
### Java OOP(面向对象)的三个特征与含义
### Override、Overload
### Interface、abstract类的区别
### Java多态实现原理
### foreach与正常for循环的效率对比
### IO、NIO
### 反射的作用和原理
### 泛型常用特点
