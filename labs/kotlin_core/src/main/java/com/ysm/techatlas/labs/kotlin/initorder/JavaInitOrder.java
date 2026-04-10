package com.ysm.techatlas.labs.kotlin.initorder;

/**
 * Java 版本的初始化顺序对比参照类
 */
public class JavaInitOrder {

    // ================== 静态/类加载级别 ==================
    // 1. 静态属性赋值 (如果是先出现)
    public static JavaInitOrder instance = new JavaInitOrder("yzq", false);

    // 2. 静态代码块
    static {
        System.out.println("[JavaInitOrder] static block 1");
    }

    static {
        System.out.println("[JavaInitOrder] static block 2");
    }

    // ================== 实例级别 ==================
    // 3. 实例属性
    private boolean gender = true;

    // 4. 非静态代码块（与属性按从上到下顺序执行）
    {
        System.out.println("[JavaInitOrder] instance block 2, gender:" + gender);
    }

    {
        System.out.println("[JavaInitOrder] instance block 1");
    }

    // 5. 构造函数
    public JavaInitOrder() {
        // 在实际开发中，Java 的无参构造通常被其他重载构造通过 this() 调用
    }

    public JavaInitOrder(String name, boolean gender) {
        this(); // 隐式或显式调用父类/主构造
        System.out.println("[JavaInitOrder] constructor");
    }

    public static void runTests() {
        System.out.println("\n--- Triggering JavaInitOrder ---");
        // 因为在上面我们声明了 instance，此时触发类加载。
        // 但为了保证效果，我们可以再 new 一个对象观察第二次的实例化过程：
        new JavaInitOrder("test", true);
    }
}
