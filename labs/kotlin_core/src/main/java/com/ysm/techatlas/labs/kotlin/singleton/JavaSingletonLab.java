package com.ysm.techatlas.labs.kotlin.singleton;

/**
 * Java 版本的核心单例模式实现与考点
 */
public class JavaSingletonLab {

    // =================================================================================
    // 1. 饿汉式 (Eager)
    // 优点：简单，基于类加载机制，绝对线程安全。
    // 缺点：不能懒加载，如果初始化耗时且后续没用到，会浪费内存。
    // =================================================================================
    public static class EagerSingleton {
        public static final EagerSingleton INSTANCE = new EagerSingleton();
        private EagerSingleton() {}
    }

    // =================================================================================
    // 2. 双重检查锁 (Double-Checked Locking, DCL) [面试最高频]
    // 优点：懒加载，且只在第一次实例化时加锁，后续零开销。
    // =================================================================================
    public static class DclSingleton {
        // 【核心考点】：必须加 volatile！防止指令重排导致拿到半初始化对象。
        private static volatile DclSingleton instance;

        private DclSingleton() {}

        public static DclSingleton getInstance() {
            if (instance == null) { // 第一次检查：避免已经实例化后的无谓加锁
                synchronized (DclSingleton.class) {
                    if (instance == null) { // 第二次检查：防止多个线程同时通过了第一层检查而在锁外等待
                        instance = new DclSingleton();
                    }
                }
            }
            return instance;
        }
    }

    // =================================================================================
    // 3. 静态内部类 (Static Inner Class) [最推荐的无参单例]
    // 优点：完美结合了懒加载和类加载器的线程互斥机制，完全去除了 synchronized 的性能损耗。
    // =================================================================================
    public static class InnerClassSingleton {
        private InnerClassSingleton() {}

        // 外部类被加载时，这个内部类不会被加载。
        private static class SingletonHolder {
            // JVM 保证类的 <clinit> 初始化过程是线程安全的。
            private static final InnerClassSingleton INSTANCE = new InnerClassSingleton();
        }

        public static InnerClassSingleton getInstance() {
            // 只有这里被调用时，SingletonHolder 才会被加载，触发对象的创建
            return SingletonHolder.INSTANCE;
        }
    }

    // =================================================================================
    // 4. 枚举单例 (Enum) [绝对防御]
    // 优点：防御反射攻击、防御反序列化攻击。
    // =================================================================================
    public enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {
            System.out.println("[JavaSingleton] Enum is doing something!");
        }
    }

    public static void runTests() {
        System.out.println("--- Java Singleton Tests ---");
        System.out.println("[JavaSingleton] DclSingleton: " + DclSingleton.getInstance().hashCode());
        System.out.println("[JavaSingleton] InnerClassSingleton: " + InnerClassSingleton.getInstance().hashCode());
        EnumSingleton.INSTANCE.doSomething();
    }
}
