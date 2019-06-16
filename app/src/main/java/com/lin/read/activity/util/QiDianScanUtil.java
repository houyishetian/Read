package com.lin.read.activity.util;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.scan.qidian.QiDianConstants;
import com.lin.read.fragment.ScanFragment;
import com.lin.read.utils.Constants;
import com.lin.read.view.ScanTypeRecyclerViewUtil;

/**
 * Created by lisonglin on 2018/4/5.
 */

public class QiDianScanUtil {
//    private EditText scoreEt;
//    private EditText scoreNumEt;
//    private EditText wordsNumEt;
//    private EditText recommendEt;

    private Activity activity;

    private ScanTypeRecyclerViewUtil.ScanTypeView rankView;
    private ScanTypeRecyclerViewUtil.ScanTypeView dateView;
    private ScanTypeRecyclerViewUtil.ScanTypeView typeView;

    public void initQiDianViews(final ScanFragment scanFragment, View view, final Handler handler) {
        activity = scanFragment.getActivity();

        rankView = scanFragment.scanTypeRecyclerViewUtil.qiDianScanTypeViews.get(QiDianConstants.QD_FILTER_RANK);
        dateView = scanFragment.scanTypeRecyclerViewUtil.qiDianScanTypeViews.get(QiDianConstants.QD_FILTER_DATA);
        typeView = scanFragment.scanTypeRecyclerViewUtil.qiDianScanTypeViews.get(QiDianConstants.QD_FILTER_TYPE);

//        scoreEt = (EditText) view.findViewById(R.id.et_socre);
//        scoreNumEt = (EditText) view.findViewById(R.id.et_socre_num);
//        wordsNumEt = (EditText) view.findViewById(R.id.et_words_num);
//        recommendEt = (EditText) view.findViewById(R.id.et_recommend);

        setInputFilter();

        rankView.adapter.setOnScanItemClickListener(new ScanTypeAdapter.OnScanItemClickListener() {
            @Override
            public void onItemClick(int position, String clickText) {
                Log.e("Test", "current position:" + position);
                if (!StringUtils.isEmpty(clickText)) {
                    if (clickText.equals(QiDianConstants.QD_RANK_RECOMMEND)) {
                        dateView.parent.setVisibility(View.VISIBLE);
                        dateView.adapter.notifyDataSetChanged();
                    } else {
                        dateView.parent.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public String getRankType() {
        return rankView.adapter.getCheckedText();
    }

    public void hideDateLayout(boolean hide) {
        if (hide) {
            dateView.parent.setVisibility(View.GONE);
        } else {
            dateView.parent.setVisibility(View.VISIBLE);
            dateView.adapter.notifyDataSetChanged();
        }
    }

    private void setInputFilter() {
//        scoreEt.setFilters(new InputFilter[]{new ScoreInputFilter()});
//        scoreNumEt.setFilters(new InputFilter[]{new NumberInputFilter(6)});
//        wordsNumEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
//        recommendEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
    }

    /**
     * 获取触发键盘时获取焦点的view
     *
     * @return
     */
    public EditText getFocusEt() {
//        if (scoreEt.hasFocus()) {
//            return scoreEt;
//        }
//        if (scoreNumEt.hasFocus()) {
//            return scoreNumEt;
//        }
//        if (wordsNumEt.hasFocus()) {
//            return wordsNumEt;
//        }
//        if (recommendEt.hasFocus()) {
//            return recommendEt;
//        }
        return null;
    }

    public SearchInfo getSearchInfo() {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setWebType(Constants.WEB_QIDIAN);
        searchInfo.setRankType(rankView.adapter.getCheckedInfo().getId());
        searchInfo.setBookType(typeView.adapter.getCheckedInfo().getId());
        if (dateView.parent.getVisibility() == View.VISIBLE) {
            searchInfo.setDateType(dateView.adapter.getCheckedInfo().getId());
        }

//        String score = StringUtils.setQiDianDefaultValue(scoreEt.getText().toString(), "8.0", StringUtils.INPUTTYPE_FLOAT);
//        String scoreNum = StringUtils.setQiDianDefaultValue(scoreNumEt.getText().toString(), "300", StringUtils.INPUTTYPE_INTEGER);
//        String wordsNum = StringUtils.setQiDianDefaultValue(wordsNumEt.getText().toString(), "200", StringUtils.INPUTTYPE_INTEGER);
//        String recommend = StringUtils.setQiDianDefaultValue(recommendEt.getText().toString(), "50", StringUtils.INPUTTYPE_INTEGER);

        String score = "8.0";
        String scoreNum = "300";
        String wordsNum = "200";
        String recommend = "50";

        if (score == null || scoreNum == null || wordsNum == null || recommend == null) {
            return null;
        }
//        scoreEt.setText(score);
//        scoreNumEt.setText(scoreNum);
//        wordsNumEt.setText(wordsNum);
//        recommendEt.setText(recommend);
        searchInfo.setScore(score);
        searchInfo.setScoreNum(scoreNum);
        searchInfo.setWordsNum(wordsNum);
        searchInfo.setRecommend(recommend);
        return searchInfo;
    }
}
