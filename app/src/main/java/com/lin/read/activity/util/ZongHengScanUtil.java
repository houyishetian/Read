package com.lin.read.activity.util;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.lin.read.R;
import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.decoration.ScanTypeItemDecoration;
import com.lin.read.filter.scan.ScanTypeInfo;
import com.lin.read.filter.scan.qidian.QiDianConstants;
import com.lin.read.filter.scan.zongheng.ZongHengConstants;
import com.lin.read.fragment.ScanFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/4/13.
 */
public class ZongHengScanUtil {
    private RecyclerView rcvRankType;
    private List<ScanTypeInfo> rankTypeList;
    private ScanTypeAdapter rankTypeAdapter;

    private RecyclerView rcvBookType;
    private List<ScanTypeInfo> bookTypeList;
    private ScanTypeAdapter bookTypeAdapter;
    private View bookTypeView;

    private RecyclerView rcvDateType;
    private List<ScanTypeInfo> dateList;
    private ScanTypeAdapter dateAdapter;
    private View dateTypeView;

    private EditText etPingLun;
    private EditText etPengChang;
    private EditText etWordsNum;
    private EditText etRecommend;

    public void initQiDianViews(final ScanFragment scanFragment, View view) {
        Activity activity = scanFragment.getActivity();
        rcvRankType = (RecyclerView) view.findViewById(R.id.rcv_zh_scan_rank);
        rcvBookType = (RecyclerView) view.findViewById(R.id.rcv_zh_scan_booktype);
        rcvDateType = (RecyclerView) view.findViewById(R.id.rcv_zh_scan_date);
        bookTypeView=view.findViewById(R.id.ll_zh_scan_booktype);
        dateTypeView = view.findViewById(R.id.ll_zh_scan_date);
        etPingLun= (EditText) view.findViewById(R.id.zh_et_pinglun);
        etPengChang= (EditText) view.findViewById(R.id.zh_et_pengchang);
        etWordsNum= (EditText) view.findViewById(R.id.zh_et_words_num);
        etRecommend= (EditText) view.findViewById(R.id.zh_et_recommend);

        rankTypeList=new ArrayList<>();
        bookTypeList=new ArrayList<>();
        dateList=new ArrayList<>();

        rankTypeList.addAll(ZongHengConstants.scanRankTypeList);
        bookTypeList.addAll(ZongHengConstants.scanYuePiaoSubList);

        rankTypeAdapter = new ScanTypeAdapter(activity, rankTypeList);
        bookTypeAdapter = new ScanTypeAdapter(activity, bookTypeList);
        dateAdapter = new ScanTypeAdapter(activity, dateList);

        setAdapter(rcvRankType, rankTypeAdapter, activity, ZongHengConstants.ZH_RANK_YUE_PIAO);
        setAdapter(rcvBookType, bookTypeAdapter, activity, ZongHengConstants.ZH_YUE_PIAO_BAIDU);
        setAdapter(rcvDateType, dateAdapter, activity, ZongHengConstants.DATE_WEEK);

        setAdapterItemClick();
    }

    private void setAdapter(RecyclerView rcv, ScanTypeAdapter adapter, Activity activity, String defaule) {
        rcv.setLayoutManager(new GridLayoutManager(activity, 4));
        adapter.setDefaultChecked(QiDianConstants.QD_RANK_RECOMMEND);
        rcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        adapter.setDefaultChecked(defaule);
        rcv.setAdapter(adapter);
    }

    private void setAdapterItemClick(){
        rankTypeAdapter.setOnScanItemClickListener(new ScanTypeAdapter.OnScanItemClickListener() {
            @Override
            public void onItemClick(int position, String clickText) {
                switch (clickText){
                    case ZongHengConstants.ZH_RANK_YUE_PIAO:
                        bookTypeView.setVisibility(View.VISIBLE);
                        dateTypeView.setVisibility(View.GONE);
                        bookTypeList.clear();
                        bookTypeList.addAll(ZongHengConstants.scanYuePiaoSubList);
                        bookTypeAdapter.setDefaultChecked(ZongHengConstants.ZH_YUE_PIAO_BAIDU);
                        bookTypeAdapter.notifyDataSetChanged();
                        break;
                    case ZongHengConstants.ZH_RANK_CLICK:
                        bookTypeView.setVisibility(View.VISIBLE);
                        dateTypeView.setVisibility(View.VISIBLE);
                        bookTypeList.clear();
                        bookTypeList.addAll(ZongHengConstants.scanBookTypeList);
                        bookTypeAdapter.setDefaultChecked(ZongHengConstants.ZH_BOOK_ALL);
                        bookTypeAdapter.notifyDataSetChanged();
                        dateList.clear();
                        dateList.addAll(ZongHengConstants.scanClickDateTypeList);
                        dateAdapter.setDefaultChecked(ZongHengConstants.DATE_WEEK);
                        dateAdapter.notifyDataSetChanged();
                        break;
                    case ZongHengConstants.ZH_RANK_NEW_BOOK:
                        bookTypeView.setVisibility(View.VISIBLE);
                        dateTypeView.setVisibility(View.GONE);
                        bookTypeList.clear();
                        bookTypeList.addAll(ZongHengConstants.scanBookTypeList);
                        bookTypeAdapter.setDefaultChecked(ZongHengConstants.ZH_BOOK_ALL);
                        bookTypeAdapter.notifyDataSetChanged();
                        break;
                    case ZongHengConstants.ZH_RANK_HONG_PIAO:
                        bookTypeView.setVisibility(View.VISIBLE);
                        dateTypeView.setVisibility(View.VISIBLE);
                        bookTypeList.clear();
                        bookTypeList.addAll(ZongHengConstants.scanBookTypeList);
                        bookTypeAdapter.setDefaultChecked(ZongHengConstants.ZH_BOOK_ALL);
                        bookTypeAdapter.notifyDataSetChanged();
                        dateList.clear();
                        dateList.addAll(ZongHengConstants.scanHongPiaoDateTypeList);
                        dateAdapter.setDefaultChecked(ZongHengConstants.DATE_WEEK);
                        dateAdapter.notifyDataSetChanged();
                        break;
                    case ZongHengConstants.ZH_RANK_HEI_PIAO:
                        bookTypeView.setVisibility(View.VISIBLE);
                        dateTypeView.setVisibility(View.VISIBLE);
                        bookTypeList.clear();
                        bookTypeList.addAll(ZongHengConstants.scanBookTypeList);
                        bookTypeAdapter.setDefaultChecked(ZongHengConstants.ZH_BOOK_ALL);
                        bookTypeAdapter.notifyDataSetChanged();
                        dateList.clear();
                        dateList.addAll(ZongHengConstants.scanHeiPiaoDateTypeList);
                        dateAdapter.setDefaultChecked(ZongHengConstants.DATE_WEEK);
                        dateAdapter.notifyDataSetChanged();
                        break;
                    case ZongHengConstants.ZH_RANK_RE_MEN:
                        bookTypeView.setVisibility(View.GONE);
                        dateTypeView.setVisibility(View.VISIBLE);
                        dateList.clear();
                        dateList.addAll(ZongHengConstants.scanReMenDateTypeList);
                        dateAdapter.setDefaultChecked(ZongHengConstants.DATE_WEEK);
                        dateAdapter.notifyDataSetChanged();
                        break;
                    default:
                        bookTypeView.setVisibility(View.GONE);
                        dateTypeView.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }
}
