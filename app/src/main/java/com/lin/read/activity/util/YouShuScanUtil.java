package com.lin.read.activity.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.lin.read.R;
import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.decoration.ScanTypeItemDecoration;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.youshu.YouShuConstants;
import com.lin.read.fragment.ScanFragment;
import com.lin.read.utils.Constants;
import com.lin.read.utils.MessageUtils;

public class YouShuScanUtil {
    private RecyclerView scanCategoryRcv;
    private RecyclerView scanWordsNumRcv;
    private RecyclerView scanBookStateRcv;
    private RecyclerView scanUpdateDateRcv;
    private RecyclerView scanSortTypeRcv;

    private ScanTypeAdapter scanCategoryAdapter;
    private ScanTypeAdapter scanWordsNumAdapter;
    private ScanTypeAdapter scanBookStateAdapter;
    private ScanTypeAdapter scanUpdateDateAdapter;
    private ScanTypeAdapter scanSortTypeAdapter;


    private TextView scanYsPrePageTv;
    private TextView scanYsNextPageTv;
    private EditText scanYsSkipPageEt;
    private TextView scanYsTotalPageTv;

    private Activity activity;

    public void initYouShuViews(final ScanFragment scanFragment, View view, final Handler handler) {
        activity = scanFragment.getActivity();
        scanCategoryRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_category);
        scanWordsNumRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_wordsNum);
        scanBookStateRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_book_status);
        scanUpdateDateRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_update_date);
        scanSortTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_sort_type);

        scanCategoryAdapter = new ScanTypeAdapter(activity, YouShuConstants.categoryList,R.layout.item_scan_type_4_chars);
        scanWordsNumAdapter = new ScanTypeAdapter(activity, YouShuConstants.wordsNumList,R.layout.item_scan_type_4_chars);
        scanBookStateAdapter = new ScanTypeAdapter(activity, YouShuConstants.bookStatusList);
        scanUpdateDateAdapter = new ScanTypeAdapter(activity, YouShuConstants.updateDateList);
        scanSortTypeAdapter = new ScanTypeAdapter(activity, YouShuConstants.sortTypeList);

        scanYsPrePageTv = (TextView) view.findViewById(R.id.scan_ys_previous_page);
        scanYsNextPageTv = (TextView) view.findViewById(R.id.scan_ys_next_page);
        scanYsSkipPageEt = (EditText) view.findViewById(R.id.scan_ys_skip_page);
        scanYsTotalPageTv = (TextView) view.findViewById(R.id.scan_ys_total_page);

        setAdapter(activity);
    }

    private void setAdapter(Activity activity) {
        scanCategoryRcv.setLayoutManager(new GridLayoutManager(activity, 3));
        scanCategoryAdapter.setDefaultChecked(YouShuConstants.YS_CATE_FANTASY);
        scanCategoryRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanCategoryRcv.setAdapter(scanCategoryAdapter);

        scanWordsNumRcv.setLayoutManager(new GridLayoutManager(activity, 3));
        scanWordsNumAdapter.setDefaultChecked(YouShuConstants.YS_WORDS_6);
        scanWordsNumRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanWordsNumRcv.setAdapter(scanWordsNumAdapter);

        scanBookStateRcv.setLayoutManager(new GridLayoutManager(activity, 4));
        scanBookStateAdapter.setDefaultChecked(YouShuConstants.YS_TYPE_UNLIMIT);
        scanBookStateRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanBookStateRcv.setAdapter(scanBookStateAdapter);

        scanUpdateDateRcv.setLayoutManager(new GridLayoutManager(activity, 4));
        scanUpdateDateAdapter.setDefaultChecked(YouShuConstants.YS_TYPE_UNLIMIT);
        scanUpdateDateRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanUpdateDateRcv.setAdapter(scanUpdateDateAdapter);

        scanSortTypeRcv.setLayoutManager(new GridLayoutManager(activity, 4));
        scanSortTypeAdapter.setDefaultChecked(YouShuConstants.YS_SORT_RATE);
        scanSortTypeRcv.addItemDecoration(new ScanTypeItemDecoration(activity, 15));
        scanSortTypeRcv.setAdapter(scanSortTypeAdapter);
    }

    public SearchInfo getSearchInfo(){
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setCurrentPage(1);
        searchInfo.setWebType(Constants.WEB_YOU_SHU);
        searchInfo.setCategoryInfo(scanCategoryAdapter.getCheckedInfo());
        searchInfo.setWordsNumInfo(scanWordsNumAdapter.getCheckedInfo());
        searchInfo.setBookStatusInfo(scanBookStateAdapter.getCheckedInfo());
        searchInfo.setUpdateDateInfo(scanUpdateDateAdapter.getCheckedInfo());
        searchInfo.setSortTypeInfo(scanSortTypeAdapter.getCheckedInfo());
        return searchInfo;
    }

    public void afterGetBookInfo(Intent data){
        int totalPage = data.getIntExtra(MessageUtils.TOTAL_PAGE,0);
        scanYsTotalPageTv.setText(""+totalPage);
        scanYsSkipPageEt.setText("1");
    }
}
