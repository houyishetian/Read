package com.lin.read.filter

import android.app.Activity
import android.widget.ListView
import com.lin.read.R
import com.lin.read.adapter.DialogSortTypeAdapter
import com.lin.read.utils.showFullScreenDialog
import java.util.*

class BookComparatorUtil {
    private val SORT_BY_DEFAULT = Pair("默认排序", 0)
    private val SORT_BY_SCORE = Pair("按分数排序", 1)
    private val SORT_BY_SCORE_NUM = Pair("按评分人数排序", 2)
    private val SORT_BY_WORDS_NUM = Pair("按总字数排序", 3)

    private var lastClickItem = SORT_BY_DEFAULT.second
    private var lastSortType: BookComparator.SortType? = null

    fun showSortDialog(activity: Activity, dataList: List<ScanBookBean>, sortCompleted: () -> Unit) {
        activity.showFullScreenDialog(R.layout.dialog_qidian_book_sort){dialog,view->
            val availableSortTypes = getAvaliableSortTypes(dataList[0])
            view.findViewById<ListView>(R.id.dialog_select_sort_type_lv).adapter = DialogSortTypeAdapter(activity,availableSortTypes).apply {
                setOnItemSortTypeClickListener {
                    Collections.sort(dataList, BookComparator(getSortType(it), getSortBookType(it)))
                    dialog.dismiss()
                    sortCompleted.invoke()
                }
            }
        }
    }

    fun sortByDefaultRules(dataList: List<ScanBookBean>, sortCompleted: () -> Unit) {
        Collections.sort(dataList, BookComparator(BookComparator.SortType.ASCEND, BookComparator.BookType.POSTION))
        sortCompleted.invoke()
    }

    private fun getSortType(currentClickId: Int): BookComparator.SortType {
        return (currentClickId.takeIf { it == SORT_BY_DEFAULT.second }?.let {
            BookComparator.SortType.ASCEND
        } ?: currentClickId.takeIf { it == lastClickItem }?.let {
            if (lastSortType == BookComparator.SortType.ASCEND) BookComparator.SortType.DESCEND else BookComparator.SortType.ASCEND
        } ?: let {
            BookComparator.SortType.DESCEND
        }).apply {
            lastClickItem = currentClickId
            lastSortType = this
        }
    }

    private fun getSortBookType(currentClickId: Int): BookComparator.BookType {
        return when (currentClickId) {
            SORT_BY_DEFAULT.second -> BookComparator.BookType.POSTION
            SORT_BY_SCORE.second -> BookComparator.BookType.SCORE
            SORT_BY_SCORE_NUM.second -> BookComparator.BookType.SCORE_NUM
            SORT_BY_WORDS_NUM.second -> BookComparator.BookType.WORDS_NUM
            else -> throw Exception("unsupported sort book type!")
        }
    }

    private fun getAvaliableSortTypes(element: ScanBookBean): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
            add(SORT_BY_DEFAULT)
            takeIf { element.score.isNotBlank() }?.add(SORT_BY_SCORE)
            takeIf { element.scoreNum.isNotBlank() }?.add(SORT_BY_SCORE_NUM)
            takeIf { element.wordsNum.isNotBlank() }?.add(SORT_BY_WORDS_NUM)
        }
    }
}