package com.lin.read.activity.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.lin.read.R;
import com.lin.read.activity.LoadingDialogActivity;
import com.lin.read.activity.MainActivity;
import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.decoration.ScanTypeItemDecoration;
import com.lin.read.filter.BookComparatorUtil;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.scan.qidian.QiDianConstants;
import com.lin.read.fragment.ScanFragment;
import com.lin.read.utils.Constants;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.utils.NumberInputFilter;
import com.lin.read.filter.BookComparator;
import com.lin.read.utils.ScoreInputFilter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lisonglin on 2018/4/5.
 */

public class QiDianScanUtil {
    private RecyclerView scanRankTypeRcv;
    private RecyclerView scanBookTypeRcv;
    private RecyclerView scanDateTypeRcv;

    private ScanTypeAdapter scanRankTypeAdapter;
    private ScanTypeAdapter scanBookTypeAdapter;
    private ScanTypeAdapter scanDateTypeAdapter;

    private LinearLayout scanDateLinearLayout;

    private EditText scoreEt;
    private EditText scoreNumEt;
    private EditText wordsNumEt;
    private EditText recommendEt;

    private ScrollView scrollView;

    private Button scanOK;

    private boolean isSoftInputDisplay = false;

    //添加用来在键盘显示的时候，改变该view的高度（键盘高度），从而把layout顶上去；若是键盘隐藏，则将该view高度设置为0
    private View tempViewForSoft;

    //用来记录上次监听到的屏幕高度变化时的高度，避免重复处理，否则会陷入死循环
    int lastHeight = -1;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    private Activity activity;
    private BookComparatorUtil bookComparatorUtil;

