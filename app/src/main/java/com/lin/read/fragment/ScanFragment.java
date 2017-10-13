package com.lin.read.fragment;


import android.animation.StateListAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.ScanTypeAdapter;
import com.lin.read.ScanTypeItemDecoration;
import com.lin.read.filter.qidian.QiDianConstants;
import com.lin.read.utils.NumberInputFilter;
import com.lin.read.utils.ScoreInputFilter;

/**
 * Created by lisonglin on 2017/10/11.
 */

public class ScanFragment extends Fragment {

    private TextView scanFilterTv;
    private RelativeLayout scanSortIv;
    private TextView scanResultTv;
    private View scanFilterLayout;
    private View scanFilterBlank;

    private RecyclerView scanWebTypeRcv;
    private RecyclerView scanRankTypeRcv;
    private RecyclerView scanBookTypeRcv;

    private ScanTypeAdapter scanWebTypeAdapter;
    private ScanTypeAdapter scanRankTypeAdapter;
    private ScanTypeAdapter scanBookTypeAdapter;

    private EditText scoreEt;
    private EditText scoreNumEt;
    private EditText wordsNumEt;
    private EditText recommendEt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        scanFilterTv = (TextView) view.findViewById(R.id.scan_filter);
        scanSortIv = (RelativeLayout) view.findViewById(R.id.scan_sort);
        scanResultTv = (TextView) view.findViewById(R.id.scan_result);
        scanFilterLayout = view.findViewById(R.id.layout_scan_filter);
        scanFilterBlank = view.findViewById(R.id.scan_filter_blank);

        scanWebTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_web);
        scanRankTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_rank);
        scanBookTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_booktype);

        scoreEt= (EditText) view.findViewById(R.id.et_socre);
        scoreNumEt= (EditText) view.findViewById(R.id.et_socre_num);
        wordsNumEt= (EditText) view.findViewById(R.id.et_words_num);
        recommendEt= (EditText) view.findViewById(R.id.et_recommend);

        setAdapter();

        setInputFilter();

        scanFilterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_in);
                scanFilterLayout.startAnimation(anim);
                scanFilterLayout.setVisibility(View.VISIBLE);
            }
        });

        scanFilterBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFilterLayout();
            }
        });
    }

    private void setAdapter() {
        scanWebTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanWebTypeList);
        scanRankTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanRankTypeList);
        scanBookTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanBookTypeList);

        scanWebTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanWebTypeAdapter.setDefaultChecked("起点");
        scanWebTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(),15));
        scanWebTypeRcv.setAdapter(scanWebTypeAdapter);

        scanRankTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanRankTypeAdapter.setDefaultChecked("推荐");
        scanRankTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(),15));
        scanRankTypeRcv.setAdapter(scanRankTypeAdapter);

        scanBookTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanBookTypeAdapter.setDefaultChecked("玄幻");
        scanBookTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(),15));
        scanBookTypeRcv.setAdapter(scanBookTypeAdapter);
    }

    public boolean isFilterLayoutVisble() {
        return scanFilterLayout.getVisibility() == View.VISIBLE;
    }

    public void hideFilterLayout() {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scanFilterLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scanFilterLayout.startAnimation(anim);
    }

    public void hideFilterLayoutWithoutAnimation() {
        scanFilterLayout.setVisibility(View.GONE);
    }

    private void setInputFilter(){
        scoreEt.setFilters(new InputFilter[]{new ScoreInputFilter()});
        scoreNumEt.setFilters(new InputFilter[]{new NumberInputFilter(6)});
        wordsNumEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
        recommendEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
    }
}
