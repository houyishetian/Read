package com.lin.read.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.activity.util.QiDianScanUtil;
import com.lin.read.activity.util.ZongHengScanUtil;
import com.lin.read.adapter.ScanBookItemAdapter;
import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.decoration.ScanTypeItemDecoration;
import com.lin.read.activity.MainActivity;
import com.lin.read.filter.BookComparatorUtil;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.scan.qidian.QiDianConstants;
import com.lin.read.utils.Constants;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.filter.BookComparator;

import java.util.ArrayList;
import java.util.Collections;

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
    private ScanTypeAdapter scanWebTypeAdapter;

    private RecyclerView allBooksRcv;
    private ScanBookItemAdapter allBookAdapter;
    private ArrayList<BookInfo> allBookData;
    private TextView emptyTv;

    private View layoutScanQiDian;
    private View layoutScanZongHeng;
    private View layoutScan17k;
    private View[] allScanLayouts;
    private int currentWebPosition;
    private final int LAYOUT_INDEX_QIDIAN=0;
    private final int LAYOUT_INDEX_ZONGHENG=1;
    private final int LAYOUT_INDEX_17K=2;

    private Handler handler;

    private QiDianScanUtil qiDianScanUtil;
    private ZongHengScanUtil zongHengScanUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        handler=new Handler();
        qiDianScanUtil=new QiDianScanUtil();
        qiDianScanUtil.initQiDianViews(this,view,handler);

        zongHengScanUtil = new ZongHengScanUtil();
        zongHengScanUtil.initQiDianViews(this,view);

        scanFilterTv = (TextView) view.findViewById(R.id.scan_filter);
        scanSortIv = (RelativeLayout) view.findViewById(R.id.scan_sort);
        scanResultTv = (TextView) view.findViewById(R.id.scan_result);
        scanFilterLayout = view.findViewById(R.id.layout_scan_filter);
        scanFilterBlank = view.findViewById(R.id.scan_filter_blank);

        currentWebPosition=LAYOUT_INDEX_QIDIAN;

        scanWebTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_web);

        allBooksRcv= (RecyclerView) view.findViewById(R.id.rcv_scan_all_books);

        emptyTv = (TextView) view.findViewById(R.id.empty_view);

        layoutScan17k=view.findViewById(R.id.layout_scan_17k);
        layoutScanZongHeng=view.findViewById(R.id.layout_scan_zongheng);
        layoutScanQiDian=view.findViewById(R.id.layout_scan_qidian);
        allScanLayouts=new View[]{layoutScanQiDian,layoutScanZongHeng,layoutScan17k};

        setAdapter();

        scanFilterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_in);
                scanFilterLayout.startAnimation(anim);
                scanFilterLayout.setVisibility(View.VISIBLE);
                if(currentWebPosition==LAYOUT_INDEX_QIDIAN){
                    qiDianScanUtil.setSoftInputStateListener();
                }
            }
        });

        scanFilterBlank.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideFilterLayout();
            }
        });

        scanSortIv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(allBookData.size()!=0){
                    if(currentWebPosition==LAYOUT_INDEX_QIDIAN){
                        qiDianScanUtil.showSortDialog(allBookData);
                    }
                }
            }
        });
    }

    private void setAdapter() {
        scanWebTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanWebTypeList);
        allBookData=new ArrayList<>();
        allBookAdapter=new ScanBookItemAdapter(getActivity(),allBookData);

        scanWebTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanWebTypeAdapter.setDefaultChecked(QiDianConstants.WEB_QIDIAN);
        scanWebTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(), 15));
        scanWebTypeRcv.setAdapter(scanWebTypeAdapter);

        allBooksRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        allBooksRcv.addItemDecoration(new ScanBooksItemDecoration(getActivity()));
        allBooksRcv.setAdapter(allBookAdapter);

        scanWebTypeAdapter.setOnScanItemClickListener(new ScanTypeAdapter.OnScanItemClickListener() {
            @Override
            public void onItemClick(int position,String clickText) {
                Log.e("Test", "current position:" + position);
                if(!StringUtils.isEmpty(clickText)){
                    showScanLayout(position);
                    currentWebPosition = position;
                    if (position == LAYOUT_INDEX_QIDIAN) {
                        String rankType=qiDianScanUtil.getRankType();
                        if (rankType.equals(QiDianConstants.QD_RANK_RECOMMEND) || rankType.equals(QiDianConstants.QD_RANK_FINAL)) {
                            qiDianScanUtil.hideDateLayout(false);
                        }else{
                            qiDianScanUtil.hideDateLayout(true);
                        }
                    }
                }
            }
        });

        allBookAdapter.setOnBookItemClickListener(new ScanBookItemAdapter.OnBookItemClickListener() {
            @Override
            public void onBookItemClick(BookInfo bookInfo) {
                ((MainActivity)getActivity()).clickScanBookItem(bookInfo);
            }
        });
    }


    public boolean isFilterLayoutVisble() {
        return scanFilterLayout.getVisibility() == View.VISIBLE;
    }

    public void hideSoft(){
        if(currentWebPosition == LAYOUT_INDEX_QIDIAN){
            qiDianScanUtil.hideSoftInQidian();
        }
    }

    public void hideFilterLayout() {
        hideSoft();
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scanFilterLayout.setVisibility(View.GONE);
                if(currentWebPosition==LAYOUT_INDEX_QIDIAN){
                    qiDianScanUtil.cancelSoftInputStateListener();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scanFilterLayout.startAnimation(anim);
    }

    public void hideFilterLayoutWithoutAnimation() {
        if (currentWebPosition == LAYOUT_INDEX_QIDIAN) {
            qiDianScanUtil.hideSoftInQidian();
            qiDianScanUtil.cancelSoftInputStateListener();
        }
        scanFilterLayout.setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SCAN_REQUEST_CODE) {
            switch (resultCode) {
                case Constants.SCAN_RESPONSE_FAILED:
                    scanResultTv.setVisibility(View.VISIBLE);
                    scanResultTv.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_RESULT, 0));
                    allBookData.clear();
                    allBookAdapter.notifyDataSetChanged();
                    emptyTv.setVisibility(View.VISIBLE);
                    allBooksRcv.setVisibility(View.GONE);
                    if(currentWebPosition==LAYOUT_INDEX_QIDIAN){
                        qiDianScanUtil.setBookComparatorUtil(new BookComparatorUtil());
                        qiDianScanUtil.setLastClickItem(-1);
                    }
                    Toast.makeText(getActivity(),"扫描失败，请检查网络!",Toast.LENGTH_SHORT).show();
                    break;
                case Constants.SCAN_RESPONSE_SUCC:
                    if (data != null) {
                        emptyTv.setVisibility(View.GONE);
                        allBooksRcv.setVisibility(View.VISIBLE);
                        ArrayList<BookInfo> allBookDataFromScan = data.getBundleExtra(Constants.KEY_INTENT_FOR_BOOK_DATA).getParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA);
                        scanResultTv.setVisibility(View.VISIBLE);
                        Log.e("Test", "接收:" + allBookDataFromScan);
                        scanResultTv.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_RESULT, allBookDataFromScan.size()));
                        allBookData.clear();
                        allBookData.addAll(allBookDataFromScan);
                        Collections.sort(allBookData,new BookComparator(BookComparator.SortType.ASCEND, BookComparator.BookType.POSTION));
                        allBookAdapter.notifyDataSetChanged();
                        allBooksRcv.smoothScrollToPosition(0);
                        if(currentWebPosition==LAYOUT_INDEX_QIDIAN){
                            qiDianScanUtil.setBookComparatorUtil(new BookComparatorUtil());
                            qiDianScanUtil.setLastClickItem(BookComparatorUtil.SORT_BY_DEFAULT);
                        }
                        Toast.makeText(getActivity(),"扫描结束!",Toast.LENGTH_SHORT).show();
                    }else{
                        emptyTv.setVisibility(View.VISIBLE);
                        allBooksRcv.setVisibility(View.GONE);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showScanLayout(int layoutIndex) {
        if (layoutIndex < 0 || allScanLayouts == null || layoutIndex >= allScanLayouts.length) {
            return;
        }
        for (int i = 0; i < allScanLayouts.length; i++) {
            if (i == layoutIndex) {
                allScanLayouts[i].setVisibility(View.VISIBLE);
            } else {
                allScanLayouts[i].setVisibility(View.GONE);
            }
        }
    }

    public void refreshBookData(){
        allBookAdapter.notifyDataSetChanged();
        allBooksRcv.smoothScrollToPosition(0);
    }
}
