package com.lin.read.bookmark;

import android.content.Context;
import android.content.SharedPreferences;

import com.lin.read.filter.scan.StringUtils;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class BookMarkSharePres {

    private final static String BOOK_MARK_PREFS = "BOOK_MARK_PREFS";

    public static void saveBookMark(Context context, String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    public static String getBookMark(Context context, String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

}
