package com.lin.read.activity;

import android.app.Activity;
import android.content.Context;
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
    private TextView chapterNameTv;
    private TextView chapterContentTv;
    private TextView previousChapterTv;
    private TextView nextChapterTv;

    private ListView chapterLv;
    private BookChapterAdapter bookChapterAdapter;

    private ArrayList<BookChapterInfo> currentDisplayInfo;
    private List<BookChapterInfo> allInfo;
    private List<ArrayList<BookChapterInfo>> splitAllInfo;

    private EditText skipPageEt;
    private TextView totalPageTv;
    private TextView skipTv;

    private int currentPage = 0;

    private ScrollView readScroll;

    private BookChapterInfo currentChapter;

    private boolean hasPreviousPage = false;
    private boolean hasNextPage = false;
    private boolean hasPreviousChapter = false;
    private boolean hasNextChapter = false;

    private boolean isShowMenu = false;

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

        bookChapterAdapter = new BookChapterAdapter(this, currentDisplayInfo);
        chapterLv.setAdapter(bookChapterAdapter);

        bookNameTv.setText(bookInfo.getBookName());

        chapterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
                goToPage(currentChapter.getPage()+1);
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
                if (hasPreviousPage) {
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
                if (hasNextPage) {
                    goToNextPage();
                } else {
                    Toast.makeText(ReadBookActivity.this, "已经是最后一页！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bookChapterAdapter.setOnChapterClickListener(new BookChapterAdapter.OnChapterClickListener() {
            @Override
            public void onChapterClick(BookChapterInfo bookChapterInfo) {
                currentChapter = bookChapterInfo;
                bookChapterAdapter.notifyData(currentPage,currentChapter);
                hideSoft();
                clearInput();
                hideMenu();
                getChapterContent(bookChapterInfo);
            }
        });
        previousChapterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (hasPreviousChapter) {
                    goToPriviousChapter();
                } else {
                    Toast.makeText(ReadBookActivity.this, "已经是第一章！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextChapterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (hasNextChapter) {
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
                        skipPageEt.setHint((currentPage + 1) + "");
                    } else {
                        goToPage(page);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(ReadBookActivity.this, "输入错误！", Toast.LENGTH_SHORT).show();
                    skipPageEt.setText("");
                    skipPageEt.setHint((currentPage + 1) + "");
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
                setDisplay();
                if (splitInfos != null && splitInfos.size() > 0) {
                    int markPage=0;
                    int markIndex=0;
                    BookMarkBean bookMarkBean=getBookMark();
                    if(bookMarkBean!=null){
                        markPage = bookMarkBean.getPage() >= splitInfos.size() ? (splitInfos.size() - 1) : bookMarkBean.getPage();
                        markIndex = bookMarkBean.getIndex() >= allInfo.size() ? (allInfo.size() - 1) : bookMarkBean.getIndex();
                    }

                    totalPageTv.setText("" + splitInfos.size());
                    skipPageEt.setText("");
                    skipPageEt.setHint(markPage + 1 + "");
                    ReadBookActivity.this.allInfo = allInfo;
                    splitAllInfo = splitInfos;
                    currentPage = markPage;
                    currentChapter = allInfo.get(markIndex);
                    currentDisplayInfo.clear();
                    currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
                    if (splitInfos.size() < 2) {
                        hasPreviousPage = false;
                        hasNextPage = false;
                    } else {
                        if (currentPage == 0) {
                            hasPreviousPage = false;
                        } else {
                            hasPreviousPage = true;
                        }
                        if (currentPage == splitInfos.size() - 1) {
                            hasNextPage = false;
                        } else {
                            hasNextPage = true;
                        }
                    }
                    if (allInfo == null || allInfo.size() < 2) {
                        hasPreviousChapter = false;
                        hasNextChapter = false;
                    } else {
                        if (markIndex == 0) {
                            hasPreviousChapter = false;
                        } else {
                            hasPreviousChapter = true;
                        }
                        if (markIndex == allInfo.size() - 1) {
                            hasNextChapter = false;
                        } else {
                            hasNextChapter = true;
                        }
                    }
                    setDisplay();
                    bookChapterAdapter.notifyData(currentPage,currentChapter);
                    getChapterContent(currentChapter);
                } else {
                    totalPageTv.setText("0");
                    skipPageEt.setText("");
                    skipPageEt.setHint("0");
                }
                DialogUtil.getInstance().hideLoadingView();
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

    private void getChapterContent(final BookChapterInfo bookChapterInfo) {
        DialogUtil.getInstance().showLoadingDialog(this);
        GetChapterContentTask task = new GetChapterContentTask(this, bookChapterInfo, new GetChapterContentTask.OnTaskListener() {
            @Override
            public void onSucc(String chapter, String content) {
                DialogUtil.getInstance().hideLoadingView();
                chapterNameTv.setText(chapter);
                chapterContentTv.setText(Html.fromHtml(content));
                if (bookChapterInfo.getIndex() == 0) {
                    hasPreviousChapter = false;
                    hasNextChapter = true;
                } else if (bookChapterInfo.getIndex() == allInfo.size() - 1) {
                    hasPreviousChapter = true;
                    hasNextChapter = false;
                } else {
                    hasPreviousChapter = true;
                    hasNextChapter = true;
                }
                setDisplay();
                saveBookMark();
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
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage = false;
            hasPreviousChapter = false;
            hasNextPage = false;
            hasNextChapter = false;
            setDisplay();
            return false;
        } else {
            hasPreviousPage = true;
            if (currentPage == splitAllInfo.size() - 1) {
                Toast.makeText(this, "已经是最后一页！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            } else {
                currentPage++;
            }
            if (currentPage == splitAllInfo.size() - 1) {
                hasNextPage = false;
            }
            skipPageEt.setText("");
            skipPageEt.setHint((currentPage + 1) + "");
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            bookChapterAdapter.notifyData(currentPage,currentChapter);
            chapterLv.smoothScrollToPosition(bookChapterAdapter.getCurrentPosition(currentPage,currentChapter));
            setDisplay();
            return true;
        }
    }


    private boolean goToNextChapter() {
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage = false;
            hasPreviousChapter = false;
            hasNextPage = false;
            hasNextChapter = false;
            setDisplay();
            return false;
        } else {
            hasPreviousChapter = true;
            if (currentChapter.getIndex() == allInfo.size() - 1) {
                Toast.makeText(this, "已经是最后一章！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            } else {
                currentChapter = allInfo.get(currentChapter.getIndex() + 1);
            }
            if (currentChapter.getIndex() == allInfo.size() - 1) {
                hasNextChapter = false;
            }
            if (currentChapter.getPage() != currentPage) {
                currentPage = currentChapter.getPage();
                skipPageEt.setText("");
                skipPageEt.setHint((currentPage + 1) + "");
                if (currentPage == 0) {
                    hasPreviousPage = false;
                } else {
                    hasPreviousPage = true;
                }
                if (currentPage == splitAllInfo.size() - 1) {
                    hasNextPage = false;
                } else {
                    hasNextPage = true;
                }
            }
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            bookChapterAdapter.notifyData(currentPage,currentChapter);
            setDisplay();
            getChapterContent(currentChapter);
            return true;
        }
    }

    private boolean goToPriviousPage() {
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage = false;
            hasPreviousChapter = false;
            hasNextPage = false;
            hasNextChapter = false;
            setDisplay();
            return false;
        } else {
            hasNextPage = true;
            if (currentPage == 0) {
                Toast.makeText(this, "已经是第一页！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            } else {
                currentPage--;
            }
            if (currentPage == 0) {
                hasPreviousPage = false;
            }
            skipPageEt.setText("");
            skipPageEt.setHint((currentPage + 1) + "");
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            bookChapterAdapter.notifyData(currentPage,currentChapter);
            chapterLv.smoothScrollToPosition(bookChapterAdapter.getCurrentPosition(currentPage,currentChapter));
            setDisplay();
            return true;
        }
    }

    private boolean goToPriviousChapter() {
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage = false;
            hasPreviousChapter = false;
            hasNextPage = false;
            hasNextChapter = false;
            setDisplay();
            return false;
        } else {
            hasNextChapter = true;
            if (currentChapter.getIndex() == 0) {
                Toast.makeText(this, "已经是第一章！", Toast.LENGTH_SHORT).show();
                setDisplay();
                return false;
            } else {
                currentChapter = allInfo.get(currentChapter.getIndex() - 1);
            }
            if (currentChapter.getIndex() == 0) {
                hasPreviousChapter = false;
            }
            if (currentChapter.getPage() != currentPage) {
                skipPageEt.setText("");
                skipPageEt.setHint((currentPage + 1) + "");
                currentPage = currentChapter.getPage();

                if (currentPage == 0) {
                    hasPreviousPage = false;
                } else {
                    hasPreviousPage = true;
                }
                if (currentPage == splitAllInfo.size() - 1) {
                    hasNextPage = false;
                } else {
                    hasNextPage = true;
                }
            }
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            bookChapterAdapter.notifyData(currentPage,currentChapter);
            setDisplay();
            getChapterContent(currentChapter);
            return true;
        }
    }

    private boolean goToPage(int page) {
        if (splitAllInfo == null || splitAllInfo.size() == 0) {
            Toast.makeText(this, "无数据可显示！", Toast.LENGTH_SHORT).show();
            hasPreviousPage = false;
            hasPreviousChapter = false;
            hasNextPage = false;
            hasNextChapter = false;
            setDisplay();
            return false;
        } else {
            int index = page - 1;
            if (index == currentPage) {
                int positon=bookChapterAdapter.getCurrentPosition(currentPage,currentChapter);
                chapterLv.smoothScrollToPosition(positon);
                return false;
            }
            if (index == 0) {
                hasNextPage = true;
                hasPreviousPage = false;
            } else if (index == splitAllInfo.size() - 1) {
                hasNextPage = false;
                hasPreviousPage = true;
            } else {
                hasNextPage = true;
                hasPreviousPage = true;
            }

            currentPage = index;
            skipPageEt.setText("");
            skipPageEt.setHint((currentPage + 1) + "");
            currentDisplayInfo.clear();
            currentDisplayInfo.addAll(splitAllInfo.get(currentPage));
            bookChapterAdapter.notifyData(currentPage,currentChapter);
            int positon=bookChapterAdapter.getCurrentPosition(currentPage,currentChapter);
            chapterLv.smoothScrollToPosition(positon);
            setDisplay();
            return true;
        }
    }

    private void setDisplay() {
        setDisplayIml(previousChapterTv, hasPreviousChapter);
        setDisplayIml(nextChapterTv, hasNextChapter);
        setDisplayIml(previousPageTv, hasPreviousPage);
        setDisplayIml(nextPageTv, hasNextPage);
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
        skipPageEt.setHint((currentPage + 1) + "");
    }

    private void saveBookMark() {
        BookMarkBean markBean = new BookMarkBean();
        markBean.setBookName(bookInfo.getBookName());
        markBean.setAuthorName(bookInfo.getAuthorName());
        markBean.setWebType(bookInfo.getWebType());
        markBean.setPage(currentChapter.getPage());
        markBean.setIndex(currentChapter.getIndex());
        String key = markBean.getKey();
        String value = new GsonBuilder().create().toJson(markBean);
        Log.e("Test", "save book mark-->" + key + "=" + value);
        BookMarkSharePres.saveBookMark(this, key, value);
    }

    private BookMarkBean getBookMark() {
        BookMarkBean markBean = new BookMarkBean();
        markBean.setBookName(bookInfo.getBookName());
        markBean.setAuthorName(bookInfo.getAuthorName());
        markBean.setWebType(bookInfo.getWebType());
        String key = markBean.getKey();
        String value=BookMarkSharePres.getBookMark(this,key);
        try{
            Log.e("Test", "get book mark-->" + key + "=" + value);
            markBean=new GsonBuilder().create().fromJson(value,BookMarkBean.class);
            return  markBean;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
