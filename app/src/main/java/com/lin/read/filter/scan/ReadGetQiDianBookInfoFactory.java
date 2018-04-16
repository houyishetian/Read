package com.lin.read.filter.scan;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.lin.read.filter.BookInfo;
import com.lin.read.filter.ScanBookBean;
import com.lin.read.filter.scan.qidian.QiDianHttpUtils;
import com.lin.read.utils.MessageUtils;

import java.io.IOException;
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
    private List<Object> allBookInfo;
    private String qidianToken = null;
    private Handler handler;

    ExecutorService scanAndFilterService;
    ExecutorService scanBookInfoService;

    @Override
    public void getBookInfo(Handler handler, SearchInfo searchInfo, OnGetBookInfoListener onGetBookInfoListener) {
        this.onGetBookInfoListener = onGetBookInfoListener;
        this.searchInfo = searchInfo;
        this.allBookInfo = new ArrayList<>();
        this.handler = handler;
        GetQiDianBookInfoTask task = new GetQiDianBookInfoTask(1);
        task.execute(searchInfo);
    }

    class GetQiDianBookInfoTask extends AsyncTask<SearchInfo, Void, List<Object>> {
        private int page;

        public GetQiDianBookInfoTask(int page) {
            this.page = page;
        }

        @Override
        protected List<Object> doInBackground(SearchInfo... params) {
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
            List<Object> firstPageInfo = null;
            try {
                firstPageInfo = QiDianHttpUtils.getMaxPageAndBookInfoFromRankPage(searchInfo, page);
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
        protected void onPostExecute(List<Object> strings) {
            super.onPostExecute(strings);
            if (getCode() != CODE_SUCC) {
                if (onGetBookInfoListener != null) {
                    onGetBookInfoListener.failed(getCode());
                }
            } else {
                int maxPage = Integer.parseInt((String) strings.get(0));
                strings.remove(0);
                allBookInfo.addAll(strings);
                final int maxNumFirstPage = strings.size();
                if (maxPage > 1) {
                    scanBookInfoService = Executors.newFixedThreadPool(10);
                    final int totalTaskNum = maxPage - 1;
                    final List<String> completeTask = new ArrayList<String>();
                    for (int i = 2; i <= maxPage; i++) {
                        final int index = i;
                        scanBookInfoService.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<Object> currentPageInfo = QiDianHttpUtils.getMaxPageAndBookInfoFromRankPage(searchInfo, index);
                                    synchronized (Runnable.class) {
                                        currentPageInfo.remove(0);
                                        allBookInfo.addAll(currentPageInfo);
                                        completeTask.add("1");
                                        if (completeTask.size() != totalTaskNum) {
                                        } else {
                                            Log.e("Test","全部扫描完书籍，待过滤");
                                            MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, allBookInfo.size());
                                            scanBookInfoByCondition(allBookInfo,maxNumFirstPage);
                                        }
                                    }
                                } catch (IOException e) {
                                    synchronized (Runnable.class){
                                        e.printStackTrace();
                                        if (scanBookInfoService != null) {
                                            scanBookInfoService.shutdownNow();
                                            scanBookInfoService = null;
                                            Log.e("Test", "扫描书籍信息失败，停止扫描！");
                                            if (onGetBookInfoListener != null) {
                                                onGetBookInfoListener.failed(CODE_NETWORK_ERROR);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                } else {
                    MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, allBookInfo.size());
                    scanBookInfoByCondition(allBookInfo,maxNumFirstPage);
                }
            }
        }
    }


    private void scanBookInfoByCondition(final List<Object> bookInfos,final int maxNumFirstPage) {
        if (bookInfos == null || bookInfos.size() == 0 || maxNumFirstPage<=0) {
            setCode(CODE_OTHER_ERROR);
            if (onGetBookInfoListener != null) {
                onGetBookInfoListener.failed(getCode());
            }
            return;
        }

        Log.e("Test","开始过滤");
        final ArrayList<BookInfo> resultBookInfo=new ArrayList<>();
        MessageUtils.sendWhat(handler,MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START);
        scanAndFilterService = Executors.newFixedThreadPool(10);
        final List<String> completeTask = new ArrayList<String>();
        final int maxSize=bookInfos.size();
        for (int i = 0; i <bookInfos.size(); i++) {
            final int index = i;
            scanAndFilterService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(index>=maxSize){
                            Log.e("Test","重新扫描："+ bookInfos.get(index));
                        }
                        BookInfo scoreBookInfo=QiDianHttpUtils.getBookScoreInfo(searchInfo,qidianToken,((ScanBookBean)bookInfos.get(index)).getUrl(),3);

                        if(scoreBookInfo!=null){
                            scoreBookInfo = QiDianHttpUtils.getBookDetailsInfo(searchInfo, scoreBookInfo, ((ScanBookBean)bookInfos.get(index)).getUrl());
                            synchronized (Runnable.class) {
                                if (scoreBookInfo != null) {
                                    ScanBookBean scanBookBean = (ScanBookBean) bookInfos.get(index);
                                    scoreBookInfo.setPosition(scanBookBean.getPage() * maxNumFirstPage + scanBookBean.getPosition());
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
                        if(QiDianHttpUtils.EXCEPTION_GET_CONN_ERROR.equals(e.getMessage())){
                            synchronized (Runnable.class){
                                int count=getCount(bookInfos,((ScanBookBean)bookInfos.get(index)).getUrl());
                                Log.e("Test","扫描失败："+ bookInfos.get(index)+",已经失败"+count+"次");
                                if(count<3){
                                    completeTask.add("1");
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
                        }else{
                            synchronized (Runnable.class){
                                if(scanAndFilterService !=null){
                                    scanAndFilterService.shutdownNow();
                                    scanAndFilterService =null;
                                    Log.e("Test","扫描失败，停止扫描！");
                                    setCode(CODE_NETWORK_ERROR);
                                    if (onGetBookInfoListener != null) {
                                        onGetBookInfoListener.failed(getCode());
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public int getCount(List<Object> allData,String item){
        if(allData==null||allData.size()==0||StringUtils.isEmpty(item)){
            return 0;
        }
        int count=0;
        for(Object currentItem:allData){
            if(item.equals(((ScanBookBean)currentItem).getUrl())){
                count++;
            }
        }
        return count;
    }
}
