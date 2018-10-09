package com.lin.read.music.search.bean.seachresult;

import java.io.Serializable;

public class MusicDataBean implements Serializable {
    private String keyword;
    private int priority;
    private MusicDataSongBean song;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public MusicDataSongBean getSong() {
        return song;
    }

    public void setSong(MusicDataSongBean song) {
        this.song = song;
    }
}
