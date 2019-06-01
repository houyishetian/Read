package com.lin.read.filter.scan;


import java.io.Serializable;

/**
 * Created by lisonglin on 2017/10/14.
 */

public class SearchInfo implements Serializable{
    private String webType;
    private String rankType;
    private String dateType;
    private String bookType;
    private String score;
    private String scoreNum;
    private String wordsNum;
    private String recommend;

    //zongheng
    private ScanTypeInfo rankInfo;
    private ScanTypeInfo bookTypeInfo;
    private ScanTypeInfo dateInfo;
    private String raiseNum;
    private String commentNum;

    //youshu
    private ScanTypeInfo categoryInfo;
    private ScanTypeInfo wordsNumInfo;
    private ScanTypeInfo bookStatusInfo;
    private ScanTypeInfo updateDateInfo;
    private ScanTypeInfo sortTypeInfo;
    private int currentPage = 1;

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

    public ScanTypeInfo getRankInfo() {
        return rankInfo;
    }

    public void setRankInfo(ScanTypeInfo rankInfo) {
        this.rankInfo = rankInfo;
    }

    public ScanTypeInfo getBookTypeInfo() {
        return bookTypeInfo;
    }

    public void setBookTypeInfo(ScanTypeInfo bookTypeInfo) {
        this.bookTypeInfo = bookTypeInfo;
    }

    public ScanTypeInfo getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(ScanTypeInfo dateInfo) {
        this.dateInfo = dateInfo;
    }

    public String getWebType() {
        return webType;
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

    public String getRankType() {
        return rankType;
    }

    public void setRankType(String rankType) {
        this.rankType = rankType;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
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

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public ScanTypeInfo getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(ScanTypeInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    public ScanTypeInfo getWordsNumInfo() {
        return wordsNumInfo;
    }

    public void setWordsNumInfo(ScanTypeInfo wordsNumInfo) {
        this.wordsNumInfo = wordsNumInfo;
    }

    public ScanTypeInfo getBookStatusInfo() {
        return bookStatusInfo;
    }

    public void setBookStatusInfo(ScanTypeInfo bookStatusInfo) {
        this.bookStatusInfo = bookStatusInfo;
    }

    public ScanTypeInfo getUpdateDateInfo() {
        return updateDateInfo;
    }

    public void setUpdateDateInfo(ScanTypeInfo updateDateInfo) {
        this.updateDateInfo = updateDateInfo;
    }

    public ScanTypeInfo getSortTypeInfo() {
        return sortTypeInfo;
    }

    public void setSortTypeInfo(ScanTypeInfo sortTypeInfo) {
        this.sortTypeInfo = sortTypeInfo;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "SearchInfo{" +
                "webType='" + webType + '\'' +
                ", rankType='" + rankType + '\'' +
                ", dateType='" + dateType + '\'' +
                ", bookType='" + bookType + '\'' +
                ", score='" + score + '\'' +
                ", scoreNum='" + scoreNum + '\'' +
                ", wordsNum='" + wordsNum + '\'' +
                ", recommend='" + recommend + '\'' +
                ", rankInfo=" + rankInfo +
                ", bookTypeInfo=" + bookTypeInfo +
                ", dateInfo=" + dateInfo +
                ", raiseNum='" + raiseNum + '\'' +
                ", commentNum='" + commentNum + '\'' +
                ", categoryInfo=" + categoryInfo +
                ", wordsNumInfo=" + wordsNumInfo +
                ", bookStatusInfo=" + bookStatusInfo +
                ", updateDateInfo=" + updateDateInfo +
                ", sortTypeInfo=" + sortTypeInfo +
                ", currentPage=" + currentPage +
                '}';
    }
}
