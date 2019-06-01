package com.lin.read.filter.scan;

import android.os.Handler;

import com.lin.read.utils.Constants;

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
    protected ReadGetBookInfoFactory(){

    }

    public static ReadGetBookInfoFactory getInstance(String type){
        if(StringUtils.isEmpty(type)){
            return null;
        }
        switch (type) {
            case Constants.WEB_QIDIAN:
                return new ReadGetQiDianBookInfoFactory();
            case Constants.WEB_ZONGHENG:
                return new ReadZongHengBookInfoFactory();
            case Constants.WEB_17K:
                return null;
            case Constants.WEB_YOU_SHU:
                return new ReadGetYouShuBookInfoFactory();
            default:
                return null;
        }
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
