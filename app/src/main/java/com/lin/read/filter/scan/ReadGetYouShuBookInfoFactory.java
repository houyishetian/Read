package com.lin.read.filter.scan;

import android.os.AsyncTask;
import android.os.Handler;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.youshu.YouShuHttpUtil;
import com.lin.read.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class ReadGetYouShuBookInfoFactory extends ReadGetBookInfoFactory {
    private Handler handler;
    private OnGetBookInfoListener onGetBookInfoListener;
    private int page;

    @Override
    public void getBookInfo(Handler handler, SearchInfo searchInfo, OnGetBookInfoListener onGetBookInfoListener) {
        this.handler = handler;
        this.page = 1;
        this.onGetBookInfoListener = onGetBookInfoListener;
        GetBookInfoTask task = new GetBookInfoTask();
        task.execute(searchInfo);
    }

    public void setPage(int page){
        this.page = page;
    }

    class GetBookInfoTask extends AsyncTask<SearchInfo, Void, List<Object>> {

        @Override
        protected List<Object> doInBackground(SearchInfo... searchInfos) {
            handler.sendEmptyMessage(MessageUtils.SCAN_START);
            try {
                ArrayList<Object> allBooks = YouShuHttpUtil.getAllBookInfo(searchInfos[0],page);
                if (allBooks != null && allBooks.size() > 1) {
                    setCode(CODE_SUCC);
                    onGetBookInfoListener.succ(allBooks);
                    return allBooks;
                } else {
                    setCode(CODE_NETWORK_ERROR);
                    onGetBookInfoListener.failed(CODE_NETWORK_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setCode(CODE_NETWORK_ERROR);
                onGetBookInfoListener.failed(CODE_NETWORK_ERROR);
            }
            return null;
        }
    }
}
