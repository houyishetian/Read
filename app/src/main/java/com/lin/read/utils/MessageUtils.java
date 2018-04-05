package com.lin.read.utils;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lin.read.filter.BookInfo;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class MessageUtils {
    public static final int SCAN_START=0;
    public static final int SCAN_BOOK_INFO_END=1;
    public static final int SCAN_BOOK_INFO_BY_CONDITION_START=2;
    public static final int SCAN_BOOK_INFO_BY_CONDITION_GET_ONE=3;
    public static final int SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE=4;
    public static final int SCAN_BOOK_INFO_BY_CONDITION_END=5;

    public static final String DATA_QIDIAN_BOOK_LIST="DATA_QIDIAN_BOOK_LIST";

    public static void sendWhat(Handler handler, int what){
        if(handler!=null){
            handler.sendEmptyMessage(what);
        }
    }

    public static void sendMessageOfInteger(Handler handler, int what,int message){
        if(handler!=null){
            Message msg=handler.obtainMessage();
            msg.what=what;
            msg.arg1=message;
            handler.sendMessage(msg);
        }
    }


    public static void sendMessageOfArrayList(Handler handler, int what,ArrayList<BookInfo> allData){
        if(handler!=null){
            Message msg=handler.obtainMessage();
            msg.what=what;
            Bundle bundle=new Bundle();
            bundle.putParcelableArrayList(DATA_QIDIAN_BOOK_LIST,allData);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}
