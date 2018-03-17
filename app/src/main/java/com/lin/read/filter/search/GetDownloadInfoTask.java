package com.lin.read.filter.search;

import android.app.Activity;
import android.os.AsyncTask;

import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.biquge.ResolveUtilsForBiQuGe;
import com.lin.read.filter.search.novel80.ResolveUtilsFor80;

import java.io.IOException;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/12.
 */

public class GetDownloadInfoTask extends AsyncTask<String, Void, Void> {

    public static final int RESOLVE_FROM_NOVEL80 = 0;
    public static final int RESOLVE_FROM_BIQUGE = 1;
    private ResolveUtilsFactory resolveUtilsFactory;
    private Activity activity;

    public GetDownloadInfoTask(Activity activity, int currentResolveType, OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;
        this.activity=activity;
        switch (currentResolveType) {
            case RESOLVE_FROM_NOVEL80:
                resolveUtilsFactory = new ResolveUtilsFor80();
                break;
            case RESOLVE_FROM_BIQUGE:
                resolveUtilsFactory = new ResolveUtilsForBiQuGe();
                break;
            default:
                resolveUtilsFactory = null;
        }
    }

    private OnTaskListener onTaskListener;

    public interface OnTaskListener {
        void onSucc(List<BookInfo> allBooks);

        void onFailed();
    }

    @Override
    protected Void doInBackground(String... params) {
        if (resolveUtilsFactory != null) {
            try {
                List<BookInfo> resultData = resolveUtilsFactory.getBooksByBookname(params);
                result(true, resultData);
            } catch (IOException e) {
                e.printStackTrace();
                result(false, null);
            }
        }
        return null;
    }

    private void result(final boolean isSuccessful, final List<BookInfo> allBooks) {
        if (onTaskListener == null) {
            return;
        }
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccessful) {
                    onTaskListener.onSucc(allBooks);
                } else {
                    onTaskListener.onFailed();
                }
            }
        });
    }
}
