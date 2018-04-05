package com.lin.read.filter.scan.qidian.entity;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class ScoreJson {
//    {
//        "data": {
//    },
//        "code": 0,
//            "msg": "suc"
//    }

    private ScoreData data;
    private String code;
    private String msg;

    public ScoreData getData() {
        return data;
    }

    public void setData(ScoreData data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ScoreJson{" +
                "data=" + data +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
