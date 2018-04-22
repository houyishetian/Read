package com.lin.read.filter;

/**
 * Created by lisonglin on 2018/4/16.
 */

public class ScanBookBean {
    private String url;
    private int position;
    private int page;
    private String bookName;

    @Override
    public String toString() {
        return "ScanBookBean{" +
                "url='" + url + '\'' +
                ", position=" + position +
                ", page=" + page +
                ", bookName='" + bookName + '\'' +
                '}';
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
