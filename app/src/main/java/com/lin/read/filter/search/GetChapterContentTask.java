package com.lin.read.filter.search;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.lin.read.filter.scan.StringUtils;

import java.io.IOException;

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
            while (!bookChapterInfo.isComplete()) {
                Log.e("Test","未读完，继续读!");
                String temp = ResolveChapterUtils.getChapterContent(bookChapterInfo);
                if (StringUtils.isEmpty(temp)) {
                    break;
                } else {
                    content = content + temp;
                }
            }
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
