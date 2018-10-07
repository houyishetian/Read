package com.lin.read.music.search.bean.seachresult;

public class MusicBean {
//    {
//        "code": 0,
//            "data": {
//        "keyword": "爱上一朵花",
//                "priority": 0,
//                "qc": [ ],
//        "semantic": {
//            "curnum": 0,
//                    "curpage": 1,
//                    "list": [ ],
//            "totalnum": 0
//        },
//        "song": {
//            "curnum": 7,
//                    "curpage": 1,
//                    "list": [],
//            "totalnum": 7
//        },
//        "tab": 0,
//                "taglist": [ ],
//        "totaltime": 0,
//                "zhida": {}
//    },
//        "message": "",
//            "notice": "",
//            "subcode": 0,
//            "time": 1538753716,
//            "tips": ""
//    }
    private int code;
    private MusicDataBean data;

    public MusicDataBean getData() {
        return data;
    }

    public void setData(MusicDataBean data) {
        this.data = data;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
