package com.lin.read.filter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class BookInfo implements Parcelable{
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
    private String bookLink;
    private String bookType;
    private String downloadLink;

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

    public String getBookLink() {
        return bookLink;
    }

    public void setBookLink(String bookLink) {
        this.bookLink = bookLink;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
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
        dest.writeString(this.bookName);
        dest.writeString(this.authorName);
        dest.writeString(this.webType);
        dest.writeString(this.lastUpdate);
        dest.writeString(this.wordsNum);
        dest.writeString(this.recommend);
        dest.writeString(this.vipClick);
        dest.writeString(this.score);
        dest.writeString(this.scoreNum);
        dest.writeString(this.lastChapter);
        dest.writeString(this.bookLink);
        dest.writeString(this.bookType);
        dest.writeString(this.downloadLink);
    }

    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel in) {
            BookInfo bookInfo =new BookInfo();
            bookInfo.setBookName(in.readString());
            bookInfo.setAuthorName(in.readString());
            bookInfo.setWebType(in.readString());
            bookInfo.setLastUpdate(in.readString());
            bookInfo.setWordsNum(in.readString());
            bookInfo.setRecommend(in.readString());
            bookInfo.setVipClick(in.readString());
            bookInfo.setScore(in.readString());
            bookInfo.setScoreNum(in.readString());
            bookInfo.setLastChapter(in.readString());
            bookInfo.setBookLink(in.readString());
            bookInfo.setBookType(in.readString());
            bookInfo.setDownloadLink(in.readString());
            return bookInfo;
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };

}
