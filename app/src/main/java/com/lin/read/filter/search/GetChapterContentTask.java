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

public class GetChapterContentTask extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private BookChapterInfo bookChapterInfo;

    public GetChapterContentTask(Activity activity, BookChapterInfo bookChapterInfo, OnTaskListener onTaskListener) {
        this.activity = activity;
        this.bookChapterInfo = bookChapterInfo;
        this.onTaskListener = onTaskListener;
    }

    private OnTaskListener onTaskListener;

    public interface OnTaskListener {
        void onSucc(String chapter, String content);

        void onFailed();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String content = ResolveChapterUtils.getChapterContent(bookChapterInfo);
            result(true, bookChapterInfo.getChapterName(), content);
        } catch (IOException e) {
            e.printStackTrace();
            result(false, null, null);
        }
        return null;
    }

    private void result(final boolean isSuccessful, final String chapter, final String content) {
        if (onTaskListener == null) {
            return;
        }
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccessful) {
                    onTaskListener.onSucc(chapter, content);
                } else {
                    onTaskListener.onFailed();
                }
            }
        });
    }
}
