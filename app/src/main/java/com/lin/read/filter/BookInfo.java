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
    private String click;
    private String score;
    private String scoreNum;
    private String lastChapter;
    private String bookLink;
    private String bookType;
    private String downloadLink;
    private String webName;
    private int position;
    //zongheng
    private String raiseNum;
    private String commentNum;

    @Override
    public String toString() {
        return "BookInfo{" +
                "bookName='" + bookName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", webType='" + webType + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", wordsNum='" + wordsNum + '\'' +
                ", recommend='" + recommend + '\'' +
                ", click='" + click + '\'' +
                ", score='" + score + '\'' +
                ", scoreNum='" + scoreNum + '\'' +
                ", lastChapter='" + lastChapter + '\'' +
                ", bookLink='" + bookLink + '\'' +
                ", bookType='" + bookType + '\'' +
                ", downloadLink='" + downloadLink + '\'' +
                ", webName='" + webName + '\'' +
                ", position=" + position +
                ", raiseNum='" + raiseNum + '\'' +
                ", commentNum='" + commentNum + '\'' +
                '}';
    }

    public String getRaiseNum() {
        return raiseNum;
    }

    public void setRaiseNum(String raiseNum) {
        this.raiseNum = raiseNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
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

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
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
        dest.writeString(this.click);
        dest.writeString(this.score);
        dest.writeString(this.scoreNum);
        dest.writeString(this.lastChapter);
        dest.writeString(this.bookLink);
        dest.writeString(this.bookType);
        dest.writeString(this.downloadLink);
        dest.writeString(this.webName);
        dest.writeInt(position);
        dest.writeString(raiseNum);
        dest.writeString(commentNum);
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
            bookInfo.setClick(in.readString());
            bookInfo.setScore(in.readString());
            bookInfo.setScoreNum(in.readString());
            bookInfo.setLastChapter(in.readString());
            bookInfo.setBookLink(in.readString());
            bookInfo.setBookType(in.readString());
            bookInfo.setDownloadLink(in.readString());
            bookInfo.setWebName(in.readString());
            bookInfo.setPosition(in.readInt());
            bookInfo.setRaiseNum(in.readString());
            bookInfo.setCommentNum(in.readString());
            return bookInfo;
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };

}
