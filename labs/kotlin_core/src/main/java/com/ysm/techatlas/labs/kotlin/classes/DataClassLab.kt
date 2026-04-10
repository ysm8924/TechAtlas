package com.ysm.techatlas.labs.kotlin.classes

/**
 * Data Class 数据类验证室
 *
 * 建议 Decompile 观察：自动生成的 equals, hashCode, toString, copy 和 componentN。
 */
object DataClassLab {

    @PublishedApi
    internal const val TAG = "DataClassLab"

    // =================================================================================
    // 考点 1: 标准 Data Class
    // =================================================================================
    data class User(val name: String, val age: Int) {
        // 【面试坑点】：写在类体内部的属性，不会被纳入 copy/equals/toString/hashCode 的计算中！
        var isOnline: Boolean = false
    }

    // =================================================================================
    // 考点 2: 数组陷阱
    // =================================================================================
    data class ImageCache(val id: String, val bytes: ByteArray) {
        // 【大坑】：如果不在 data class 里手动重写 equals 和 hashCode，
        // Kotlin 默认对 ByteArray 比较的是"内存地址 (引用)"，而不是数组内容。
        // （因为底层调用的是 Arrays.equals() 还是 == 的问题，Kotlin data class 默认只生成普通 equals，数组就是对比引用）
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ImageCache
            if (id != other.id) return false
            if (!bytes.contentEquals(other.bytes)) return false // 必须手动 contentEquals
            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    fun runTests() {
        println("--- DataClassLab Tests ---")
        
        val u1 = User("Alice", 20).apply { isOnline = true }
        val u2 = u1.copy() // copy() 只拷贝主构造中的 name 和 age
        
        println("[$TAG] u1 == u2 ? ${u1 == u2}") // true, 因为 data class 的 equals 不关心 isOnline
        println("[$TAG] u2 isOnline ? ${u2.isOnline}") // false! 因为 copy 没拷贝类体属性

        val img1 = ImageCache("img1", byteArrayOf(1, 2, 3))
        val img2 = ImageCache("img1", byteArrayOf(1, 2, 3))
        // 如果没有手动重写 equals，这里会打印 false。重写后为 true。
        println("[$TAG] img1 == img2 ? ${img1 == img2}") 
    }
}