package com.lin.read.music.search.bean.vkey;

public class VKeyBean {
//    {
//        "code": 0,
//         "cid": 205361747,
//         "userip": "1.80.36.76",
//        "data": {
//              "expiration": 80400,
//              "items": [
//                {
//                   "subcode": 0,
//                   "songmid": "003a1tne1nSz1Y",
//                   "filename": "C400003a1tne1nSz1Y.m4a",
//                   "vkey": "A7597B74C876D8EC6FB82EDBE7EE6C8FE01180A71157429FDF7919D605618776ADF4D50182B03A31A312CBBE7A7B7595EF3B1332FB311995"
//               }
//              ]
//         }
//    }
    private int code;
    private long cid;
    private String userip;
    private VKeyDataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getUserip() {
        return userip;
    }

    public void setUserip(String userip) {
        this.userip = userip;
    }

    public VKeyDataBean getData() {
        return data;
    }

    public void setData(VKeyDataBean data) {
        this.data = data;
    }
}
