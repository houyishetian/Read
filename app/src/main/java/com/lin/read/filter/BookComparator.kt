package com.lin.read.filter

import java.lang.Exception

class BookComparator(private val sortType: SortType, private val bookType: BookType) : Comparator<BookInfo> {
    override fun compare(o1: BookInfo?, o2: BookInfo?): Int {
        return this.takeIf { o1 != null && o2 != null }?.let {
            when (sortType) {
                SortType.ASCEND -> sortByAscend(o1!!, o2!!)
                SortType.DESCEND -> -sortByAscend(o1!!, o2!!)
            }
        } ?: 0
    }

    private fun sortByAscend(book0: BookInfo, book1: BookInfo): Int {
        getSortPriority(bookType).let { bookTypeList ->
            bookTypeList.forEach { bookType ->
                sortByType(book0, book1, bookType).let { sortResult ->
                    sortResult.takeIf { it != 0 }?.run {
                        return this
                    }
                }
            }
        }
        return 0
    }

    private fun getSortPriority(bookType: BookType): List<BookType> {
        return mutableListOf<BookType>().apply {
            add(BookType.POSTION)
            add(BookType.SCORE)
            add(BookType.SCORE_NUM)
            add(BookType.RAISE)
            add(BookType.COMMENT)
            add(BookType.WORDS_NUM)
            add(BookType.RECOMMEND)
            add(BookType.VIP_CLICK)
            remove(bookType)
            add(0, bookType)
        }
    }

    private fun sortByType(book0: BookInfo, book1: BookInfo, bookType: BookType): Int {
        return try {
            when (bookType) {
                BookType.POSTION -> book0.position.let { book0.position - book1.position }
                BookType.SCORE -> book0.score?.let { (book0.score.toFloat() - book1.score.toFloat()).times(100).toInt() } ?: 0
                BookType.SCORE_NUM -> book0.scoreNum?.let { (book0.scoreNum.toFloat() - book1.scoreNum.toFloat()).times(100).toInt() } ?: 0
                BookType.WORDS_NUM -> book0.wordsNum?.let { (book0.wordsNum.toFloat() - book1.wordsNum.toFloat()).times(100).toInt() } ?: 0
                BookType.RECOMMEND -> book0.recommend?.let { (book0.recommend.toFloat() - book1.recommend.toFloat()).times(100).toInt() } ?: 0
                BookType.VIP_CLICK -> book0.click?.let { (book0.click.toFloat() - book1.click.toFloat()).times(100).toInt() } ?: 0
                BookType.RAISE -> book0.raiseNum?.let { book0.raiseNum.toInt() - book1.raiseNum.toInt() } ?: 0
                BookType.COMMENT -> book0.commentNum?.let { book0.commentNum.toInt() - book1.commentNum.toInt() } ?: 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    enum class SortType {
        ASCEND, DESCEND
    }

    enum class BookType {
        SCORE, SCORE_NUM, WORDS_NUM, RECOMMEND, VIP_CLICK, POSTION, RAISE, COMMENT
    }
}