package com.lin.read.filter.search;

import android.os.Handler;

/**
 * Created by lisonglin on 2017/10/15.
 */

public abstract class ReadGetBookInfoFactory {
    public static final int CODE_SUCC=0;
    public static final int CODE_NETWORK_ERROR=1;
    public static final int CODE_OTHER_ERROR=2;

    public static final String WEB_QIDIAN="1";
    public static final String WEB_ZONGHENG="2";
    public static final String WEB_17K="3";


    private int code;
    public ReadGetBookInfoFactory(String type){

    }

    public ReadGetBookInfoFactory(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public interface OnGetBookInfoListener{
        void succ(Object allBookInfo);
        void failed(int code);
    }

    public abstract void getBookInfo(Handler handler,SearchInfo searchInfo, OnGetBookInfoListener onGetBookInfoListener);
}
