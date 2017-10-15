package com.lin.read.filter;


import java.io.Serializable;

/**
 * Created by lisonglin on 2017/10/14.
 */

public class SearchInfo implements Serializable{
    private String webType;
    private String rankType;
    private String bookType;
    private String score;
    private String scoreNum;
    private String wordsNum;
    private String recommend;

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

    @Override
    public String toString() {
        return "SearchInfo{" +
                "webType='" + webType + '\'' +
                ", rankType='" + rankType + '\'' +
                ", bookType='" + bookType + '\'' +
                ", score='" + score + '\'' +
                ", scoreNum='" + scoreNum + '\'' +
                ", wordsNum='" + wordsNum + '\'' +
                ", recommend='" + recommend + '\'' +
                '}';
    }

}
