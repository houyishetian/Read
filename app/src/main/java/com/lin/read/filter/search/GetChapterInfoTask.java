package com.lin.read.filter.search;

import android.app.Activity;
import android.os.AsyncTask;

import com.lin.read.filter.BookInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class GetChapterInfoTask extends AsyncTask<String, Void, Void> {
    private Activity activity;
    private BookInfo bookInfo;
    public static final int eachLen = 30;

    public GetChapterInfoTask(Activity activity, BookInfo bookInfo, OnTaskListener onTaskListener) {
        this.activity = activity;
        this.bookInfo = bookInfo;
        this.onTaskListener = onTaskListener;
    }

    private OnTaskListener onTaskListener;

    public interface OnTaskListener {
        void onSucc(List<BookChapterInfo> allInfo,List<ArrayList<BookChapterInfo>> splitInfos);

        void onFailed();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            List<BookChapterInfo> allInfo = ResolveChapterUtils.getChapterInfo(bookInfo);
            result(true, allInfo,splitList(allInfo));
        } catch (IOException e) {
            e.printStackTrace();
            result(false, null,null);
        }
        return null;
    }

    private void result(final boolean isSuccessful, final List<BookChapterInfo> allInfo,final List<ArrayList<BookChapterInfo>> splitInfos) {
        if (onTaskListener == null) {
            return;
        }
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccessful) {
                    onTaskListener.onSucc(allInfo,splitInfos);
                } else {
                    onTaskListener.onFailed();
                }
            }
        });
    }

    private List<ArrayList<BookChapterInfo>> splitList(List<BookChapterInfo> allInfo) {
        if (allInfo == null || allInfo.size() == 0) {
            return null;
        }
        List<ArrayList<BookChapterInfo>> result = new ArrayList<>();
        int minPage = 0;
        int maxPage;
        if (allInfo.size() % eachLen == 0) {
            maxPage = allInfo.size() / eachLen;
        } else {
            maxPage = allInfo.size() / eachLen + 1;
        }
        for (int i = minPage; i < maxPage; i++) {
            ArrayList<BookChapterInfo> subList = new ArrayList<>();
            int maxLen = ((i + 1) * eachLen) > allInfo.size() ? allInfo.size() : ((i + 1) * eachLen);
            for (int j = i * eachLen; j < maxLen; j++) {
                BookChapterInfo current=allInfo.get(j);
                current.setPage(i);
                current.setIndex(j);
                subList.add(current);
            }
            result.add(subList);
        }
        return result;
    }
}
