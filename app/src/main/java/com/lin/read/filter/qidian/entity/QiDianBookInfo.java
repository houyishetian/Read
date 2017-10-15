package com.lin.read.filter.qidian.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class QiDianBookInfo implements Parcelable{
    private String bookName;
    private String authorName;
    private String webType;
    private String lastUpdate;
    private String wordsNum;
    private String recommend;
    private String vipClick;
    private String score;
    private String scoreNum;
    private String lastChapter;

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

    public String getWebType() {
        return webType;
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getWordsNum() {
        return wordsNum;
    }

    public void setWordsNum(String wordsNum) {
        this.wordsNum = wordsNum;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getVipClick() {
        return vipClick;
    }

    public void setVipClick(String vipClick) {
        this.vipClick = vipClick;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreNum() {
        return scoreNum;
    }

    public void setScoreNum(String scoreNum) {
        this.scoreNum = scoreNum;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    @Override
    public String toString() {
        return "QiDianBookInfo{" +
                "bookName='" + bookName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", webType='" + webType + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", wordsNum='" + wordsNum + '\'' +
                ", recommend='" + recommend + '\'' +
                ", vipClick='" + vipClick + '\'' +
                ", score='" + score + '\'' +
                ", scoreNum='" + scoreNum + '\'' +
                ", lastChapter='" + lastChapter + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<QiDianBookInfo> CREATOR = new Creator<QiDianBookInfo>() {
        @Override
        public QiDianBookInfo createFromParcel(Parcel in) {
            return new QiDianBookInfo();
        }

        @Override
        public QiDianBookInfo[] newArray(int size) {
            return new QiDianBookInfo[size];
        }
    };

}
