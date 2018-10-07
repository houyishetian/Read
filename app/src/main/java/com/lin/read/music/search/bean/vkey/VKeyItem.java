package com.lin.read.music.search.bean.vkey;

public class VKeyItem {
//                {
//                   "subcode": 0,
//                   "songmid": "003a1tne1nSz1Y",
//                   "filename": "C400003a1tne1nSz1Y.m4a",
//                   "vkey": "A7597B74C876D8EC6FB82EDBE7EE6C8FE01180A71157429FDF7919D605618776ADF4D50182B03A31A312CBBE7A7B7595EF3B1332FB311995"
//               }
    private int subcode;
    private String songmid;
    private String filename;
    private String vkey;

    public int getSubcode() {
        return subcode;
    }

    public void setSubcode(int subcode) {
        this.subcode = subcode;
    }

    public String getSongmid() {
        return songmid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getVkey() {
        return vkey;
    }

    public void setVkey(String vkey) {
        this.vkey = vkey;
    }
}
