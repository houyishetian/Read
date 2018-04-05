package com.lin.read.filter.scan.qidian.entity;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class ScoreCommentInfo {
//    {
//        "userId": 228492547,
//            "rateId": 70696,
//            "star": 5,
//            "comment": "",
//            "like": 0,
//            "time": "2017-09-27 22:26",
//            "userIcon": "//facepic.qidian.com/qd_face/349573/0/50",
//            "nickName": "书友20170118204818925",
//            "fanLevel": 0
//    }
    private String userId;
    private String rateId;
    private String star;
    private String comment;
    private String like;
    private String time;
    private String userIcon;
    private String nickName;
    private String fanLevel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFanLevel() {
        return fanLevel;
    }

    public void setFanLevel(String fanLevel) {
        this.fanLevel = fanLevel;
    }

    @Override
    public String toString() {
        return "ScoreCommentInfo{" +
                "userId='" + userId + '\'' +
                ", rateId='" + rateId + '\'' +
                ", star='" + star + '\'' +
                ", comment='" + comment + '\'' +
                ", like='" + like + '\'' +
                ", time='" + time + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", nickName='" + nickName + '\'' +
                ", fanLevel='" + fanLevel + '\'' +
                '}';
    }
}
