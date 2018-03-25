package com.lin.read.bookmark;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class BookMarkSharePres {

    private final static String BOOK_MARK_PREFS = "BOOK_MARK_PREFS";
    private final static String BOOK_MARK_LIST = "BOOK_MARK_LIST";

    public static void saveBookMark(Context context, BookMarkBean markBean) {
        if (markBean == null) {
            return;
        }
        String key = markBean.getKey();
        if (key == null) {
            Log.e("Test", "cannot save book mark by a \"null\" key!");
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        BookMarkBean existBean = getBookMark(context, markBean.getBookInfo());
        if (existBean != null && existBean.getIndex() == markBean.getIndex() && existBean.getPage() == markBean.getPage()) {
            Log.e("Test", "the book mark does not update,no need to save!");
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        String value = new GsonBuilder().create().toJson(markBean);
        Log.e("Test", "save book mark-->" + key + "=" + value);
        editor.putString(key, value);

        List<String> currentItems=getBookMarkList(context,preferences);
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }
        if(!currentItems.contains(key)){
            currentItems.add(key);
            String valueAfterAdd = new GsonBuilder().create().toJson(currentItems).toString();
            editor.putString(BOOK_MARK_LIST, valueAfterAdd);
        }
        editor.commit();
    }

    public static BookMarkBean getBookMark(Context context, BookInfo bookInfo) {
        String key = BookMarkBean.getKeyByBookInfo(bookInfo);
        if (key == null) {
            Log.e("Test", "cannot get book mark by a \"null\" key!");
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        String value = preferences.getString(key, null);
        try{
            Log.e("Test", "get book mark-->" + key + "=" + value);
            BookMarkBean markBean=new GsonBuilder().create().fromJson(value,BookMarkBean.class);
            return  markBean;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getBookMark(Context context, String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private static List<String> getBookMarkList(Context context,SharedPreferences preferences) {
        String value = preferences.getString(BOOK_MARK_LIST, null);
        List<String> result = new ArrayList<>();
        try {
            result = new GsonBuilder().create().fromJson(value,List.class);
            return result;
        } catch (Exception e) {
            Log.e("Test", "parse to List<String> error:" + e.getMessage());
        }
        return result;
    }

    public static void deleteBookMarks(Context context,List<BookMarkBean> deleteItems){
        if (deleteItems == null || deleteItems.size() == 0) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        List<String> currentItems = getBookMarkList(context, preferences);

        for(BookMarkBean deleteItem:deleteItems){
            if (deleteItem == null) {
                continue;
            }
            String key = deleteItem.getKey();
            if (key == null) {
                continue;
            }
            editor.remove(key);
            if (currentItems != null) {
                currentItems.remove(key);
            }
        }
        String valueAfterDelete = new GsonBuilder().create().toJson(currentItems).toString();
        editor.putString(BOOK_MARK_LIST, valueAfterDelete);
        editor.commit();
    }

    public static List<BookMarkBean> getBookMarkBeans(Context context){
        SharedPreferences preferences = context.getSharedPreferences(BOOK_MARK_PREFS, Context.MODE_PRIVATE);
        List<String> currentItems=getBookMarkList(context,preferences);
        List<BookMarkBean> result=new ArrayList<>();
        if (currentItems == null || currentItems.size() == 0) {
            return result;
        }
        for(String item:currentItems){
            String currentBeanStr = getBookMark(context, item);
            try {
                 BookMarkBean bean=new GsonBuilder().create().fromJson(currentBeanStr,BookMarkBean.class);
                if (bean != null) {
                    result.add(bean);
                }
            } catch (Exception e) {
                Log.e("Test", "parse to BookMarkBean error:" + e.getMessage());
            }
        }
        return result;
    }
}
