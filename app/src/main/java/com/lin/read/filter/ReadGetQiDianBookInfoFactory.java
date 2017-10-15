package com.lin.read.filter;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.lin.read.filter.qidian.QiDianHttpUtils;
import com.lin.read.filter.qidian.entity.QiDianBookInfo;
import com.lin.read.utils.MessageUtils;

import java.io.IOException;
import java.security.CodeSigner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class ReadGetQiDianBookInfoFactory extends ReadGetBookInfoFactory {

    public ReadGetQiDianBookInfoFactory() {
    }

    public OnGetBookInfoListener onGetBookInfoListener;
    private SearchInfo searchInfo;
    private List<String> allBookInfo;
    private String qidianToken = null;
    private Handler handler;

    @Override
    public void getBookInfo(Handler handler, SearchInfo searchInfo, OnGetBookInfoListener onGetBookInfoListener) {
        this.onGetBookInfoListener = onGetBookInfoListener;
        this.searchInfo = searchInfo;
        this.allBookInfo = new ArrayList<>();
        this.handler = handler;
        GetQiDianBookInfoTask task = new GetQiDianBookInfoTask(1);
        task.execute(searchInfo);
    }

    class GetQiDianBookInfoTask extends AsyncTask<SearchInfo, Void, List<String>> {
        private int page;

        public GetQiDianBookInfoTask(int page) {
            this.page = page;
        }

        @Override
        protected List<String> doInBackground(SearchInfo... params) {
            MessageUtils.sendWhat(handler, MessageUtils.SCAN_START);
            try {
                qidianToken = QiDianHttpUtils.getToken();
                if (StringUtils.isEmpty(qidianToken)) {
                    setCode(CODE_OTHER_ERROR);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                setCode(CODE_NETWORK_ERROR);
                return null;
            }

            SearchInfo searchInfo = params[0];
            List<String> firstPageInfo = null;
            try {
                firstPageInfo = QiDianHttpUtils.getMaxPageAndBookInfoFromRankPage(searchInfo.getRankType(), searchInfo.getBookType(), page);
                if (firstPageInfo != null && firstPageInfo.size() > 1) {
                    setCode(CODE_SUCC);
                } else {
                    setCode(CODE_OTHER_ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                setCode(CODE_NETWORK_ERROR);
            }
            return firstPageInfo;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if (getCode() != CODE_SUCC) {
                if (onGetBookInfoListener != null) {
                    onGetBookInfoListener.failed(getCode());
                }
            } else {
                int maxPage = Integer.parseInt(strings.get(0));
                strings.remove(0);
                allBookInfo.addAll(strings);
                if (maxPage > 1) {
                    final ExecutorService service = Executors.newFixedThreadPool(10);
                    final int totalTaskNum = maxPage - 1;
                    final List<String> completeTask = new ArrayList<String>();
                    for (int i = 2; i <= maxPage; i++) {
                        final int index = i;
                        service.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<String> currentPageInfo = QiDianHttpUtils.getMaxPageAndBookInfoFromRankPage(searchInfo.getRankType(), searchInfo.getBookType(), index);
                                    synchronized (Runnable.class) {
                                        currentPageInfo.remove(0);
                                        allBookInfo.addAll(currentPageInfo);
                                        completeTask.add("1");
                                        if (completeTask.size() != totalTaskNum) {
                                        } else {
                                            Log.e("Test","全部扫描完书籍，待过滤");
                                            MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, allBookInfo.size());
                                            scanBookInfoByCondition(allBookInfo);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    if (onGetBookInfoListener != null) {
                                        onGetBookInfoListener.failed(CODE_NETWORK_ERROR);
                                    }
                                    service.shutdownNow();
                                }
                            }
                        });
                    }
                } else {
                    MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, allBookInfo.size());
                    scanBookInfoByCondition(allBookInfo);
                }
            }
        }
    }

    private void scanBookInfoByCondition(final List<String> bookInfos) {
        if (bookInfos == null || bookInfos.size() == 0) {
            setCode(CODE_OTHER_ERROR);
            if (onGetBookInfoListener != null) {
                onGetBookInfoListener.failed(getCode());
            }
            return;
        }

        Log.e("Test","开始过滤");
        final ArrayList<QiDianBookInfo> resultBookInfo=new ArrayList<>();
        MessageUtils.sendWhat(handler,MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START);
        final ExecutorService service = Executors.newFixedThreadPool(10);
        final List<String> completeTask = new ArrayList<String>();
        final int maxSize=bookInfos.size();
        for (int i = 0; i <bookInfos.size(); i++) {
            final int index = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(index>=maxSize){
                            Log.e("Test","重新扫描："+ bookInfos.get(index));
                        }
                        QiDianBookInfo scoreBookInfo=QiDianHttpUtils.getBookScoreInfo(searchInfo,qidianToken,bookInfos.get(index));

                        if(scoreBookInfo!=null){
                            scoreBookInfo = QiDianHttpUtils.getBookDetailsInfo(searchInfo, scoreBookInfo, bookInfos.get(index));
                            synchronized (Runnable.class) {
                                if (scoreBookInfo != null) {
                                    resultBookInfo.add(scoreBookInfo);
                                    MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE, resultBookInfo.size());
                                }
                                completeTask.add("1");
                                MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE, completeTask.size());
                                if (bookInfos.size() == completeTask.size()) {
                                    Log.e("Test", "all scan end!");
                                    MessageUtils.sendMessageOfArrayList(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_END, resultBookInfo);
                                    if (onGetBookInfoListener != null) {
                                        onGetBookInfoListener.succ(resultBookInfo);
                                    }
                                }
                            }
                        }else{
                            synchronized (Runnable.class) {
                                completeTask.add("1");
                                MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE, completeTask.size());
                                if (bookInfos.size() == completeTask.size()) {
                                    Log.e("Test", "all scan end!");
                                    MessageUtils.sendMessageOfArrayList(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_END, resultBookInfo);
                                    if (onGetBookInfoListener != null) {
                                        onGetBookInfoListener.succ(resultBookInfo);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        synchronized (Runnable.class){
                            int count=getCount(bookInfos,bookInfos.get(index));
                            Log.e("Test","扫描失败："+ bookInfos.get(index)+",已经失败"+count+"次");
                            if(count<3){
                                bookInfos.add(bookInfos.get(index));
                            }else{
                                completeTask.add("1");
                                MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE, completeTask.size());
                                if (bookInfos.size() == completeTask.size()) {
                                    Log.e("Test", "all scan end!");
                                    MessageUtils.sendMessageOfArrayList(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_END, resultBookInfo);
                                    if (onGetBookInfoListener != null) {
                                        onGetBookInfoListener.succ(resultBookInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public int getCount(List<String> allData,String item){
        if(allData==null||allData.size()==0||StringUtils.isEmpty(item)){
            return 0;
        }
        int count=0;
        for(String currentItem:allData){
            if(item.equals(currentItem)){
                count++;
            }
        }
        return count;
    }
}
