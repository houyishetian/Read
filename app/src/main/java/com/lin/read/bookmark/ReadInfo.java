package com.lin.read.bookmark;

import com.lin.read.filter.search.BookChapterInfo;

/**
 * Created by lisonglin on 2018/3/20.
 */

public class ReadInfo {
    private boolean hasPreviousPage = false;
    private boolean hasNextPage = false;
    private boolean hasPreviousChapter = false;
    private boolean hasNextChapter = false;
    private int currentPage = 0;
    private BookChapterInfo currentChapter;

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousChapter() {
        return hasPreviousChapter;
    }

    public void setHasPreviousChapter(boolean hasPreviousChapter) {
        this.hasPreviousChapter = hasPreviousChapter;
    }

    public boolean isHasNextChapter() {
        return hasNextChapter;
    }

    public void setHasNextChapter(boolean hasNextChapter) {
        this.hasNextChapter = hasNextChapter;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public BookChapterInfo getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(BookChapterInfo currentChapter) {
        this.currentChapter = currentChapter;
    }
}
