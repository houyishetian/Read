package com.lin.read.filter;

/**
 * Created by lisonglin on 2018/4/16.
 */

public class BookComparatorUtil {
    public static final int SORT_BY_SCORE = 0;
    public static final int SORT_BY_DEFAULT = 5;
    public static final int SORT_BY_SCORE_NUM = 1;
    public static final int SORT_BY_WORDS = 2;
    public static final int SORT_BY_RECOMMEND = 3;
    public static final int SORT_BY_VIP_CLICK = 4;

    private final int SORT_ASCEND = 1;
    private final int SORT_DESCEND = 2;

    private int lastClickItem = -1;
    private int lastSortType = -1;

    public BookComparator.SortType getSortType(int currentClickItem) {
        if(currentClickItem==SORT_BY_DEFAULT){
            lastSortType = SORT_ASCEND;
        }else{
            if (lastClickItem == currentClickItem) {
                if (lastSortType == SORT_ASCEND) {
                    lastSortType = SORT_DESCEND;
                } else {
                    lastSortType = SORT_ASCEND;
                }
            } else {
                lastSortType = SORT_DESCEND;
            }
        }

        lastClickItem = currentClickItem;
        BookComparator.SortType sortType;
        if (lastSortType == SORT_DESCEND) {
            sortType = BookComparator.SortType.DESCEND;
        } else {
            sortType = BookComparator.SortType.ASCEND;
        }
        return sortType;
    }

    public BookComparator.BookType getSortBookType(int currentClickItem) {
        BookComparator.BookType bookType = null;
        switch (currentClickItem) {
            case SORT_BY_DEFAULT:
                bookType = BookComparator.BookType.POSTION;
                break;
            case SORT_BY_SCORE:
                bookType = BookComparator.BookType.SCORE;
                break;
            case SORT_BY_SCORE_NUM:
                bookType = BookComparator.BookType.SCORE_NUM;
                break;
            case SORT_BY_WORDS:
                bookType = BookComparator.BookType.WORDS_NUM;
                break;
            case SORT_BY_RECOMMEND:
                bookType = BookComparator.BookType.RECOMMEND;
                break;
            case SORT_BY_VIP_CLICK:
                bookType = BookComparator.BookType.VIP_CLICK;
                break;
        }
        return bookType;
    }

    public void setLastClickItem(int lastClickItem) {
        this.lastClickItem = lastClickItem;
    }
}
