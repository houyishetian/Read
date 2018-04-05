package com.lin.read.filter.search;

import java.io.Serializable;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class BookChapterInfo implements Serializable {

    private String chapterName;
    private String chapterNameOri;
    private String chapterLink;
    private String webType;
    private boolean isCurrentReading=false;
    private int page=-1;
    private int index=-1;

    public String getChapterNameOri() {
        return chapterNameOri;
    }

    public void setChapterNameOri(String chapterNameOri) {
        this.chapterNameOri = chapterNameOri;
    }

    public boolean isCurrentReading() {
        return isCurrentReading;
    }

    public void setCurrentReading(boolean currentReading) {
        isCurrentReading = currentReading;
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

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterLink() {
        return chapterLink;
    }

    public void setChapterLink(String chapterLink) {
        this.chapterLink = chapterLink;
    }

    public String getWebType() {
        return webType;
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

    @Override
    public String toString() {
        return "BookChapterInfo{" +
                "chapterName='" + chapterName + '\'' +
                ", chapterLink='" + chapterLink + '\'' +
                ", webType='" + webType + '\'' +
                ", page=" + page +
                ", index=" + index +
                '}';
    }
}
