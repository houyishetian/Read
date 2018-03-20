package com.lin.read.bookmark;

import com.lin.read.filter.scan.StringUtils;

import java.io.Serializable;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class BookMarkBean implements Serializable {
    private int page;
    private int index;
    private String webType;
    private String bookName;
    private String authorName;
    private String bookLink;

    public String getBookLink() {
        return bookLink;
    }

    public void setBookLink(String bookLink) {
        this.bookLink = bookLink;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public String getWebType() {
        return webType;
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

    public String getKey() {
        if (StringUtils.isEmpty(bookLink) || StringUtils.isEmpty(webType) || StringUtils.isEmpty(bookName) || StringUtils.isEmpty(authorName)) {
            return null;
        }
        return webType + "_" + bookName + "_" + authorName + "_" + bookLink;
    }
}
