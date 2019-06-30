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
import com.lin.read.R;
import com.lin.read.activity.LoadingDialogActivity;
import com.lin.read.filter.scan.ScanInfo;
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

    private OnStartScanYouShuListener onStartScanYouShuListener;


    public void initYouShuViews(final ScanFragment scanFragment, View view, final Handler handler) {
        activity = scanFragment.getActivity();
        this.scanFragment = scanFragment;

        scanYsPrePageTv = (TextView) view.findViewById(R.id.scan_ys_previous_page);
        scanYsNextPageTv = (TextView) view.findViewById(R.id.scan_ys_next_page);
        scanYsSkipPageEt = (EditText) view.findViewById(R.id.scan_ys_skip_page);
        scanYsTotalPageTv = (TextView) view.findViewById(R.id.scan_ys_total_page);
        scanSkipTv = (TextView) view.findViewById(R.id.scan_ys_skip);

        initListener();
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
            if(onStartScanYouShuListener!=null){
                onStartScanYouShuListener.startScan(currentPage);
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

    public interface OnStartScanYouShuListener{
        void startScan(int page);
    }

    public void setOnStartScanYouShuListener(OnStartScanYouShuListener onStartScanYouShuListener) {
        this.onStartScanYouShuListener = onStartScanYouShuListener;
    }
}
