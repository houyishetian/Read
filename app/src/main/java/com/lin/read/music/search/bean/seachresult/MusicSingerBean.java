package com.lin.read.music.search.bean.seachresult;

import java.io.Serializable;

public class MusicSingerBean implements Serializable {
    private String mid;
    private String id;
    private String name;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
