package com.lin.read.music.search.bean.seachresult;

import java.io.Serializable;
import java.util.ArrayList;

public class MusicDataSongBean implements Serializable {
    private int curnum;
    private int curpage;
    private ArrayList<MusicDataSongItemBean> list;
    private int totalnum;

    public int getCurnum() {
        return curnum;
    }

    public void setCurnum(int curnum) {
        this.curnum = curnum;
    }

    public int getCurpage() {
        return curpage;
    }

    public void setCurpage(int curpage) {
        this.curpage = curpage;
    }

    public ArrayList<MusicDataSongItemBean> getList() {
        return list;
    }

    public void setList(ArrayList<MusicDataSongItemBean> list) {
        this.list = list;
    }

    public int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }
}
