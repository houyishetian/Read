package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.adapter.ReadBookChapterItemAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.filter.search.GetChapterContentTask;
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
    private TextView nextPageTv;
    private RecyclerView chaptersRcv;
    private TextView chapterNameTv;
    private TextView chapterContentTv;
    private TextView previousChapterTv;
    private TextView nextChapterTv;
    private ReadBookChapterItemAdapter readBookChapterItemAdapter;
    private ArrayList<BookChapterInfo> currentDisplayInfo;
    private List<BookChapterInfo> allInfo;
    private List<ArrayList<BookChapterInfo>> splitAllInfo;

    private int currentPage=0;

    private ScrollView readScroll;

    private BookChapterInfo currentChapter;

    private boolean hasPreviousPage = false;
    private boolean hasNextPage = false;
    private boolean hasPreviousChapter = false;
    private boolean hasNextChapter = false;
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
        nextPageTv = (TextView) findViewById(R.id.chapter_next_page);
        chaptersRcv = (RecyclerView) findViewById(R.id.rcv_chapters);
        chapterNameTv = (TextView) findViewById(R.id.chapter_name);
        chapterContentTv = (TextView) findViewById(R.id.chapter_content);
        previousChapterTv = (TextView) findViewById(R.id.chapter_previous_chapter);
        nextChapterTv = (TextView) findViewById(R.id.chapter_next_chapter);
        readScroll= (ScrollView) findViewById(R.id.read_scroll);

        readBookChapterItemAdapter = new ReadBookChapterItemAdapter(this, currentDisplayInfo);
        chaptersRcv.setLayoutManager(new LinearLayoutManager(this));
        chaptersRcv.addItemDecoration(new ScanBooksItemDecoration(this));
        chaptersRcv.setAdapter(readBookChapterItemAdapter);

        bookNameTv.setText(bookInfo.getBookName());

        chapterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showMenu();
            }
        });
        layoutMenuBlank.setOnClickListener(new NoDoubleClickListener(){
            @Override
            public void onNoDoubleClick(View v) {
                hideMenu();
            }
        });
        previousPageTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(hasPreviousPage){
                    goToPriviousPage();
                }else{
                    Toast.makeText(ReadBookActivity.this, "已经是第一页！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextPageTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(hasNextPage){
                    goToNextPage();
                }else{
                    Toast.makeText(ReadBookActivity.this, "已经是最后一页！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        readBookChapterItemAdapter.setOnChapterClickListener(new ReadBookChapterItemAdapter.OnChapterClickListener() {
            @Override
            public void onChapterClick(BookChapterInfo bookChapterInfo) {
                currentChapter=bookChapterInfo;
                hideMenu();
                getChapterContent(bookChapterInfo);
            }
        });
        previousChapterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(hasPreviousChapter){
                    goToPriviousChapter();
                }else{
                    Toast.makeText(ReadBookActivity.this, "已经是第一章！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextChapterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(hasNextChapter){
                    goToNextChapter();
                }else{
                    Toast.makeText(ReadBookActivity.this, "已经是最后一章！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showMenu(){
        Animation anim = AnimationUtils.loadAnimation(ReadBookActivity.this, R.anim.set_scan_filter_menu_in);
        layoutMenu.startAnimation(anim);
        layoutMenu.setVisibility(View.VISIBLE);
    }

    private void hideMenu(){
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

    private void getChapterInfo() {
        DialogUtil.getInstance().showLoadingDialog(this);
        GetChapterInfoTask getChapterInfoTask = new GetChapterInfoTask(this, bookInfo, new GetChapterInfoTask.OnTaskListener() {
            @Override
            public void onSucc(List<BookChapterInfo> allInfo,List<ArrayList<BookChapterInfo>> splitInfos) {
                setDisplay();
                if (splitInfos != null && splitInfos.size() > 0) {
                    ReadBookActivity.this.allInfo = allInfo;
                    splitAllInfo = splitInfos;
                    currentDisplayInfo.clear();
                    currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
                    readBookChapterItemAdapter.notifyDataSetChanged();
                    if(splitInfos.size()<2){
                        hasPreviousPage=false;
                        hasNextPage=false;
                    }else{
                        hasPreviousPage=false;
                        hasNextPage=true;
                    }
                    if (allInfo == null || allInfo.size() < 2) {
                        hasPreviousChapter=false;
                        hasNextChapter=false;
                    } else {
                        hasPreviousChapter=false;
                        hasNextChapter=true;
                    }
                    setDisplay();
                    currentChapter = currentDisplayInfo.get(0);
                    getChapterContent(currentDisplayInfo.get(0));
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

    private void getChapterContent(BookChapterInfo bookChapterInfo){
        DialogUtil.getInstance().showLoadingDialog(this);
        GetChapterContentTask task=new GetChapterContentTask(this, bookChapterInfo, new GetChapterContentTask.OnTaskListener() {
            @Override
            public void onSucc(String chapter, String content) {
                DialogUtil.getInstance().hideLoadingView();
                chapterNameTv.setText(chapter);
                chapterContentTv.setText(Html.fromHtml(content));
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        readScroll.scrollTo(0,0);
                    }
                });
            }

            @Override
            public void onFailed() {
                DialogUtil.getInstance().hideLoadingView();
            }
        });
        task.execute();
    }

    private boolean goToNextPage() {
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage=false;
            hasPreviousChapter=false;
            hasNextPage=false;
            hasNextChapter=false;
            setDisplay();
            return false;
        } else {
            hasPreviousPage=true;
            if (currentPage == splitAllInfo.size() - 1) {
                Toast.makeText(this, "已经是最后一页！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            } else {
                currentPage++;
            }
            if(currentPage== splitAllInfo.size() - 1){
                hasNextPage=false;
            }
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            readBookChapterItemAdapter.notifyDataSetChanged();
            setDisplay();
            return true;
        }
    }


    private boolean goToNextChapter(){
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage=false;
            hasPreviousChapter=false;
            hasNextPage=false;
            hasNextChapter=false;
            setDisplay();
            return false;
        }else{
            hasPreviousChapter=true;
            if(currentChapter.getIndex()==allInfo.size()-1){
                Toast.makeText(this, "已经是最后一章！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            }else{
                currentChapter=allInfo.get(currentChapter.getIndex()+1);
            }
            if(currentChapter.getIndex()==allInfo.size()-1){
                hasNextChapter=false;
            }
            if(currentChapter.getPage()!=currentPage){
                currentPage=currentChapter.getPage();
                currentDisplayInfo.clear();
                currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
                readBookChapterItemAdapter.notifyDataSetChanged();
                if(currentPage == 0){
                    hasPreviousPage=false;
                }else{
                    hasPreviousPage=true;
                }
                if(currentPage== splitAllInfo.size() - 1){
                    hasNextPage=false;
                }else{
                    hasNextPage=true;
                }
            }
            setDisplay();
            getChapterContent(currentChapter);
            return true;
        }
    }

    private boolean goToPriviousPage() {
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage=false;
            hasPreviousChapter=false;
            hasNextPage=false;
            hasNextChapter=false;
            setDisplay();
            return false;
        } else {
            hasNextPage=true;
            if (currentPage == 0) {
                Toast.makeText(this, "已经是第一页！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            } else {
                currentPage--;
            }
            if(currentPage== 0){
                hasPreviousPage=false;
            }
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            readBookChapterItemAdapter.notifyDataSetChanged();
            setDisplay();
            return true;
        }
    }

    private boolean goToPriviousChapter(){
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage=false;
            hasPreviousChapter=false;
            hasNextPage=false;
            hasNextChapter=false;
            setDisplay();
            return false;
        }else{
            hasNextChapter=true;
            if(currentChapter.getIndex()==0){
                Toast.makeText(this, "已经是第一章！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            }else{
                currentChapter = allInfo.get(currentChapter.getIndex()-1);
            }
            if(currentChapter.getIndex()==0){
                hasPreviousChapter=false;
            }
            if(currentChapter.getPage()!=currentPage){
                currentPage=currentChapter.getPage();
                currentDisplayInfo.clear();
                currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
                readBookChapterItemAdapter.notifyDataSetChanged();
                if(currentPage == 0){
                    hasPreviousPage=false;
                }else{
                    hasPreviousPage=true;
                }
                if(currentPage== splitAllInfo.size() - 1){
                    hasNextPage=false;
                }else{
                    hasNextPage=true;
                }
            }
            setDisplay();
            getChapterContent(currentChapter);
            return true;
        }
    }

    private void setDisplay(){
        setDisplayIml(previousChapterTv,hasPreviousChapter);
        setDisplayIml(nextChapterTv,hasNextChapter);
        setDisplayIml(previousPageTv,hasPreviousPage);
        setDisplayIml(nextPageTv,hasNextPage);
    }
    private void setDisplayIml(TextView displayView,boolean isEnable){
        if(isEnable){
            displayView.setTextColor(getResources().getColor(android.R.color.black));
        }else{
            displayView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }
}
