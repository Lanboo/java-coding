[toc]

# 常量池
- 类文件中常量池（静态常量池）
- 运行时常量池
- String常量池

## 1、常量池
- 方法区的一部分
### 1.1、类文件中常量池（静态常量池）
- Class文件的资源仓库，包含字符串(数字)字面量，还包含类、方法的信息，占用class文件绝大部分空间。
- 编译期大小已定
- 主要用于存放两大类常量：字面量(Literal)和符号引用量(Symbolic References)
   - `字面量`相当于Java语言层面常量的概念，如文本字符串，声明为final的常量值等
   - `符号引用`则属于编译原理方面的概念，包括了如下三种类型的常量
       - 类和接口的全限定名
       - 字段名称和描述符
       - 方法名称和描述符

### 1.2、运行时常量池
- 类加载完成后，把类的元信息、类文件中常量池的信息存储到方法区的常量池中，这个常量池通常称为运行时常量池
- 相对于CLass文件常量池的另外一个重要特征是<b>具备动态性</b>，Java语言并不要求常量一定只有编译期才能产生，也就是并非预置入CLass文件中常量池的内容才能进入方法区运行时常量池，运行期间也可能将新的常量放入池中，这种特性被开发人员利用比较多的就是String类的intern()方法。



### 1.3、String常量池
String的intern()方法：从String常量池中查找是否存在equals的String对象，存在则返回该equals对象的地址引用，不存在则把指定String对象存入String常量池中，同时返回存入常量池的对象的地址引用

``` java
public class StringConstantPool {
    public static void main(String[] args) {
        String a = "abc"; // 字面量会进入String常量池
        String b = "abc"; // String常量池已存在，直接返回a变量的地址引用
        String c = new String(a);// new，常见一个新的String对象，同时返回新的地址应用
        String d = c.intern();// String常量池，已存在"abc",故返回String常量池中的地址引用
        System.out.println(a == b); // true
        System.out.println(a == c); // false
        System.out.println(a == d); // true
        System.out.println(b == c); // false
        System.out.println(b == d); // true
        System.out.println(c == d); // false 
        //
        String aa = new String(new char[] { 'a', 'a' });
        String bb = aa.intern(); // String常量池中不存在"aa",所以把aa变量添加到String常量池中，同时把aa的地址引用返回给bb
        System.out.println(aa == bb); // true
    }
}
```

## JDK1.6

位于方法区，JDK1.6方法区是使用永久代实现的。

## JDK1.7
移除永久代的工作从JDK1.7就开始了。JDK1.7中，存储在永久代的部分数据就已经转移到了Java Heap或者是 Native Heap。但永久代仍存在于JDK1.7中，并没完全移除，譬如符号引用(Symbols)转移到了native heap；字面量(interned strings)转移到了java heap；类的静态变量(class statics)转移到了java heap。

## JDK1.8
String常量池在堆，并且-XX:PermSize -XX:MaxPermSize 参数已无效
