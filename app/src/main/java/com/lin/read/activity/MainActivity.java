package com.lin.read.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.fragment.DownloadHistoryFragment;
import com.lin.read.fragment.ScanFragment;
import com.lin.read.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ScanFragment scanFragment;
    private SearchFragment searchFragment;
    private DownloadHistoryFragment downloadHistoryFragment;

    private View scanView;
    private View searchView;
    private View downloadHistoryView;

    private View mainSplitView;
    private LinearLayout mainFunctionViews;

    private List<View> allFunctionViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        scanView = findViewById(R.id.rl_scan);
        searchView = findViewById(R.id.rl_search);
        downloadHistoryView = findViewById(R.id.rl_history);
        mainSplitView=findViewById(R.id.main_split_view);
        mainFunctionViews= (LinearLayout) findViewById(R.id.ll_main_functions);

        allFunctionViews=new ArrayList<>();
        allFunctionViews.add(scanView);
        allFunctionViews.add(searchView);
        allFunctionViews.add(downloadHistoryView);

        scanFragment = new ScanFragment();
        searchFragment = new SearchFragment();
        downloadHistoryFragment = new DownloadHistoryFragment();

        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        localFragmentTransaction.add(R.id.fragment_container, this.scanFragment).add(R.id.fragment_container, this.searchFragment).add(R.id.fragment_container, this.downloadHistoryFragment);
        localFragmentTransaction.show(this.scanFragment).hide(this.searchFragment).hide(this.downloadHistoryFragment);
        localFragmentTransaction.commit();

        scanView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        downloadHistoryView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.scanFragment.hideSoft();
        this.searchFragment.hideSoft();
        switch (v.getId()) {
            case R.id.rl_scan:
                localFragmentTransaction.show(this.scanFragment).hide(this.searchFragment).hide(this.downloadHistoryFragment);
                setSelectBackground(0);
                break;
            case R.id.rl_search:
                scanFragment.hideFilterLayoutWithoutAnimation();
                localFragmentTransaction.show(this.searchFragment).hide(this.scanFragment).hide(this.downloadHistoryFragment);
                setSelectBackground(1);
                break;
            case R.id.rl_history:
                scanFragment.hideFilterLayoutWithoutAnimation();
                localFragmentTransaction.show(this.downloadHistoryFragment).hide(this.searchFragment).hide(this.scanFragment);
                setSelectBackground(2);
                break;
            default:
                break;
        }
        localFragmentTransaction.commit();
    }

    public void setSelectBackground(int index){
        if(allFunctionViews==null||allFunctionViews.size()==0||index<0||index>=allFunctionViews.size()){
            return;
        }
        for(int i=0;i<allFunctionViews.size();i++){
            if(i==index){
                allFunctionViews.get(i).setBackgroundResource(R.color.main_selected);
            }else{
                allFunctionViews.get(i).setBackgroundResource(R.color.main_unselected);
            }
        }
    }

    private long lastClick = 0L;
    public void onBackPressed()
    {
        if(scanFragment.isFilterLayoutVisble()){
            scanFragment.hideFilterLayout();
            return;
        }
        long current = System.currentTimeMillis();
        if (current - this.lastClick <= 2000L)
        {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
        this.lastClick = current;
    }

    public void hideBottomViews(boolean isHide){
        if(isHide){
            mainSplitView.setVisibility(View.GONE);
            mainFunctionViews.setVisibility(View.GONE);
        }else{
            mainSplitView.setVisibility(View.VISIBLE);
            mainFunctionViews.setVisibility(View.VISIBLE);
        }
    }


}
