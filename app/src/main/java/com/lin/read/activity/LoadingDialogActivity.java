package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.ReadGetBookInfoFactory;
import com.lin.read.filter.search.ReadGetQiDianBookInfoFactory;
import com.lin.read.filter.search.SearchInfo;
import com.lin.read.utils.Constants;
import com.lin.read.utils.MessageUtils;

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

    private Handler handlerFromScan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_dialog);

        initView();

        initHandler();

        scanBook();
    }

    private void initView(){
        tvProgress= (TextView) findViewById(R.id.loading_progress);
        tvScanBookState= (TextView) findViewById(R.id.loading_scan_book);
        tvScanBookResultState= (TextView) findViewById(R.id.loading_scan_book_result);
        tvProgress.setVisibility(View.INVISIBLE);
        tvScanBookState.setVisibility(View.INVISIBLE);
        tvScanBookResultState.setVisibility(View.INVISIBLE);
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
                        tvProgress.setText((msg.arg1*100/totalNum)+"%");
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE:
                        tvScanBookResultState.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_GET_ONE,msg.arg1));
                        break;
                    case MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_END:
                        ArrayList<BookInfo> result=msg.getData().getParcelableArrayList(MessageUtils.DATA_QIDIAN_BOOK_LIST);
                        tvScanBookResultState.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_END,result.size()));
                        break;
                }
            }
        };
    }

    private void scanBook(){
        SearchInfo searchInfo= (SearchInfo) getIntent().getSerializableExtra(Constants.KEY_SEARCH_INFO);
        new ReadGetQiDianBookInfoFactory().getBookInfo(handler,searchInfo, new ReadGetBookInfoFactory.OnGetBookInfoListener() {
            @Override
            public void succ(Object allBookInfo) {
                if(allBookInfo!=null){
                    Log.e("Test","共："+((List) allBookInfo).size());
                    ArrayList<BookInfo> allBooks= (ArrayList<BookInfo>) allBookInfo;
                    Intent intent=new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA,allBooks);
                    Log.e("Test","传递："+ allBooks.toString());
                    intent.putExtra(Constants.KEY_INTENT_FOR_BOOK_DATA,bundle);
                    setResult(Constants.SCAN_RESPONSE_SUCC,intent);
                    finish();
                }
            }

            @Override
            public void failed(int code) {
                setResult(Constants.SCAN_RESPONSE_FAILED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
