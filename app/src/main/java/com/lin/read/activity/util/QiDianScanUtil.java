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

    private Activity activity;

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
                    if (clickText.equals(QiDianConstants.QD_RANK_RECOMMEND)) {
                        scanDateLinearLayout.setVisibility(View.VISIBLE);
                        scanDateTypeAdapter.setDefaultChecked(QiDianConstants.QD_DATE_WEEK);
                        scanDateTypeAdapter.notifyDataSetChanged();
                    } else {
                        scanDateLinearLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
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
}
