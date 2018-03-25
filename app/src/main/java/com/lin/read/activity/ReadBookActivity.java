package com.lin.read.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.lin.read.R;
import com.lin.read.adapter.BookChapterAdapter;
import com.lin.read.bookmark.BookMarkBean;
import com.lin.read.bookmark.BookMarkSharePres;
import com.lin.read.bookmark.ReadInfo;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.filter.search.GetChapterContentTask;
import com.lin.read.filter.search.GetChapterInfoTask;
import com.lin.read.utils.Constants;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.view.DialogUtil;

import java.text.NumberFormat;
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
    private TextView chapterNameTv;
    private TextView chapterContentTv;
    private TextView previousChapterTv;
    private TextView nextChapterTv;
    private TextView readProgressTv;

    private ListView chapterLv;
    private BookChapterAdapter bookChapterAdapter;

    private ArrayList<BookChapterInfo> currentDisplayInfo;
    private List<BookChapterInfo> allInfo;
    private List<ArrayList<BookChapterInfo>> splitAllInfo;

    private EditText skipPageEt;
    private TextView totalPageTv;
    private TextView skipTv;

    private ScrollView readScroll;

    private boolean isShowMenu = false;

    private ReadInfo currentReadInfo;

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
        chapterMenu = (ImageView) findViewById(R.id.chapter_menu);
        layoutMenu = findViewById(R.id.layout_chapters);
        layoutMenuBlank = findViewById(R.id.chapter_blank_view);
        bookNameTv = (TextView) findViewById(R.id.chapter_bookName);
        previousPageTv = (TextView) findViewById(R.id.chapter_previous_page);
        nextPageTv = (TextView) findViewById(R.id.chapter_next_page);
        chapterLv = (ListView) findViewById(R.id.lv_chapters);
        chapterNameTv = (TextView) findViewById(R.id.chapter_name);
        chapterContentTv = (TextView) findViewById(R.id.chapter_content);
        previousChapterTv = (TextView) findViewById(R.id.chapter_previous_chapter);
        nextChapterTv = (TextView) findViewById(R.id.chapter_next_chapter);
        readScroll = (ScrollView) findViewById(R.id.read_scroll);
        skipPageEt = (EditText) findViewById(R.id.chapter_skip_page);
        skipTv = (TextView) findViewById(R.id.chapter_skip);
        totalPageTv = (TextView) findViewById(R.id.chapter_total_page);
        readProgressTv = (TextView) findViewById(R.id.read_progress);

        bookChapterAdapter = new BookChapterAdapter(this, currentDisplayInfo);
        chapterLv.setAdapter(bookChapterAdapter);

        bookNameTv.setText(bookInfo.getBookName());
        currentReadInfo=new ReadInfo();

        chapterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
                goToPage(currentReadInfo.getCurrentChapter().getPage() + 1);
            }
        });
        layoutMenuBlank.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideSoft();
                clearInput();
                hideMenu();
            }
        });
        previousPageTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideSoft();
                clearInput();
                if (currentReadInfo.isHasPreviousPage()) {
                    goToPriviousPage();
                } else {
                    Toast.makeText(ReadBookActivity.this, "已经是第一页！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextPageTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideSoft();
                clearInput();
                if (currentReadInfo.isHasNextPage()) {
                    goToNextPage();
                } else {
                    Toast.makeText(ReadBookActivity.this, "已经是最后一页！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bookChapterAdapter.setOnChapterClickListener(new BookChapterAdapter.OnChapterClickListener() {
            @Override
            public void onChapterClick(BookChapterInfo bookChapterInfo) {
                hideSoft();
                clearInput();
                hideMenu();
                if(!bookChapterInfo.isCurrentReading()){
                    ReadInfo afterClick = new ReadInfo();
                    copyReadInfo(currentReadInfo,afterClick);

                    afterClick.setCurrentChapter(bookChapterInfo);
                    getChapterContent(afterClick);
                }
            }
        });
        previousChapterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (currentReadInfo.isHasPreviousChapter()) {
                    goToPriviousChapter();
                } else {
                    Toast.makeText(ReadBookActivity.this, "已经是第一章！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextChapterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (currentReadInfo.isHasNextChapter()) {
                    goToNextChapter();
                } else {
                    Toast.makeText(ReadBookActivity.this, "已经是最后一章！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoft();
                String inputPage = skipPageEt.getText().toString();
                try {
                    int page = Integer.parseInt(inputPage);
                    int totalPage = splitAllInfo == null ? 0 : splitAllInfo.size();
                    if (page < 1 || page > totalPage) {
                        Toast.makeText(ReadBookActivity.this, "输入错误！", Toast.LENGTH_SHORT).show();
                        skipPageEt.setText("");
                        skipPageEt.setHint((currentReadInfo.getCurrentPage() + 1) + "");
                    } else {
                        goToPage(page);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(ReadBookActivity.this, "输入错误！", Toast.LENGTH_SHORT).show();
                    skipPageEt.setText("");
                    skipPageEt.setHint((currentReadInfo.getCurrentPage() + 1) + "");
                }
            }
        });
    }

    private void showMenu() {
        isShowMenu = true;
        Animation anim = AnimationUtils.loadAnimation(ReadBookActivity.this, R.anim.set_scan_filter_menu_in);
        layoutMenu.startAnimation(anim);
        layoutMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isShowMenu) {
            hideSoft();
            clearInput();
            hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void hideMenu() {
        isShowMenu = false;
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
            public void onSucc(List<BookChapterInfo> allInfo, List<ArrayList<BookChapterInfo>> splitInfos) {
                if(splitInfos==null||splitInfos.size()==0){
                    currentReadInfo.setHasPreviousChapter(false);
                    currentReadInfo.setHasNextChapter(false);
                    currentReadInfo.setHasPreviousPage(false);
                    currentReadInfo.setHasNextPage(false);
                    setDisplay();
                    Toast.makeText(ReadBookActivity.this,"未获取到数据!",Toast.LENGTH_SHORT).show();
                    DialogUtil.getInstance().hideLoadingView();
                    finish();
                    return;
                }

                int markPage = 0;
                int markIndex = 0;
                BookMarkBean bookMarkBean = getBookMark();
                if (bookMarkBean != null) {
                    markPage = bookMarkBean.getPage() >= splitInfos.size() ? (splitInfos.size() - 1) : bookMarkBean.getPage();
                    markIndex = bookMarkBean.getIndex() >= allInfo.size() ? (allInfo.size() - 1) : bookMarkBean.getIndex();
                }

                totalPageTv.setText("" + splitInfos.size());
                skipPageEt.setText("");
                skipPageEt.setHint(markPage + 1 + "");
                ReadBookActivity.this.allInfo = allInfo;
                splitAllInfo = splitInfos;
                currentReadInfo.setCurrentPage(markPage);

                currentDisplayInfo.clear();
                currentDisplayInfo.addAll(splitAllInfo.get(currentReadInfo.getCurrentPage()));
                if (splitInfos.size() < 2) {
                    currentReadInfo.setHasPreviousPage(false);
                    currentReadInfo.setHasNextPage(false);
                } else {
                    if (currentReadInfo.getCurrentPage() == 0) {
                        currentReadInfo.setHasPreviousPage(false);
                    } else {
                        currentReadInfo.setHasPreviousPage(true);
                    }
                    if (currentReadInfo.getCurrentPage() == splitInfos.size() - 1) {
                        currentReadInfo.setHasNextPage(false);

                    } else {
                        currentReadInfo.setHasNextPage(true);
                    }
                }
                if (allInfo == null || allInfo.size() < 2) {
                    currentReadInfo.setHasPreviousChapter(false);
                    currentReadInfo.setHasNextChapter(false);
                } else {
                    if (markIndex == 0) {
                        currentReadInfo.setHasPreviousChapter(false);
                    } else {
                        currentReadInfo.setHasPreviousChapter(true);
                    }
                    if (markIndex == allInfo.size() - 1) {
                        currentReadInfo.setHasNextChapter(false);
                    } else {
                        currentReadInfo.setHasNextChapter(true);
                    }
                }
                currentReadInfo.setCurrentChapter(allInfo.get(markIndex));
                ReadInfo afterLoad=new ReadInfo();
                copyReadInfo(currentReadInfo,afterLoad);
                setDisplay();
                DialogUtil.getInstance().hideLoadingView();
                getChapterContent(afterLoad);
            }

            @Override
            public void onFailed() {
                Toast.makeText(ReadBookActivity.this,"网络请求失败!",Toast.LENGTH_SHORT).show();
                DialogUtil.getInstance().hideLoadingView();
                finish();
            }
        });
        getChapterInfoTask.execute();
    }

    private void getChapterContent(final ReadInfo afterClickInfo) {
        DialogUtil.getInstance().showLoadingDialog(this);
        GetChapterContentTask task = new GetChapterContentTask(this, afterClickInfo.getCurrentChapter(), new GetChapterContentTask.OnTaskListener() {
            @Override
            public void onSucc(String chapter, String content) {
                DialogUtil.getInstance().hideLoadingView();
                chapterNameTv.setText(chapter);
                chapterContentTv.setText(Html.fromHtml(content));
                if (afterClickInfo.getCurrentChapter().getIndex() == 0) {
                    afterClickInfo.setHasPreviousChapter(false);
                    afterClickInfo.setHasNextChapter(true);
                } else if (afterClickInfo.getCurrentChapter().getIndex() == allInfo.size() - 1) {
                    afterClickInfo.setHasPreviousChapter(true);
                    afterClickInfo.setHasNextChapter(false);
                } else {
                    afterClickInfo.setHasPreviousChapter(true);
                    afterClickInfo.setHasNextChapter(true);
                }
                copyReadInfo(afterClickInfo,currentReadInfo);
                currentDisplayInfo.clear();
                currentDisplayInfo.addAll(splitAllInfo.get(currentReadInfo.getCurrentPage()));
                bookChapterAdapter.notifyData(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter());
                setDisplay();
                saveBookMark();
                readProgressTv.setText(getProgress());
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        readScroll.scrollTo(0, 0);
                    }
                });
            }

            @Override
            public void onFailed() {
                Toast.makeText(ReadBookActivity.this,"网络请求失败!",Toast.LENGTH_SHORT).show();
                DialogUtil.getInstance().hideLoadingView();
            }
        });
        task.execute();
    }

    private boolean goToNextPage() {
        currentReadInfo.setHasPreviousPage(true);
        if (currentReadInfo.getCurrentPage() == splitAllInfo.size() - 1) {
            Toast.makeText(this, "已经是最后一页！", Toast.LENGTH_SHORT).show();
            setDisplay();
            return false;
        } else {
            currentReadInfo.setCurrentPage(currentReadInfo.getCurrentPage() + 1);
        }
        if (currentReadInfo.getCurrentPage() == splitAllInfo.size() - 1) {
            currentReadInfo.setHasNextPage(false);
        }
        skipPageEt.setText("");
        skipPageEt.setHint((currentReadInfo.getCurrentPage() + 1) + "");
        currentDisplayInfo.clear();
        currentDisplayInfo.addAll(splitAllInfo.get(currentReadInfo.getCurrentPage()));
        bookChapterAdapter.notifyData(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter());
        chapterLv.smoothScrollToPosition(bookChapterAdapter.getCurrentPosition(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter()));
        setDisplay();
        return true;
    }


    private boolean goToNextChapter() {
        ReadInfo afterClick = new ReadInfo();
        copyReadInfo(currentReadInfo,afterClick);

        afterClick.setHasPreviousChapter(true);
        if (afterClick.getCurrentChapter().getIndex() == allInfo.size() - 1) {
            Toast.makeText(this, "已经是最后一章！", Toast.LENGTH_SHORT).show();
            setDisplay();
            return false;
        } else {
            afterClick.setCurrentChapter(allInfo.get(afterClick.getCurrentChapter().getIndex() + 1));
        }
        if (afterClick.getCurrentChapter().getIndex() == allInfo.size() - 1) {
            afterClick.setHasNextChapter(false);
        }
        if (afterClick.getCurrentChapter().getPage() != afterClick.getCurrentPage()) {
            afterClick.setCurrentPage(afterClick.getCurrentChapter().getPage());
            skipPageEt.setText("");
            skipPageEt.setHint((afterClick.getCurrentPage() + 1) + "");
            if (afterClick.getCurrentPage() == 0) {
                afterClick.setHasPreviousPage(false);
            } else {
                afterClick.setHasPreviousPage(true);
            }
            if (afterClick.getCurrentPage() == splitAllInfo.size() - 1) {
                afterClick.setHasNextPage(false);
            } else {
                afterClick.setHasNextPage(true);
            }
        }
        getChapterContent(afterClick);
        return true;
    }

    private boolean goToPriviousPage() {
        currentReadInfo.setHasNextPage(true);
        if (currentReadInfo.getCurrentPage() == 0) {
            Toast.makeText(this, "已经是第一页！", Toast.LENGTH_SHORT).show();
            setDisplay();
            return false;
        } else {
            currentReadInfo.setCurrentPage(currentReadInfo.getCurrentPage() - 1);
        }
        if (currentReadInfo.getCurrentPage() == 0) {
            currentReadInfo.setHasPreviousPage(false);
        }
        skipPageEt.setText("");
        skipPageEt.setHint((currentReadInfo.getCurrentPage() + 1) + "");
        currentDisplayInfo.clear();
        currentDisplayInfo.addAll(splitAllInfo.get(currentReadInfo.getCurrentPage()));
        bookChapterAdapter.notifyData(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter());
        chapterLv.smoothScrollToPosition(bookChapterAdapter.getCurrentPosition(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter()));
        setDisplay();
        return true;
    }

    private boolean goToPriviousChapter() {
        ReadInfo afterClick = new ReadInfo();
        copyReadInfo(currentReadInfo,afterClick);

        afterClick.setHasNextChapter(true);
        if (afterClick.getCurrentChapter().getIndex() == 0) {
            Toast.makeText(this, "已经是第一章！", Toast.LENGTH_SHORT).show();
            setDisplay();
            return false;
        } else {
            afterClick.setCurrentChapter(allInfo.get(afterClick.getCurrentChapter().getIndex() - 1));
        }
        if (afterClick.getCurrentChapter().getIndex() == 0) {
            afterClick.setHasPreviousChapter(false);
        }
        if (afterClick.getCurrentChapter().getPage() != afterClick.getCurrentPage()) {
            skipPageEt.setText("");
            skipPageEt.setHint((afterClick.getCurrentPage() + 1) + "");
            afterClick.setCurrentPage(afterClick.getCurrentChapter().getPage());

            if (afterClick.getCurrentPage() == 0) {
                afterClick.setHasPreviousPage(false);
            } else {
                afterClick.setHasPreviousPage(true);
            }
            if (afterClick.getCurrentPage() == splitAllInfo.size() - 1) {
                afterClick.setHasNextPage(false);
            } else {
                afterClick.setHasNextPage(true);
            }
        }
        getChapterContent(afterClick);
        return true;
    }

    private boolean goToPage(int page) {
        int index = page - 1;
        if (index == currentReadInfo.getCurrentPage()) {
            int positon = bookChapterAdapter.getCurrentPosition(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter());
            chapterLv.smoothScrollToPosition(positon);
            return false;
        }
        if (index == 0) {
            currentReadInfo.setHasNextPage(true);
            currentReadInfo.setHasPreviousPage(false);
        } else if (index == splitAllInfo.size() - 1) {
            currentReadInfo.setHasNextPage(false);
            currentReadInfo.setHasPreviousPage(true);
        } else {
            currentReadInfo.setHasNextPage(true);
            currentReadInfo.setHasPreviousPage(true);
        }

        currentReadInfo.setCurrentPage(index);
        skipPageEt.setText("");
        skipPageEt.setHint((currentReadInfo.getCurrentPage() + 1) + "");
        currentDisplayInfo.clear();
        currentDisplayInfo.addAll(splitAllInfo.get(currentReadInfo.getCurrentPage()));
        bookChapterAdapter.notifyData(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter());
        int positon = bookChapterAdapter.getCurrentPosition(currentReadInfo.getCurrentPage(), currentReadInfo.getCurrentChapter());
        chapterLv.smoothScrollToPosition(positon);
        setDisplay();
        return true;
    }

    private void setDisplay() {
        setDisplayIml(previousChapterTv, currentReadInfo.isHasPreviousChapter());
        setDisplayIml(nextChapterTv, currentReadInfo.isHasNextChapter());
        setDisplayIml(previousPageTv, currentReadInfo.isHasPreviousPage());
        setDisplayIml(nextPageTv, currentReadInfo.isHasNextPage());
    }

    private void setDisplayIml(TextView displayView, boolean isEnable) {
        if (isEnable) {
            displayView.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            displayView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void hideSoft() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(skipPageEt.getWindowToken(), 0);
    }

    private void clearInput() {
        skipPageEt.setText("");
        skipPageEt.setHint((currentReadInfo.getCurrentPage() + 1) + "");
    }

    private void saveBookMark() {
        BookMarkBean markBean = new BookMarkBean();
        markBean.setBookInfo(bookInfo);
        markBean.setLastReadTime(System.currentTimeMillis());
        markBean.setPage(currentReadInfo.getCurrentChapter().getPage());
        markBean.setIndex(currentReadInfo.getCurrentChapter().getIndex());
        markBean.setLastReadChapter(currentReadInfo.getCurrentChapter().getChapterName());
        BookMarkSharePres.saveBookMark(this, markBean);
    }

    private BookMarkBean getBookMark() {
        return BookMarkSharePres.getBookMark(this,bookInfo);
    }

    private void copyReadInfo(ReadInfo fromInfo, ReadInfo toInfo) {
        if (fromInfo == null || toInfo == null) {
            return;
        }
        toInfo.setCurrentPage(fromInfo.getCurrentPage());
        toInfo.setHasPreviousPage(fromInfo.isHasPreviousPage());
        toInfo.setHasNextPage(fromInfo.isHasNextPage());
        toInfo.setHasPreviousChapter(fromInfo.isHasPreviousChapter());
        toInfo.setHasNextChapter(fromInfo.isHasNextChapter());
        toInfo.setCurrentChapter(fromInfo.getCurrentChapter());
    }

    private String getProgress(){
        int total = allInfo.size();
        int current = currentReadInfo.getCurrentChapter().getIndex() + 1;
        if (total == 0 || current > total) {
            return "";
        }
        float num= (float)current/total;
        NumberFormat format= NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(2);
        return format.format(num);
    }
}
