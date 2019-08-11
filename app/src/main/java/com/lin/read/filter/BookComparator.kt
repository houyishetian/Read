package com.lin.read.filter

import java.lang.Exception

class BookComparator(private val sortType: SortType, private val bookType: BookType) : Comparator<ScanBookBean> {
    override fun compare(o1: ScanBookBean?, o2: ScanBookBean?): Int {
        return this.takeIf { o1 != null && o2 != null }?.let {
            when (sortType) {
                SortType.ASCEND -> sortByAscend(o1!!, o2!!)
                SortType.DESCEND -> -sortByAscend(o1!!, o2!!)
            }
        } ?: 0
    }

    private fun sortByAscend(book0: ScanBookBean, book1: ScanBookBean): Int {
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
            add(BookType.WORDS_NUM)
            remove(bookType)
            add(0, bookType)
        }
    }

    private fun sortByType(book0: ScanBookBean, book1: ScanBookBean, bookType: BookType): Int {
        return try {
            when (bookType) {
                BookType.POSTION -> book0.position.let { book0.position - book1.position }
                BookType.SCORE -> book0.score.let { (book0.score.toFloat() - book1.score.toFloat()).times(100).toInt() }
                BookType.SCORE_NUM -> book0.scoreNum.let { (book0.scoreNum.toFloat() - book1.scoreNum.toFloat()).times(100).toInt() }
                BookType.WORDS_NUM -> book0.wordsNum.let { (book0.wordsNum.toFloat() - book1.wordsNum.toFloat()).times(100).toInt() }
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
        SCORE, SCORE_NUM, WORDS_NUM, POSTION
    }
}