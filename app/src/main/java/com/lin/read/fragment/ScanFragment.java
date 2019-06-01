package com.lin.read.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.activity.LoadingDialogActivity;
import com.lin.read.activity.util.QiDianScanUtil;
import com.lin.read.activity.util.YouShuScanUtil;
import com.lin.read.activity.util.ZongHengScanUtil;
import com.lin.read.adapter.ScanBookItemAdapter;
import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.decoration.ScanTypeItemDecoration;
import com.lin.read.activity.MainActivity;
import com.lin.read.filter.BookComparatorUtil;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.SortInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.scan.qidian.QiDianConstants;
import com.lin.read.utils.Constants;
import com.lin.read.utils.MessageUtils;
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

    private View scanResultYouShuLl;

    private RecyclerView scanWebTypeRcv;
    private ScanTypeAdapter scanWebTypeAdapter;

    private RecyclerView allBooksRcv;
    private ScanBookItemAdapter allBookAdapter;
    private ArrayList<BookInfo> allBookData;
    private TextView emptyTv;

    private Button scanOK;

    private View layoutScanQiDian;
    private View layoutScanZongHeng;
    private View layoutScan17k;
    private View layoutScanYouShu;
    private View[] allScanLayouts;
    private int currentWebPosition;
    private final int LAYOUT_INDEX_QIDIAN=0;
    private final int LAYOUT_INDEX_ZONGHENG=1;
    private final int LAYOUT_INDEX_17K=2;
    private final int LAYOUT_INDEX_YOUSHU  =3;

    private Handler handler;

    private QiDianScanUtil qiDianScanUtil;
    private ZongHengScanUtil zongHengScanUtil;
    private YouShuScanUtil youShuScanUtil;

    private BookComparatorUtil bookComparatorUtil;

    //添加用来在键盘显示的时候，改变该view的高度（键盘高度），从而把layout顶上去；若是键盘隐藏，则将该view高度设置为0
    private View tempViewForSoft;

    private ScrollView scrollView;

    //用来记录上次监听到的屏幕高度变化时的高度，避免重复处理，否则会陷入死循环
    int lastHeight = -1;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    private boolean isSoftInputDisplay = false;

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

        youShuScanUtil = new YouShuScanUtil();
        youShuScanUtil.initYouShuViews(this,view,handler);

        scanFilterTv = (TextView) view.findViewById(R.id.scan_filter);
        scanSortIv = (RelativeLayout) view.findViewById(R.id.scan_sort);
        scanResultTv = (TextView) view.findViewById(R.id.scan_result_qidian);
        scanFilterLayout = view.findViewById(R.id.layout_scan_filter);
        scanFilterBlank = view.findViewById(R.id.scan_filter_blank);

        scanResultYouShuLl = view.findViewById(R.id.scan_result_youshu);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        tempViewForSoft = view.findViewById(R.id.tempView_for_soft);

        scanOK = (Button) view.findViewById(R.id.scan_ok);

        currentWebPosition=LAYOUT_INDEX_QIDIAN;

        scanWebTypeRcv = (RecyclerView) view.findViewById(R.id.rcv_scan_web);

        allBooksRcv= (RecyclerView) view.findViewById(R.id.rcv_scan_all_books);

        emptyTv = (TextView) view.findViewById(R.id.empty_view);

        layoutScan17k=view.findViewById(R.id.layout_scan_17k);
        layoutScanZongHeng=view.findViewById(R.id.layout_scan_zongheng);
        layoutScanQiDian=view.findViewById(R.id.layout_scan_qidian);
        layoutScanYouShu = view.findViewById(R.id.layout_scan_youshu);
        allScanLayouts=new View[]{layoutScanQiDian,layoutScanZongHeng,layoutScan17k,layoutScanYouShu};

        setAdapter();

        scanFilterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_in);
                scanFilterLayout.startAnimation(anim);
                scanFilterLayout.setVisibility(View.VISIBLE);
                setSoftInputStateListener();
            }
        });

        scanFilterBlank.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideFilterLayout();
            }
        });

        scanOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFilterLayout();
                SearchInfo searchInfo = null;
                switch (currentWebPosition){
                    case LAYOUT_INDEX_QIDIAN:
                        searchInfo = qiDianScanUtil.getSearchInfo();
                        break;
                    case LAYOUT_INDEX_ZONGHENG:
                        searchInfo = zongHengScanUtil.getSearchInfo();
                        break;
                    case LAYOUT_INDEX_YOUSHU:
                        searchInfo = youShuScanUtil.getSearchInfo();
                        searchInfo.setCurrentPage(1);
                        break;
                    default:
                        return;
                }
                startScanning(searchInfo);
            }
        });

        scanSortIv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(allBookData.size()!=0){
                    if (bookComparatorUtil != null) {
                        bookComparatorUtil.showSortDialog(getActivity(), allBookData, new BookComparatorUtil.OnSortCompletedListener() {
                            @Override
                            public void onSortCompleted() {
                                refreshBookData();
                            }
                        });
                    }
                }
            }
        });

        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = getActivity().getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                if (heightDifference == 0) {
                    isSoftInputDisplay = false;
                    android.util.Log.e("Test", "hide softInput!---" + heightDifference);
                } else {
                    isSoftInputDisplay = true;
                    android.util.Log.e("Test", "show softInput!---" + heightDifference);
                }

                //若当前height改变还未处理过
                if (lastHeight != heightDifference) {
                    //将该height设置到tempViewForSoft
                    ViewGroup.LayoutParams params = tempViewForSoft.getLayoutParams();
                    params.height = heightDifference;
                    if (params.height != 0) {
                        params.height = 200;
                    }
                    tempViewForSoft.setLayoutParams(params);
                    //若此时是键盘显示
                    if (heightDifference != 0) {
                        ((MainActivity) getActivity()).hideBottomViews(true);
                        scrollToEndAndRequestFocus(handler, scrollView);
                    } else {
                        ((MainActivity) getActivity()).hideBottomViews(false);
                    }
                    //将当前高度记为已处理，否则fullScroll会requestLayout,会再次触发onGlobalLayout，这样会陷入死循环
                    lastHeight = heightDifference;
                }
            }
        };
    }

    /**
     * 设置键盘状态的监听
     */
    public void setSoftInputStateListener() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 取消键盘状态的监听
     */
    public void cancelSoftInputStateListener() {
        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }


    private void scrollToEndAndRequestFocus(Handler handler, final ScrollView scrollView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //获取触发键盘的EditText
                EditText currentFocusEt = getFocusEt();
                //scrollview滚动到末尾
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                //之前触发键盘的EditText重新获取焦点
                if (currentFocusEt != null) {
                    currentFocusEt.setFocusable(true);
                    currentFocusEt.requestFocus();
                }
            }
        });
    }

    private void setAdapter() {
        scanWebTypeAdapter = new ScanTypeAdapter(getActivity(), Constants.scanWebTypeList);
        allBookData=new ArrayList<>();
        allBookAdapter=new ScanBookItemAdapter(getActivity(),allBookData);

        scanWebTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanWebTypeAdapter.setDefaultChecked(Constants.WEB_QIDIAN);
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
                    hideSoft();
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
                cancelSoftInputStateListener();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scanFilterLayout.startAnimation(anim);
    }

    public void hideFilterLayoutWithoutAnimation() {
        hideSoft();
        cancelSoftInputStateListener();
        scanFilterLayout.setVisibility(View.GONE);
    }

    public void hideSoft() {
        if (isSoftInputDisplay) {
            View view = getFocusEt();
            if(view!=null){
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                view.clearFocus();
            }
        }
    }

    public EditText getFocusEt() {
        if (currentWebPosition == LAYOUT_INDEX_QIDIAN) {
            return qiDianScanUtil.getFocusEt();
        } else if (currentWebPosition == LAYOUT_INDEX_ZONGHENG) {
            return zongHengScanUtil.getFocusEt();
        }
        return null;
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
                    scanResultYouShuLl.setVisibility(View.GONE);
                    bookComparatorUtil = null;
                    Toast.makeText(getActivity(),"扫描失败，请检查网络!",Toast.LENGTH_SHORT).show();
                    break;
                case Constants.SCAN_RESPONSE_SUCC:
                    if (data != null) {
                        emptyTv.setVisibility(View.GONE);
                        allBooksRcv.setVisibility(View.VISIBLE);
                        ArrayList<BookInfo> allBookDataFromScan = data.getBundleExtra(Constants.KEY_INTENT_FOR_BOOK_DATA).getParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA);
                        if(currentWebPosition == LAYOUT_INDEX_YOUSHU){
                            scanResultYouShuLl.setVisibility(View.VISIBLE);
                            scanResultTv.setVisibility(View.GONE);
                            youShuScanUtil.afterGetBookInfo(data);
                        }else if(currentWebPosition == LAYOUT_INDEX_QIDIAN){
                            scanResultTv.setVisibility(View.VISIBLE);
                            scanResultYouShuLl.setVisibility(View.GONE);
                            Log.e("Test", "接收:" + allBookDataFromScan);
                            scanResultTv.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_RESULT, allBookDataFromScan.size()));
                        }
                        allBookData.clear();
                        allBookData.addAll(allBookDataFromScan);
                        Collections.sort(allBookData,new BookComparator(BookComparator.SortType.ASCEND, BookComparator.BookType.POSTION));
                        allBookAdapter.notifyDataSetChanged();
                        allBooksRcv.smoothScrollToPosition(0);
                        bookComparatorUtil = new BookComparatorUtil();
                        bookComparatorUtil.setLastClickItem(SortInfo.ID_SORT_BY_DEFAULT);
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

    private void startScanning(SearchInfo searchInfo){
        if (searchInfo != null) {
            Log.e("Test", searchInfo.toString());
            Intent intent = new Intent(getActivity(), LoadingDialogActivity.class);
            intent.putExtra(Constants.KEY_SEARCH_INFO, searchInfo);
            startActivityForResult(intent, Constants.SCAN_REQUEST_CODE);
        }
    }
}
