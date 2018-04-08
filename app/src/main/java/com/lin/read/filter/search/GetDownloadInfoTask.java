package com.lin.read.filter.search;

import android.app.Activity;
import android.os.AsyncTask;

import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.aishuwang.ResolveUtilsForAiShuWang;
import com.lin.read.filter.search.biquge.ResolveUtilsForBiQuGe;
import com.lin.read.filter.search.bdzhannei.ResolveUtilsForBDZhannei;
import com.lin.read.filter.search.qingkan.ResolveUtilsForQingKan;
import com.lin.read.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lisonglin on 2018/3/12.
 */

public class GetDownloadInfoTask extends AsyncTask<String, Void, List<BookInfo>> {

    private ResolveUtilsFactory resolveUtilsFactory;
    private Activity activity;
    private String webType;

    public GetDownloadInfoTask(Activity activity, WebTypeBean currentResolveType, OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;
        this.activity=activity;
        this.webType=currentResolveType.getTag();
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
            case Constants.RESOLVE_FROM_QINGKAN:
                resolveUtilsFactory = new ResolveUtilsForQingKan();
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
    protected List<BookInfo> doInBackground(String... params) {
        if (resolveUtilsFactory != null) {
            try {
                List<BookInfo> resultData = resolveUtilsFactory.getBooksByBookname(params);
                if(!Constants.RESOLVE_FROM_QINGKAN.equals(webType)){
                    result(true, resultData);
                }
                return  resultData;
            } catch (IOException e) {
                e.printStackTrace();
                result(false, null);
            }
        }else{
            result(false, null);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<BookInfo> resultData) {
        super.onPostExecute(resultData);
        if(Constants.RESOLVE_FROM_QINGKAN.equals(webType)){
            resolveDownloadLinkForQingKan(resultData);
        }
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

    private void resolveDownloadLinkForQingKan(final List<BookInfo> allBookInfo){
        if (allBookInfo == null || allBookInfo.size() == 0) {
            result(false, null);
            return;
        }
        final List<Integer> alreadyComplete=new ArrayList<>();
        final ExecutorService scanDownloadLinkService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < allBookInfo.size(); i++) {
            final int index=i;
            scanDownloadLinkService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((ResolveUtilsForQingKan)resolveUtilsFactory).getDownloadLink(allBookInfo.get(index));
                        synchronized (Runnable.class){
                            alreadyComplete.add(1);
                            if (alreadyComplete.size() == allBookInfo.size()) {
                                if (scanDownloadLinkService != null) {
                                    scanDownloadLinkService.shutdownNow();
                                }
                                result(true, allBookInfo);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        synchronized (Runnable.class){
                            alreadyComplete.add(1);
                            if (alreadyComplete.size() == allBookInfo.size()) {
                                if (scanDownloadLinkService != null) {
                                    scanDownloadLinkService.shutdownNow();
                                }
                                result(true, allBookInfo);
                            }
                        }
                    }
                }
            });
        }
    }
}
