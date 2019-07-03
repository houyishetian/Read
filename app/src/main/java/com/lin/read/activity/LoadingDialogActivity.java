package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.ReadGetBookInfoFactory;
import com.lin.read.filter.scan.ScanInfo;
import com.lin.read.utils.Constants;
import com.lin.read.utils.MessageUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2017/10/14.
 */

public class LoadingDialogActivity extends Activity {
    private Handler handler;
    private TextView tvProgress;
    private TextView tvScanBookState;
    private TextView tvScanBookResultState;

    private int totalNum=0;
    private int alreadyFind;
    private int alreadyFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_dialog);
        alreadyFind = 0;
        alreadyFinish = 0;

        initView();

        initHandler();
    }

    private void initView(){
        tvProgress= (TextView) findViewById(R.id.loading_progress);
        tvScanBookState= (TextView) findViewById(R.id.loading_scan_book);
        tvScanBookResultState= (TextView) findViewById(R.id.loading_scan_book_result);
        tvProgress.setVisibility(View.INVISIBLE);
        tvScanBookState.setVisibility(View.INVISIBLE);
        tvScanBookResultState.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(){
            @Override
            public void run() {
                super.run();
                scanBook();
            }
        }.start();
    }

    private void initHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MessageUtils.SCAN_START:
                        tvScanBookState.setVisibility(View.VISIBLE);
                        tvScanBookState.setText(Constants.TEXT_SCAN_START);
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_END:
                        totalNum=msg.arg1;
                        tvScanBookState.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_END,msg.arg1));
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START:
                        tvProgress.setVisibility(View.VISIBLE);
                        tvProgress.setText("0%");
                        tvScanBookResultState.setVisibility(View.VISIBLE);
                        tvScanBookResultState.setText(Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_START);
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE:
                        alreadyFinish ++;
                        tvProgress.setText((alreadyFinish*100/totalNum)+"%");
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE:
                        alreadyFind++;
                        tvScanBookResultState.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_GET_ONE, alreadyFind));
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_END:
                        ArrayList<BookInfo> result=msg.getData().getParcelableArrayList(MessageUtils.BOOK_LIST);
                        tvScanBookResultState.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_END,result.size()));
                        break;
                    case MessageUtils.SCAN_YOUSHU_END:
                         break;
                }
            }
        };
    }

    private void scanBook(){
        final ScanInfo searchInfo= (ScanInfo) getIntent().getSerializableExtra(Constants.KEY_SEARCH_INFO);
        ReadGetBookInfoFactory.Companion.getInstance(searchInfo.getWebName()).getBookInfo(this, handler, searchInfo, new ReadGetBookInfoFactory.OnScanResult() {
            @Override
            public void onSucceed(int totalNum, @NotNull List<? extends BookInfo> bookInfoList) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                setResult(Constants.SCAN_RESPONSE_SUCC, intent);
                ArrayList<BookInfo> result = new ArrayList<>();
                result.addAll(bookInfoList);
                if (Constants.WEB_YOU_SHU.equals(searchInfo.getWebName())) {
                    intent.putExtra(MessageUtils.TOTAL_PAGE, totalNum);
                    intent.putExtra(MessageUtils.CURRENT_PAGE, searchInfo.getPage());
                }
                bundle.putParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA, result);
                intent.putExtra(Constants.KEY_INTENT_FOR_BOOK_DATA, bundle);
                finish();
            }

            @Override
            public void onFailed(@org.jetbrains.annotations.Nullable Throwable e) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                setResult(Constants.SCAN_RESPONSE_FAILED, intent);
                intent.putExtra(Constants.KEY_INTENT_FOR_BOOK_DATA, bundle);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
