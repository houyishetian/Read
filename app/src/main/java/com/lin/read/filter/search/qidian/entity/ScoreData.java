package com.lin.read.filter.search.qidian.entity;

import java.util.List;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class ScoreData {
//    "data": {
//        "rate": 9.2,
//                "userCount": 16,
//                "iRated": 0,
//                "totalCnt": 16,
//                "pageMax": 2,
//                "iRateStar": 0,
//                "pageIndex": 1,
//                "commentInfo": [
//        {
//            "userId": 228492547,
//                "rateId": 70696,
//                "star": 5,
//                "comment": "",
//                "like": 0,
//                "time": "2017-09-27 22:26",
//                "userIcon": "//facepic.qidian.com/qd_face/349573/0/50",
//                "nickName": "书友20170118204818925",
//                "fanLevel": 0
//        }
//        ]
//    },
    private String rate;
    private String userCount;
    private String iRated;
    private String totalCnt;
    private String pageMax;
    private String iRateStar;
    private String pageIndex;
    private List<ScoreCommentInfo> commentInfo;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public String getiRated() {
        return iRated;
    }

    public void setiRated(String iRated) {
        this.iRated = iRated;
    }

    public String getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(String totalCnt) {
        this.totalCnt = totalCnt;
    }

    public String getPageMax() {
        return pageMax;
    }

    public void setPageMax(String pageMax) {
        this.pageMax = pageMax;
    }

    public String getiRateStar() {
        return iRateStar;
    }

    public void setiRateStar(String iRateStar) {
        this.iRateStar = iRateStar;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public List<ScoreCommentInfo> getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(List<ScoreCommentInfo> commentInfo) {
        this.commentInfo = commentInfo;
    }

    @Override
    public String toString() {
        return "ScoreData{" +
                "rate='" + rate + '\'' +
                ", userCount='" + userCount + '\'' +
                ", iRated='" + iRated + '\'' +
                ", totalCnt='" + totalCnt + '\'' +
                ", pageMax='" + pageMax + '\'' +
                ", iRateStar='" + iRateStar + '\'' +
                ", pageIndex='" + pageIndex + '\'' +
                ", commentInfo=" + commentInfo +
                '}';
    }
}
