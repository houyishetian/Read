package com.lin.read.filter.search;

import android.app.Activity;
import android.os.AsyncTask;

import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.aishuwang.ResolveUtilsForAiShuWang;
import com.lin.read.filter.search.biquge.ResolveUtilsForBiQuGe;
import com.lin.read.filter.search.bdzhannei.ResolveUtilsForBDZhannei;
import com.lin.read.utils.Constants;

import java.io.IOException;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/12.
 */

public class GetDownloadInfoTask extends AsyncTask<String, Void, Void> {

    private ResolveUtilsFactory resolveUtilsFactory;
    private Activity activity;
    private String webName;

    public GetDownloadInfoTask(Activity activity, WebTypeBean currentResolveType, OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;
        this.activity=activity;
        this.webName=currentResolveType.getWebName();
        switch (currentResolveType.getTag()) {
            case Constants.RESOLVE_FROM_NOVEL80:
                resolveUtilsFactory = new ResolveUtilsForBDZhannei();
                ((ResolveUtilsForBDZhannei)resolveUtilsFactory).setTag(currentResolveType.getTag());
                break;
            case Constants.RESOLVE_FROM_BIQUGE:
                resolveUtilsFactory = new ResolveUtilsForBiQuGe();
                break;
            case Constants.RESOLVE_FROM_DINGDIAN:
                resolveUtilsFactory = new ResolveUtilsForBDZhannei();
                ((ResolveUtilsForBDZhannei)resolveUtilsFactory).setTag(currentResolveType.getTag());
                break;
            case Constants.RESOLVE_FROM_BIXIA:
                resolveUtilsFactory = new ResolveUtilsForBDZhannei();
                ((ResolveUtilsForBDZhannei)resolveUtilsFactory).setTag(currentResolveType.getTag());
                break;
            case Constants.RESOLVE_FROM_AISHU:
                resolveUtilsFactory = new ResolveUtilsForAiShuWang();
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
        }else{
            result(false, null);
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
