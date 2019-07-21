package com.lin.read

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
}
