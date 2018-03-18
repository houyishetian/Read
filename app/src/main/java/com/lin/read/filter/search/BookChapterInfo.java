package com.lin.read.filter.search;

import java.io.Serializable;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class BookChapterInfo implements Serializable {

    private String chapterName;
    private String chapterLink;
    private String webType;

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
                '}';
    }
}