    public void initQiDianViews(final ScanFragment scanFragment, View view, final Handler handler) {
        activity = scanFragment.getActivity();
        scanRankTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_rank);
        scanBookTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_booktype);
        scanDateTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_date);
        scanDateLinearLayout = (LinearLayout) view.findViewById(R.id.ll_scan_date);
        scoreEt = (EditText) view.findViewById(R.id.et_socre);
        scoreNumEt = (EditText) view.findViewById(R.id.et_socre_num);
        wordsNumEt = (EditText) view.findViewById(R.id.et_words_num);
        recommendEt = (EditText) view.findViewById(R.id.et_recommend);
        scanOK = (Button) view.findViewById(R.id.scan_ok);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        tempViewForSoft = view.findViewById(R.id.tempView_for_soft);

        scanRankTypeAdapter = new ScanTypeAdapter(activity, QiDianConstants.scanRankTypeList);
        scanBookTypeAdapter = new ScanTypeAdapter(activity, QiDianConstants.scanBookTypeList);
        scanDateTypeAdapter = new ScanTypeAdapter(activity, QiDianConstants.scanDateTypeList);

        setAdapter(activity);
        setInputFilter();

        scanRankTypeAdapter.setOnScanItemClickListener(new ScanTypeAdapter.OnScanItemClickListener() {
            @Override
            public void onItemClick(int position, String clickText) {
                Log.e("Test", "current position:" + position);
                if (!StringUtils.isEmpty(clickText)) {
                    if (clickText.equals(QiDianConstants.QD_RANK_RECOMMEND) || clickText.equals(QiDianConstants.QD_RANK_FINAL)) {
                        scanDateLinearLayout.setVisibility(View.VISIBLE);
                        scanDateTypeAdapter.setDefaultChecked(QiDianConstants.QD_DATE_WEEK);
                        scanDateTypeAdapter.notifyDataSetChanged();
                    } else {
                        scanDateLinearLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        scanOK.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                scanFragment.hideFilterLayout();
                SearchInfo searchInfo = getSearchInfo();
                if (searchInfo != null) {
                    Log.e("Test", searchInfo.toString());
//                showScaningDialog();
                    Intent intent = new Intent(activity, LoadingDialogActivity.class);
                    intent.putExtra(Constants.KEY_SEARCH_INFO, searchInfo);
                    scanFragment.startActivityForResult(intent, Constants.SCAN_REQUEST_CODE);
                }
            }
        });

        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                if (heightDifference == 0) {
                    isSoftInputDisplay = false;
                    android.util.Log.e("Test", "hide softInput!---" + heightDifference);
                } else {
                    isSoftInputDisplay = true;
                    android.util.Log.e("Test", "show softInput!---" + heightDifference);
                }

                //若当前height改变还未处理过
                if (lastHeight != heightDifference) {
                    //将该height设置到tempViewForSoft
                    ViewGroup.LayoutParams params = tempViewForSoft.getLayoutParams();
                    params.height = heightDifference;
                    if (params.height != 0) {
                        params.height = 200;
                    }
                    tempViewForSoft.setLayoutParams(params);
                    //若此时是键盘显示
                    if (heightDifference != 0) {
                        ((MainActivity) activity).hideBottomViews(true);
                        scrollToEndAndRequestFocus(handler, scrollView);
                    } else {
                        ((MainActivity) activity).hideBottomViews(false);
                    }
                    //将当前高度记为已处理，否则fullScroll会requestLayout,会再次触发onGlobalLayout，这样会陷入死循环
                    lastHeight = heightDifference;
                }
            }
        };
    }

    public String getRankType() {
        return scanRankTypeAdapter.getCheckedText();
    }

    public void hideDateLayout(boolean hide) {
        if (hide) {
            scanDateLinearLayout.setVisibility(View.GONE);
        } else {
            scanDateLinearLayout.setVisibility(View.VISIBLE);
            scanDateTypeAdapter.setDefaultChecked(QiDianConstants.QD_DATE_WEEK);
            scanDateTypeAdapter.notifyDataSetChanged();
        }
    }

    private void setAdapter(Activity activity) {
        scanRankTypeRcv.setLayoutManager(new GridLayoutManager(activity, 4));
        scanRankTypeAdapter.setDefaultChecked(QiDianConstants.QD_RANK_RECOMMEND);
        scanRankTypeRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanRankTypeRcv.setAdapter(scanRankTypeAdapter);

        scanBookTypeRcv.setLayoutManager(new GridLayoutManager(activity, 4));
        scanBookTypeAdapter.setDefaultChecked(QiDianConstants.QD_BOOK_XUAN_HUAN);
        scanBookTypeRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanBookTypeRcv.setAdapter(scanBookTypeAdapter);

        scanDateTypeRcv.setLayoutManager(new GridLayoutManager(activity, 4));
        scanDateTypeAdapter.setDefaultChecked(QiDianConstants.QD_DATE_WEEK);
        scanDateTypeRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanDateTypeRcv.setAdapter(scanDateTypeAdapter);
        scanDateLinearLayout.setVisibility(View.VISIBLE);
    }

    private void setInputFilter() {
        scoreEt.setFilters(new InputFilter[]{new ScoreInputFilter()});
        scoreNumEt.setFilters(new InputFilter[]{new NumberInputFilter(6)});
        wordsNumEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
        recommendEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
    }

    /**
     * 获取触发键盘时获取焦点的view
     *
     * @return
     */
    public EditText getFocusEt() {

        if (scoreEt.hasFocus()) {
            return scoreEt;
        }
        if (scoreNumEt.hasFocus()) {
            return scoreNumEt;
        }
        if (wordsNumEt.hasFocus()) {
            return wordsNumEt;
        }
        if (recommendEt.hasFocus()) {
            return recommendEt;
        }
        return null;
    }

    public SearchInfo getSearchInfo() {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setWebType(Constants.WEB_QIDIAN);
        searchInfo.setRankType(scanRankTypeAdapter.getCheckedInfo().getId());
        searchInfo.setBookType(scanBookTypeAdapter.getCheckedInfo().getId());
        if (scanDateLinearLayout.getVisibility() == View.VISIBLE) {
            searchInfo.setDateType(scanDateTypeAdapter.getCheckedInfo().getId());
        }

        String score = StringUtils.setQiDianDefaultValue(scoreEt.getText().toString(), "8.0", StringUtils.INPUTTYPE_FLOAT);
        String scoreNum = StringUtils.setQiDianDefaultValue(scoreNumEt.getText().toString(), "300", StringUtils.INPUTTYPE_INTEGER);
        String wordsNum = StringUtils.setQiDianDefaultValue(wordsNumEt.getText().toString(), "200", StringUtils.INPUTTYPE_INTEGER);
        String recommend = StringUtils.setQiDianDefaultValue(recommendEt.getText().toString(), "50", StringUtils.INPUTTYPE_INTEGER);
        if (score == null || scoreNum == null || wordsNum == null || recommend == null) {
            return null;
        }
        scoreEt.setText(score);
        scoreNumEt.setText(scoreNum);
        wordsNumEt.setText(wordsNum);
        recommendEt.setText(recommend);
        searchInfo.setScore(score);
        searchInfo.setScoreNum(scoreNum);
        searchInfo.setWordsNum(wordsNum);
        searchInfo.setRecommend(recommend);
        return searchInfo;
    }

    private void scrollToEndAndRequestFocus(Handler handler, final ScrollView scrollView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //获取触发键盘的EditText
                EditText currentFocusEt = getFocusEt();
                //scrollview滚动到末尾
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                //之前触发键盘的EditText重新获取焦点
                if (currentFocusEt != null) {
                    currentFocusEt.setFocusable(true);
                    currentFocusEt.requestFocus();
                }
            }
        });
    }

    public void hideSoftInQidian() {
        if (isSoftInputDisplay) {
            View view = getFocusEt();
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    private Activity getActivity() {
        return activity;
    }

    /**
     * 设置键盘状态的监听
     */
    public void setSoftInputStateListener() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 取消键盘状态的监听
     */
    public void cancelSoftInputStateListener() {
        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public void setLastClickItem(int lastClickItem) {
        bookComparatorUtil.setLastClickItem(lastClickItem);
    }

    public void setBookComparatorUtil(BookComparatorUtil bookComparatorUtil) {
        this.bookComparatorUtil = bookComparatorUtil;
    }

    public BookComparatorUtil getBookComparatorUtil() {
        return bookComparatorUtil;
    }
}
