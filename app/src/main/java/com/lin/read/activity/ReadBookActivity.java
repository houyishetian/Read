package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.adapter.ReadBookChapterItemAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.filter.search.GetChapterInfoTask;
import com.lin.read.utils.Constants;
import com.lin.read.view.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class ReadBookActivity extends Activity {

    private BookInfo bookInfo;

    private TextView bookNameTv;
    private TextView previousPageTv;
    private TextView nexePageTv;
    private RecyclerView chaptersRcv;
    private TextView chapterNameTv;
    private TextView chapterContentTv;
    private TextView previousChapterTv;
    private TextView nexeChapterTv;
    private ReadBookChapterItemAdapter readBookChapterItemAdapter;
    private ArrayList<BookChapterInfo> currentDisplayInfo;
    private final int eachLen = 30;
    private List<ArrayList<BookChapterInfo>> splitAllInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);
        initView();
        Intent intent = getIntent();
        if (intent != null) {
            bookInfo = intent.getParcelableExtra(Constants.KEY_SKIP_TO_READ);
        }
        getChapterInfo();
    }

    private void initView() {
        currentDisplayInfo = new ArrayList<>();
        bookNameTv = (TextView) findViewById(R.id.chapter_bookName);
        previousPageTv = (TextView) findViewById(R.id.chapter_previous_page);
        nexePageTv = (TextView) findViewById(R.id.chapter_next_page);
        chaptersRcv = (RecyclerView) findViewById(R.id.rcv_chapters);
        chapterNameTv = (TextView) findViewById(R.id.chapter_name);
        chapterContentTv = (TextView) findViewById(R.id.chapter_content);
        previousChapterTv = (TextView) findViewById(R.id.chapter_previous_chapter);
        nexeChapterTv = (TextView) findViewById(R.id.chapter_next_chapter);

        readBookChapterItemAdapter = new ReadBookChapterItemAdapter(this, currentDisplayInfo);
        chaptersRcv.setLayoutManager(new LinearLayoutManager(this));
        chaptersRcv.addItemDecoration(new ScanBooksItemDecoration(this));
        chaptersRcv.setAdapter(readBookChapterItemAdapter);
    }

    private void getChapterInfo() {
        DialogUtil.getInstance().showLoadingDialog(this);
        GetChapterInfoTask getChapterInfoTask = new GetChapterInfoTask(this, bookInfo, new GetChapterInfoTask.OnTaskListener() {
            @Override
            public void onSucc(List<BookChapterInfo> allInfo) {
                DialogUtil.getInstance().hideLoadingView();
                if (allInfo != null) {
                    splitAllInfo = splitList(allInfo);
                    currentDisplayInfo.clear();
                    Log.e("Test","allInfo:"+splitAllInfo.get(0));
                    currentDisplayInfo.addAll(splitAllInfo.get(0));
                    readBookChapterItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed() {
                DialogUtil.getInstance().hideLoadingView();
            }
        });
        getChapterInfoTask.execute();
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
                subList.add(allInfo.get(j));
            }
            result.add(subList);
        }
        return result;
    }
}
