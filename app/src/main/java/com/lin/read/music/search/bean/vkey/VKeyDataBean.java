package com.lin.read.music.search.bean.vkey;

import java.io.Serializable;
import java.util.ArrayList;

public class VKeyDataBean implements Serializable {
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
    private long expiration;
    private ArrayList<VKeyItem> items;

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public ArrayList<VKeyItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<VKeyItem> items) {
        this.items = items;
    }
}
