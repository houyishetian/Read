package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.adapter.ReadBookChapterItemAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.filter.search.GetChapterInfoTask;
import com.lin.read.utils.Constants;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.view.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class ReadBookActivity extends Activity {

    private BookInfo bookInfo;

    private ImageView chapterMenu;
    private View layoutMenu;
    private View layoutMenuBlank;
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
    private List<ArrayList<BookChapterInfo>> splitAllInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);
        Intent intent = getIntent();
        if (intent != null) {
            bookInfo = intent.getParcelableExtra(Constants.KEY_SKIP_TO_READ);
        }
        initView();
        getChapterInfo();
    }

    private void initView() {
        currentDisplayInfo = new ArrayList<>();
        chapterMenu= (ImageView) findViewById(R.id.chapter_menu);
        layoutMenu=findViewById(R.id.layout_chapters);
        layoutMenuBlank=findViewById(R.id.chapter_blank_view);
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

        bookNameTv.setText(bookInfo.getBookName());

        chapterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(ReadBookActivity.this, R.anim.set_scan_filter_menu_in);
                layoutMenu.startAnimation(anim);
                layoutMenu.setVisibility(View.VISIBLE);
            }
        });
        layoutMenuBlank.setOnClickListener(new NoDoubleClickListener(){
            @Override
            public void onNoDoubleClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(ReadBookActivity.this, R.anim.set_scan_filter_menu_out);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        layoutMenu.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                layoutMenu.startAnimation(anim);
            }
        });
    }

    private void getChapterInfo() {
        DialogUtil.getInstance().showLoadingDialog(this);
        GetChapterInfoTask getChapterInfoTask = new GetChapterInfoTask(this, bookInfo, new GetChapterInfoTask.OnTaskListener() {
            @Override
            public void onSucc(List<ArrayList<BookChapterInfo>> allInfo) {
                if (allInfo != null) {
                    splitAllInfo = allInfo;
                    currentDisplayInfo.clear();
                    Log.e("Test","allInfo:"+splitAllInfo.get(0));
                    currentDisplayInfo.addAll(splitAllInfo.get(0));
                    readBookChapterItemAdapter.notifyDataSetChanged();
                }
                DialogUtil.getInstance().hideLoadingView();
            }

            @Override
            public void onFailed() {
                DialogUtil.getInstance().hideLoadingView();
            }
        });
        getChapterInfoTask.execute();
    }


}
