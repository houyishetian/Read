package com.lin.read.bookmark;

import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;

import java.io.Serializable;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class BookMarkBean implements Serializable {
    private int page;
    private int index;
    private BookInfo bookInfo;
    private long lastReadTime;
    private String lastReadChapter;
    private boolean isChecked;
    private boolean showCheckBox;

    @Override
    public String toString() {
        return "BookMarkBean{" +
                "page=" + page +
                ", index=" + index +
                ", bookInfo=" + bookInfo +
                ", lastReadTime=" + lastReadTime +
                ", lastReadChapter='" + lastReadChapter + '\'' +
                ", isChecked=" + isChecked +
                ", showCheckBox=" + showCheckBox +
                '}';
    }

    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getLastReadChapter() {
        return lastReadChapter;
    }

    public void setLastReadChapter(String lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        if (bookInfo == null || StringUtils.isEmpty(bookInfo.getBookLink()) || StringUtils.isEmpty(bookInfo.getWebType())
                || StringUtils.isEmpty(bookInfo.getBookName()) || StringUtils.isEmpty(bookInfo.getAuthorName())) {
            return null;
        }
        return bookInfo.getWebType() + "_" + bookInfo.getBookName() + "_" + bookInfo.getAuthorName() + "_" + bookInfo.getBookLink();
    }

    public static String getKeyByBookInfo(BookInfo bookInfo){
        if (bookInfo == null || StringUtils.isEmpty(bookInfo.getBookLink()) || StringUtils.isEmpty(bookInfo.getWebType())
                || StringUtils.isEmpty(bookInfo.getBookName()) || StringUtils.isEmpty(bookInfo.getAuthorName())) {
            return null;
        }
        return bookInfo.getWebType() + "_" + bookInfo.getBookName() + "_" + bookInfo.getAuthorName() + "_" + bookInfo.getBookLink();
    }
}
