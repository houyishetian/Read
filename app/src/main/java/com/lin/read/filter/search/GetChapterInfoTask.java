package com.lin.read.filter.search;

import android.app.Activity;
import android.os.AsyncTask;

import com.lin.read.filter.BookInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class GetChapterInfoTask extends AsyncTask<String, Void, Void> {
    private Activity activity;
    private BookInfo bookInfo;

    public GetChapterInfoTask(Activity activity, BookInfo bookInfo, OnTaskListener onTaskListener) {
        this.activity = activity;
        this.bookInfo = bookInfo;
        this.onTaskListener = onTaskListener;
    }

    private OnTaskListener onTaskListener;

    public interface OnTaskListener {
        void onSucc(List<BookChapterInfo> allInfo);

        void onFailed();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            List<BookChapterInfo> allInfo = ResolveChapterUtils.getChapterInfo(bookInfo);
            result(true, allInfo);
        } catch (IOException e) {
            e.printStackTrace();
            result(false, null);
        }
        return null;
    }

    private void result(final boolean isSuccessful, final List<BookChapterInfo> allInfo) {
        if (onTaskListener == null) {
            return;
        }
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccessful) {
                    onTaskListener.onSucc(allInfo);
                } else {
                    onTaskListener.onFailed();
                }
            }
        });
    }
}
