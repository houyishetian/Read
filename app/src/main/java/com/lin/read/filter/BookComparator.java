package com.lin.read.filter;

import com.lin.read.filter.BookInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lisonglin on 2017/10/19.
 */

public class BookComparator implements Comparator<BookInfo> {

    public enum SortType {
        ASCEND, DESCEND
    }

    public enum BookType {
        SCORE, SCORE_NUM, WORDS_NUM, RECOMMEND, VIP_CLICK, POSTION
    }

    private SortType sortType;
    private BookType bookType;

    public BookComparator(SortType sortType, BookType bookType) {
        this.sortType = sortType;
        this.bookType = bookType;
    }

    @Override
    public int compare(BookInfo bookInfo0, BookInfo bookInfo1) {
        if (bookInfo0 == null || bookInfo1 == null || sortType == null || bookType == null) {
            return 0;
        }
        switch (sortType) {
            case ASCEND:
                return sortByAscend(bookInfo0, bookInfo1);
            case DESCEND:
                return -sortByAscend(bookInfo0, bookInfo1);
        }
        return 0;
    }

    private int sortByAscend(BookInfo bookInfo0, BookInfo bookInfo1) {
        if (bookInfo0 == null || bookInfo1 == null || bookType == null) {
            return 0;
        }
        List<BookType> sortPriority = getSortPriority(bookType);
        if (sortPriority != null) {
            for (BookType item : sortPriority) {
                int current = sortByType(bookInfo0, bookInfo1, item);
                if (current != 0) {
                    return current;
                }
            }
        }
        return 0;
    }

    private int sortByType(BookInfo bookInfo0, BookInfo bookInfo1, BookType bookType) {
        if (bookInfo0 == null || bookInfo1 == null || bookType == null) {
            return 0;
        }
        float num0=0;
        float num1=0;
        switch (bookType) {
            case POSTION:
                return bookInfo0.getPosition() - bookInfo1.getPosition();
            case SCORE:
                try {
                    num0 = Float.parseFloat(bookInfo0.getScore());
                    num1 = Float.parseFloat(bookInfo1.getScore());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                break;
            case SCORE_NUM:
                try {
                    num0 = Float.parseFloat(bookInfo0.getScoreNum());
                    num1 = Float.parseFloat(bookInfo1.getScoreNum());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                break;
            case WORDS_NUM:
                try {
                    num0 = Float.parseFloat(bookInfo0.getWordsNum());
                    num1 = Float.parseFloat(bookInfo1.getWordsNum());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                break;
            case RECOMMEND:
                try {
                    num0 = Float.parseFloat(bookInfo0.getRecommend());
                    num1 = Float.parseFloat(bookInfo1.getRecommend());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                break;
            case VIP_CLICK:
                try {
                    num0 = Float.parseFloat(bookInfo0.getVipClick());
                    num1 = Float.parseFloat(bookInfo1.getVipClick());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                break;
        }
        if(num0>num1){
            return 1;
        }else if(num0<num1){
            return -1;
        }
        return 0;
    }

    private List<BookType> getSortPriority(BookType bookType) {
        if (bookType == null) {
            return null;
        }
        List<BookType> result = new ArrayList<>();
        result.add(BookType.POSTION);
        result.add(BookType.SCORE);
        result.add(BookType.SCORE_NUM);
        result.add(BookType.WORDS_NUM);
        result.add(BookType.RECOMMEND);
        result.add(BookType.VIP_CLICK);
        result.remove(bookType);
        result.add(0, bookType);
        return result;
    }
}
