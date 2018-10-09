package com.lin.read.music.search.bean.seachresult;

import java.io.Serializable;
import java.util.ArrayList;

public class MusicDataSongItemBean implements Serializable {
    private MusicFileBean file;
    private String name;
    private ArrayList<MusicSingerBean> singer;

    public MusicFileBean getFile() {
        return file;
    }

    public void setFile(MusicFileBean file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MusicSingerBean> getSinger() {
        return singer;
    }

    public void setSinger(ArrayList<MusicSingerBean> singer) {
        this.singer = singer;
    }
}
