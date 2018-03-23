package com.lin.read.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lisonglin on 2018/3/24.
 */

public class DateUtils {
    public static String formatTime(long time){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(time));
    }
}
