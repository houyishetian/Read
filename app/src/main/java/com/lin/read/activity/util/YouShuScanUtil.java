package com.lin.read.activity.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lin.read.activity.LoadingDialogActivity;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.fragment.ScanFragment;
import com.lin.read.utils.Constants;
import com.lin.read.utils.MessageUtils;
import com.lin.read.view.ScanTypeRecyclerViewUtil;

public class YouShuScanUtil {
    private TextView scanYsPrePageTv;
    private TextView scanYsNextPageTv;
    private EditText scanYsSkipPageEt;
    private TextView scanYsTotalPageTv;
    private TextView scanSkipTv;

    private Activity activity;
    private ScanFragment scanFragment;
    private int totalPage;
    private int currentPage = 1;

    private ScanTypeRecyclerViewUtil.ScanTypeView cateView;
    private ScanTypeRecyclerViewUtil.ScanTypeView wordsView;
    private ScanTypeRecyclerViewUtil.ScanTypeView statusView;
    private ScanTypeRecyclerViewUtil.ScanTypeView updateView;
    private ScanTypeRecyclerViewUtil.ScanTypeView sortView;


    public void initYouShuViews(final ScanFragment scanFragment, View view, final Handler handler) {
//        activity = scanFragment.getActivity();
//        this.scanFragment = scanFragment;
//
//        cateView = scanFragment.scanTypeRecyclerViewUtil.getScanTypeViews().get(Constants.WEB_YOU_SHU).get(YouShuConstants.YS_FILTER_CATE);
//        wordsView = scanFragment.scanTypeRecyclerViewUtil.getScanTypeViews().get(Constants.WEB_YOU_SHU).get(YouShuConstants.YS_FILTER_WORDS);
//        statusView = scanFragment.scanTypeRecyclerViewUtil.getScanTypeViews().get(Constants.WEB_YOU_SHU).get(YouShuConstants.YS_FILTER_STATUS);
//        updateView = scanFragment.scanTypeRecyclerViewUtil.getScanTypeViews().get(Constants.WEB_YOU_SHU).get(YouShuConstants.YS_FILTER_UPDATE);
//        sortView = scanFragment.scanTypeRecyclerViewUtil.getScanTypeViews().get(Constants.WEB_YOU_SHU).get(YouShuConstants.YS_FILTER_SORT);
//
//        scanYsPrePageTv = (TextView) view.findViewById(R.id.scan_ys_previous_page);
//        scanYsNextPageTv = (TextView) view.findViewById(R.id.scan_ys_next_page);
//        scanYsSkipPageEt = (EditText) view.findViewById(R.id.scan_ys_skip_page);
//        scanYsTotalPageTv = (TextView) view.findViewById(R.id.scan_ys_total_page);
//        scanSkipTv = (TextView) view.findViewById(R.id.scan_ys_skip);
//
//        initListener();
    }

    public SearchInfo getSearchInfo(){
        SearchInfo searchInfo = new SearchInfo();
//        searchInfo.setCurrentPage(currentPage);
//        searchInfo.setWebType(Constants.WEB_YOU_SHU);
//        searchInfo.setCategoryInfo(cateView.adapter.getCheckedInfo());
//        searchInfo.setWordsNumInfo(wordsView.adapter.getCheckedInfo());
//        searchInfo.setBookStatusInfo(statusView.adapter.getCheckedInfo());
//        searchInfo.setUpdateDateInfo(updateView.adapter.getCheckedInfo());
//        searchInfo.setSortTypeInfo(sortView.adapter.getCheckedInfo());
        return searchInfo;
    }

    public void afterGetBookInfo(Intent data) {
        totalPage = data.getIntExtra(MessageUtils.TOTAL_PAGE, 0);
        currentPage = data.getIntExtra(MessageUtils.CURRENT_PAGE, 0);
        scanYsTotalPageTv.setText("" + totalPage);
        scanYsSkipPageEt.setText("");
        scanYsSkipPageEt.setHint("" + currentPage);
        if (currentPage > 1) {
            scanYsPrePageTv.setTextColor(activity.getResources().getColor(android.R.color.black));
        } else {
            scanYsPrePageTv.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
        }
        if (currentPage < totalPage) {
            scanYsNextPageTv.setTextColor(activity.getResources().getColor(android.R.color.black));
        } else {
            scanYsNextPageTv.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void initListener(){
        scanYsSkipPageEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        int page = Integer.parseInt(scanYsSkipPageEt.getText().toString());
                        Log.d("skip to:",""+currentPage);
                        if (page <= 0 || page > totalPage) {
                            Toast.makeText(activity, "输入错误", Toast.LENGTH_SHORT).show();
                            scanYsSkipPageEt.setText(currentPage + "");
                            return false;
                        }
                        startScan(page);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        scanSkipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int page = Integer.parseInt(scanYsSkipPageEt.getText().toString());
                    Log.d("skip to:",""+currentPage);
                    if (page <= 0 || page > totalPage) {
                        Toast.makeText(activity, "输入错误", Toast.LENGTH_SHORT).show();
                        scanYsSkipPageEt.setText(currentPage + "");
                        return;
                    }
                    startScan(page);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        scanYsPrePageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 1) {
                    Toast.makeText(activity, "已经是第一页！", Toast.LENGTH_SHORT).show();
                    return;
                }
                startScan(currentPage-1);
            }
        });
        scanYsNextPageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == totalPage) {
                    Toast.makeText(activity, "已经是最后一页！", Toast.LENGTH_SHORT).show();
                    return;
                }
                startScan(currentPage+1);
            }
        });
    }

    private void startScan(int page){
        hideSoft();
        try {
            currentPage = page;
            SearchInfo searchInfo = getSearchInfo();
            if (searchInfo != null) {
                Log.e("Test", searchInfo.toString());
                Intent intent = new Intent(activity, LoadingDialogActivity.class);
                intent.putExtra(Constants.KEY_SEARCH_INFO, searchInfo);
                scanFragment.startActivityForResult(intent, Constants.SCAN_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideSoft() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(scanYsSkipPageEt.getWindowToken(), 0);
    }
}
