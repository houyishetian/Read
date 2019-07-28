package com.lin.read

import com.lin.read.utils.ReflectUtil
import com.lin.read.utils.StringKtUtil
import org.junit.Test

class TestKt {
    @Test
    fun testTakeIf() {
        val list = listOf("bc", "a", "b", "ab", "123")
//        val filterReuslt = list.filter { it.contains("b") }.takeIf { it.isNotEmpty() }?.get(0) ?: null
//        println("${filterReuslt}")

        val filterReuslt2 = list.firstOrNull { it.contains("4545") }
        println("$filterReuslt2")
    }

    @Test
    fun testChapterHandler(){
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节 一"))

        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节(第5更)"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节[第5更]"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节【第5更】"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节（第5更）"))

        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节(第五更)"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节[第五更]"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节【第五更】"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节（第五更）"))

        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节(1)"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节[五]"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节【上】"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节（末）"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节（加更）"))
        println(StringKtUtil.removeUnusefulCharsFromChapter("1212 测试章节（一二）"))
    }

    @Test
    fun testGetDataFromContentByRegex() {
        println(StringKtUtil.getDataFromContentByRegex("wo ce shi %非常 好ffff测试《》,.<aaa>", "<[a]+>", listOf(0)))
        println(StringKtUtil.getDataFromContentByRegex("wo ce shi %非常 好ffff测试《》,.<aaa>", "<[a]+>", listOf(0),true))
        println(StringKtUtil.getDataFromContentByRegex("wo ce shi %非常 好ffff测试《》,.<aaa>", "[\\s\\S]+", listOf(0),true))
    }

    @Test
    fun testGetDataListFromContentByRegex() {
        println(StringKtUtil.getDataListFromContentByRegex("wo ce shi %非常 好ffff测试《》,.<aaa>", "([a-z]+) ([a-z]{1})", listOf(0,1,2)))
        println(StringKtUtil.getDataListFromContentByRegex("wo ce shi %非常 好ffff测试《》,.<aaa>", "acd", listOf(0,1,2)))
    }

    @Test
    fun testReplaceDataOfContentByRegex(){
        println(StringKtUtil.replaceDataOfContentByRegex("wo ce shi %非常 好ffff测试《》,.<aaa>", "([a-z]+) ([a-z]{1})"))
        println(StringKtUtil.replaceDataOfContentByRegex("ab12\nbc23\ncd34", "2"))
    }

    @Test
    fun testGetCurrentPageAndMaxPageForQiDian() {
        //		data-page="2" data-pageMax="5"></div>
        //       id="page-container" data-pageMax="5" data-page="1"
        println(StringKtUtil.getCurrentAndMaxPageForQiDian("data-page=\"2\" data-pageMax=\"5\"></div>"))
        println(StringKtUtil.getCurrentAndMaxPageForQiDian("id=\"page-container\" data-pageMax=\"5\" data-page=\"1\""))
    }

    @Test
    fun testInvoke() {
        class TestReflect {
            fun test(): String = "no params!"
            fun test(a: String, b: String): String = a + b
            fun test(a: String, b: String, c: String): String = a + b + c
            fun test(a: Int, b: Int): Int = a + b
            fun test(a: Short, b: Short): Short = (a + b).toShort()
            fun test(a: Byte, b: Byte): Byte = (a + b).toByte()
            fun test(a: Long, b: Long): Long = a + b
            fun test(a: Double, b: Double): Double = a + b
            fun test(a: Float, b: Float): Float = a + b
            fun test(a: Char): Char = a.plus(1)
            fun test(a: Boolean, b: Boolean): Boolean = a && b
        }

        val string = "0123456"
        println(ReflectUtil.invokeMethod(string, "substring", String::class.java, 0))
        println(ReflectUtil.invokeMethod(string, "substring", String::class.java, 0, 5))

        val testObj = TestReflect()
        println(ReflectUtil.invokeMethod(testObj, "test", String::class.java))
        println(ReflectUtil.invokeMethod(testObj, "test", String::class.java, "a", "b"))
        println(ReflectUtil.invokeMethod(testObj, "test", String::class.java, "a", "b", "c"))
        println("test int -> " + ReflectUtil.invokeMethod(testObj, "test", Int::class.java, 0, 3))
        println("test short -> " + ReflectUtil.invokeMethod(testObj, "test", Short::class.java, 0.toShort(), 3.toShort()))
        println("test byte -> " + ReflectUtil.invokeMethod(testObj, "test", Byte::class.java, 0.toByte(), 3.toByte()))
        println("test long -> " + ReflectUtil.invokeMethod(testObj, "test", Long::class.java, 0.toLong(), 3.toLong()))
        println("test double -> " + ReflectUtil.invokeMethod(testObj, "test", Double::class.java, 0.0, 3.0))
        println("test float -> " + ReflectUtil.invokeMethod(testObj, "test", Float::class.java, 0.0.toFloat(), 3.toFloat()))
        println("test char -> " + ReflectUtil.invokeMethod(testObj, "test", Char::class.java, 'a'))
        println("test boolean -> " + ReflectUtil.invokeMethod(testObj, "test", Boolean::class.java, false, false))
    }
}
