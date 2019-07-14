package com.lin.read.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/3/24.
 */

public class DateUtils {
    public static String formatTime(long time) {
        return DateFormat.format("yyyy-MM-dd HH:mm", new Date(time)).toString();
    }

    public static String removeSeconds(String date) {
        if (!TextUtils.isEmpty(date)) {
            //2018-04-08 17:32:45
            Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}):\\d{2}");
            Matcher matcher = pattern.matcher(date);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return date;
    }
}
