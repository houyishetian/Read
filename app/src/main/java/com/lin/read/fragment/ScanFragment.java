package com.lin.read.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
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

import com.lin.read.R;
import com.lin.read.adapter.ScanBookItemAdapter;
import com.lin.read.adapter.ScanTypeAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.decoration.ScanTypeItemDecoration;
import com.lin.read.activity.LoadingDialogActivity;
import com.lin.read.activity.MainActivity;
import com.lin.read.filter.SearchInfo;
import com.lin.read.filter.StringUtils;
import com.lin.read.filter.qidian.QiDianConstants;
import com.lin.read.filter.qidian.entity.QiDianBookInfo;
import com.lin.read.utils.Constants;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.utils.NumberInputFilter;
import com.lin.read.utils.QiDianBookComparator;
import com.lin.read.utils.ScoreInputFilter;

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
    private RecyclerView scanRankTypeRcv;
    private RecyclerView scanBookTypeRcv;
    private RecyclerView allBooksRcv;

    private ScanTypeAdapter scanWebTypeAdapter;
    private ScanTypeAdapter scanRankTypeAdapter;
    private ScanTypeAdapter scanBookTypeAdapter;
    private ScanBookItemAdapter allBookAdapter;

    private EditText scoreEt;
    private EditText scoreNumEt;
    private EditText wordsNumEt;
    private EditText recommendEt;

    private ScrollView scrollView;

    private ArrayList<QiDianBookInfo> allBookData;

    private Button scanOK;

    //添加用来在键盘显示的时候，改变该view的高度（键盘高度），从而把layout顶上去；若是键盘隐藏，则将该view高度设置为0
    private View tempViewForSoft;

    //用来记录上次监听到的屏幕高度变化时的高度，避免重复处理，否则会陷入死循环
    int lastHeight = -1;
    private boolean isSoftInputDisplay=false;

    private Handler handler;

    private final int SORT_BY_SCORE=0;
    private final int SORT_BY_SCORE_NUM=1;
    private final int SORT_BY_WORDS=2;
    private final int SORT_BY_RECOMMEND=3;
    private final int SORT_BY_VIP_CLICK=4;

    private final int SORT_ASCEND=1;
    private final int SORT_DESCEND=2;

    private int lastClickItem=-1;
    private int lastSortType=-1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan, null);
        initView(view);
        setSoftInputStateListener();
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
        allBooksRcv= (RecyclerView) view.findViewById(R.id.rcv_scan_all_books);

        scoreEt = (EditText) view.findViewById(R.id.et_socre);
        scoreNumEt = (EditText) view.findViewById(R.id.et_socre_num);
        wordsNumEt = (EditText) view.findViewById(R.id.et_words_num);
        recommendEt = (EditText) view.findViewById(R.id.et_recommend);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        tempViewForSoft = view.findViewById(R.id.tempView_for_soft);
        scanOK = (Button) view.findViewById(R.id.scan_ok);

        allBookData=new ArrayList<>();

        handler=new Handler();

        setAdapter();

        setInputFilter();

        scanFilterTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_in);
                scanFilterLayout.startAnimation(anim);
                scanFilterLayout.setVisibility(View.VISIBLE);
            }
        });

        scanFilterBlank.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideFilterLayout();
            }
        });

        scanOK.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                hideFilterLayout();
                SearchInfo searchInfo=getSearchInfo();
                if(searchInfo!=null){
                  Log.e("Test",searchInfo.toString());
//                showScaningDialog();
                    Intent intent = new Intent(getActivity(), LoadingDialogActivity.class);
                    intent.putExtra(Constants.KEY_SEARCH_INFO, searchInfo);
                    startActivityForResult(intent, Constants.SCAN_REQUEST_CODE);
                }
            }
        });

        scanSortIv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(allBookData.size()!=0){
                    showSortDialog();
                }
            }
        });
    }

    private void setAdapter() {
        scanWebTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanWebTypeList);
        scanRankTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanRankTypeList);
        scanBookTypeAdapter = new ScanTypeAdapter(getActivity(), QiDianConstants.scanBookTypeList);
        allBookAdapter=new ScanBookItemAdapter(getActivity(),allBookData);

        scanWebTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanWebTypeAdapter.setDefaultChecked("起点");
        scanWebTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(), 15));
        scanWebTypeRcv.setAdapter(scanWebTypeAdapter);

        scanRankTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanRankTypeAdapter.setDefaultChecked("推荐");
        scanRankTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(), 15));
        scanRankTypeRcv.setAdapter(scanRankTypeAdapter);

        scanBookTypeRcv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        scanBookTypeAdapter.setDefaultChecked("玄幻");
        scanBookTypeRcv.addItemDecoration(new ScanTypeItemDecoration(getActivity(), 15));
        scanBookTypeRcv.setAdapter(scanBookTypeAdapter);

        allBooksRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        allBooksRcv.addItemDecoration(new ScanBooksItemDecoration(getActivity()));
        allBooksRcv.setAdapter(allBookAdapter);
    }

    public boolean isFilterLayoutVisble() {
        return scanFilterLayout.getVisibility() == View.VISIBLE;
    }

    public void hideFilterLayout() {
        if(isSoftInputDisplay){
            View view=getFocusEt();
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            view.clearFocus();
        }
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
        if(isSoftInputDisplay){
            View view=getFocusEt();
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            view.clearFocus();
        }
        scanFilterLayout.setVisibility(View.GONE);
    }

    private void setInputFilter() {
        scoreEt.setFilters(new InputFilter[]{new ScoreInputFilter()});
        scoreNumEt.setFilters(new InputFilter[]{new NumberInputFilter(6)});
        wordsNumEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
        recommendEt.setFilters(new InputFilter[]{new NumberInputFilter(4)});
    }

    /**
     * 设置键盘状态的监听
     */
    private void setSoftInputStateListener() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
                    isSoftInputDisplay=false;
                    android.util.Log.e("Test", "hide softInput!---" + heightDifference);
                } else {
                    isSoftInputDisplay=true;
                    android.util.Log.e("Test", "show softInput!---" + heightDifference);
                }

                //若当前height改变还未处理过
                if (lastHeight != heightDifference) {
                    //将该height设置到tempViewForSoft
                    ViewGroup.LayoutParams params = tempViewForSoft.getLayoutParams();
                    params.height = heightDifference;
                    if(params.height!=0){
                        params.height=200;
                    }
                    tempViewForSoft.setLayoutParams(params);
                    //若此时是键盘显示
                    if (heightDifference != 0) {
                        ((MainActivity)getActivity()).hideBottomViews(true);
                        scrollToEndAndRequestFocus();
                    }else{
                        ((MainActivity)getActivity()).hideBottomViews(false);
                    }
                    //将当前高度记为已处理，否则fullScroll会requestLayout,会再次触发onGlobalLayout，这样会陷入死循环
                    lastHeight = heightDifference;
                }
            }
        });
    }

    private void scrollToEndAndRequestFocus(){
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

    /**
     * 获取触发键盘时获取焦点的view
     * @return
     */
    private EditText getFocusEt() {

        if (scoreEt.hasFocus()) {
            return scoreEt;
        }
        if (scoreNumEt.hasFocus()) {
            return scoreNumEt;
        }
        if (wordsNumEt.hasFocus()) {
            return wordsNumEt;
        }
        if (recommendEt.hasFocus()) {
            return recommendEt;
        }
        return null;
    }

    public SearchInfo getSearchInfo() {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setWebType(scanWebTypeAdapter.getCheckedInfo().getId());
        searchInfo.setRankType(scanRankTypeAdapter.getCheckedInfo().getId());
        searchInfo.setBookType(scanBookTypeAdapter.getCheckedInfo().getId());

        String score = StringUtils.setQiDianDefaultValue(scoreEt.getText().toString(),"8.0",StringUtils.INPUTTYPE_FLOAT);
        String scoreNum =StringUtils.setQiDianDefaultValue(scoreNumEt.getText().toString(),"300",StringUtils.INPUTTYPE_INTEGER);
        String wordsNum =StringUtils.setQiDianDefaultValue(wordsNumEt.getText().toString(),"200",StringUtils.INPUTTYPE_INTEGER);
        String recommend =StringUtils.setQiDianDefaultValue(recommendEt.getText().toString(),"50",StringUtils.INPUTTYPE_INTEGER);
        if (score == null || scoreNum == null || wordsNum == null || recommend == null) {
            return null;
        }
        scoreEt.setText(score);
        scoreNumEt.setText(scoreNum);
        wordsNumEt.setText(wordsNum);
        recommendEt.setText(recommend);
        searchInfo.setScore(score);
        searchInfo.setScoreNum(scoreNum);
        searchInfo.setWordsNum(wordsNum);
        searchInfo.setRecommend(recommend);
        return searchInfo;
    }

    private void showScaningDialog(){
        Dialog dialog = new Dialog(this.getActivity(), R.style.Dialog_Fullscreen);
        dialog.show();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewDialog = inflater.inflate(R.layout.dialog_scaning, null);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, layoutParams);
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
                    break;
                case Constants.SCAN_RESPONSE_SUCC:
                    if (data != null) {
                        ArrayList<QiDianBookInfo> allBookDataFromScan = data.getBundleExtra(Constants.KEY_INTENT_FOR_BOOK_DATA).getParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA);
                        scanResultTv.setVisibility(View.VISIBLE);
                        Log.e("Test", "接收:" + allBookDataFromScan);
                        scanResultTv.setText(String.format(Constants.TEXT_SCAN_BOOK_INFO_RESULT, allBookDataFromScan.size()));
                        allBookData.clear();
                        allBookData.addAll(allBookDataFromScan);
                        Collections.sort(allBookData,new QiDianBookComparator(QiDianBookComparator.SortType.DESCEND, QiDianBookComparator.BookType.SCORE));
                        allBookAdapter.notifyDataSetChanged();
                        allBooksRcv.smoothScrollToPosition(0);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Dialog sortDialog;
    private void showSortDialog(){
        sortDialog = new Dialog(this.getActivity(), R.style.Dialog_Fullscreen);
        sortDialog.show();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewDialog = inflater.inflate(R.layout.dialog_qidian_book_sort, null);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        sortDialog.setContentView(viewDialog, layoutParams);

        View sortByScore=viewDialog.findViewById(R.id.qidian_sort_by_score);
        View sortByScoreNum=viewDialog.findViewById(R.id.qidian_sort_by_scorenum);
        View sortByWordsNum=viewDialog.findViewById(R.id.qidian_sort_by_wordsnum);
        View sortByRecommend=viewDialog.findViewById(R.id.qidian_sort_by_recommend);
        View sortByVipClick=viewDialog.findViewById(R.id.qidian_sort_by_vipclick);
        sortByScore.setTag(SORT_BY_SCORE);
        sortByScoreNum.setTag(SORT_BY_SCORE_NUM);
        sortByWordsNum.setTag(SORT_BY_WORDS);
        sortByRecommend.setTag(SORT_BY_RECOMMEND);
        sortByVipClick.setTag(SORT_BY_VIP_CLICK);
        sortByScore.setOnClickListener(new SortDialogItemsClickListener());
        sortByScoreNum.setOnClickListener(new SortDialogItemsClickListener());
        sortByWordsNum.setOnClickListener(new SortDialogItemsClickListener());
        sortByRecommend.setOnClickListener(new SortDialogItemsClickListener());
        sortByVipClick.setOnClickListener(new SortDialogItemsClickListener());
    }

    class SortDialogItemsClickListener extends NoDoubleClickListener{

        @Override
        public void onNoDoubleClick(View v) {
            Object o=v.getTag();
            if(o!=null){
                try {
                    int currentItem= (int) o;
                    QiDianBookComparator.SortType sortType=getSortType(currentItem);
                    QiDianBookComparator.BookType bookType=getSortBookType(currentItem);
                    Collections.sort(allBookData,new QiDianBookComparator(sortType, bookType));
                    allBookAdapter.notifyDataSetChanged();
                    allBooksRcv.smoothScrollToPosition(0);

                    if(sortDialog!=null&&sortDialog.isShowing()){
                        sortDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private QiDianBookComparator.SortType getSortType(int currentClickItem){
        if(lastClickItem==currentClickItem){
            if(lastSortType==SORT_ASCEND){
                lastSortType=SORT_DESCEND;
            }else{
                lastSortType=SORT_ASCEND;
            }
        }else{
            lastSortType=SORT_DESCEND;
        }
        lastClickItem=currentClickItem;
        QiDianBookComparator.SortType sortType;
        if(lastSortType==SORT_DESCEND){
            sortType= QiDianBookComparator.SortType.DESCEND;
        }else{
            sortType= QiDianBookComparator.SortType.ASCEND;
        }
        return sortType;
    }

    private QiDianBookComparator.BookType getSortBookType(int currentClickItem){
        QiDianBookComparator.BookType bookType=null;
        switch (currentClickItem) {
            case SORT_BY_SCORE:
                bookType= QiDianBookComparator.BookType.SCORE;
                break;
            case SORT_BY_SCORE_NUM:
                bookType= QiDianBookComparator.BookType.SCORE_NUM;
                break;
            case SORT_BY_WORDS:
                bookType= QiDianBookComparator.BookType.WORDS_NUM;
                break;
            case SORT_BY_RECOMMEND:
                bookType= QiDianBookComparator.BookType.RECOMMEND;
                break;
            case SORT_BY_VIP_CLICK:
                bookType= QiDianBookComparator.BookType.VIPC_LICK;
                break;
        }
        return bookType;
    }
}
