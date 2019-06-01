package com.lin.read.filter;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lin.read.R;
import com.lin.read.adapter.DialogSortTypeAdapter;
import com.lin.read.filter.scan.SortInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lisonglin on 2018/4/16.
 */

public class BookComparatorUtil {

    private final int SORT_ASCEND = 1;
    private final int SORT_DESCEND = 2;

    private int lastClickItem = -1;
    private int lastSortType = -1;

    public BookComparator.SortType getSortType(int currentClickItem) {
        if(currentClickItem==SortInfo.ID_SORT_BY_DEFAULT){
            lastSortType = SORT_ASCEND;
        }else{
            if (lastClickItem == currentClickItem) {
                if (lastSortType == SORT_ASCEND) {
                    lastSortType = SORT_DESCEND;
                } else {
                    lastSortType = SORT_ASCEND;
                }
            } else {
                lastSortType = SORT_DESCEND;
            }
        }

        lastClickItem = currentClickItem;
        BookComparator.SortType sortType;
        if (lastSortType == SORT_DESCEND) {
            sortType = BookComparator.SortType.DESCEND;
        } else {
            sortType = BookComparator.SortType.ASCEND;
        }
        return sortType;
    }

    public BookComparator.BookType getSortBookType(int currentClickItem) {
        BookComparator.BookType bookType = null;
        switch (currentClickItem) {
            case SortInfo.ID_SORT_BY_DEFAULT:
                bookType = BookComparator.BookType.POSTION;
                break;
            case SortInfo.QD_ID_SORT_BY_SCORE:
                bookType = BookComparator.BookType.SCORE;
                break;
            case SortInfo.QD_ID_SORT_BY_SCORE_NUM:
                bookType = BookComparator.BookType.SCORE_NUM;
                break;
            case SortInfo.ZH_ID_SORT_BY_RAISE:
                bookType = BookComparator.BookType.RAISE;
                break;
            case SortInfo.ZH_ID_SORT_BY_COMMENT:
                bookType = BookComparator.BookType.COMMENT;
                break;
            case SortInfo.QD_ID_SORT_BY_WORDS:
            case SortInfo.ZH_ID_SORT_BY_WORDS:
                bookType = BookComparator.BookType.WORDS_NUM;
                break;
            case SortInfo.QD_ID_SORT_BY_RECOMMEND:
            case SortInfo.ZH_ID_SORT_BY_RECOMMEND:
                bookType = BookComparator.BookType.RECOMMEND;
                break;
            case SortInfo.QD_ID_SORT_BY_VIP_CLICK:
            case SortInfo.ZH_ID_SORT_BY_CLICK:
                bookType = BookComparator.BookType.VIP_CLICK;
                break;
        }
        return bookType;
    }

    public void setLastClickItem(int lastClickItem) {
        this.lastClickItem = lastClickItem;
    }

    private Dialog sortDialog;

    public void showSortDialog(Activity activity, final ArrayList<BookInfo> allBookData, final OnSortCompletedListener onSortCompletedListener) {
        if (allBookData == null || allBookData.size() == 0) {
            return;
        }
        List<SortInfo> allSortInfo = getSortInfoData(allBookData.get(0));
        if (allSortInfo == null || allSortInfo.size() == 0) {
            return;
        }
        sortDialog = new Dialog(activity, R.style.Dialog_Fullscreen);
        sortDialog.show();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View viewDialog = inflater.inflate(R.layout.dialog_qidian_book_sort, null);
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        sortDialog.setContentView(viewDialog, layoutParams);

        ListView sortListView = (ListView) viewDialog.findViewById(R.id.dialog_select_sort_type_lv);
        DialogSortTypeAdapter adapter = new DialogSortTypeAdapter(activity,allSortInfo);
        sortListView.setAdapter(adapter);

        adapter.setOnItemSortTypeClickListener(new DialogSortTypeAdapter.OnItemSortTypeClickListener() {
            @Override
            public void onItemClick(int clickId) {
                BookComparator.SortType sortType = getSortType(clickId);
                BookComparator.BookType bookType = getSortBookType(clickId);
                Collections.sort(allBookData, new BookComparator(sortType, bookType));
                hideSortDialog();
                if(onSortCompletedListener!=null){
                    onSortCompletedListener.onSortCompleted();
                }
            }
        });
    }

    private void hideSortDialog() {
        if (sortDialog != null && sortDialog.isShowing()) {
            sortDialog.dismiss();
        }
    }

    public interface OnSortCompletedListener{
        void onSortCompleted();
    }

    private List<SortInfo> getSortInfoData(BookInfo oneItem) {
        if (oneItem == null || StringUtils.isEmpty(oneItem.getWebName())) {
            return null;
        }
        if(Constants.WEB_QIDIAN.equals(oneItem.getWebName()) || Constants.WEB_YOU_SHU.equals(oneItem.getWebName())){
            List<SortInfo> allSortInfo = new ArrayList<>();
            allSortInfo.add(new SortInfo(SortInfo.SORT_BY_DEFAULT,SortInfo.ID_SORT_BY_DEFAULT));
            if(!TextUtils.isEmpty(oneItem.getScore())){
                allSortInfo.add(new SortInfo(SortInfo.QD_SORT_BY_SCORE,SortInfo.QD_ID_SORT_BY_SCORE));
            }
            if(!TextUtils.isEmpty(oneItem.getScoreNum())){
                allSortInfo.add(new SortInfo(SortInfo.QD_SORT_BY_SCORE_NUM,SortInfo.QD_ID_SORT_BY_SCORE_NUM));
            }
            if(!TextUtils.isEmpty(oneItem.getWordsNum())){
                allSortInfo.add(new SortInfo(SortInfo.QD_SORT_BY_WORDS_NUM,SortInfo.QD_ID_SORT_BY_WORDS));
            }
            if(!TextUtils.isEmpty(oneItem.getRecommend())){
                allSortInfo.add(new SortInfo(SortInfo.QD_SORT_BY_RECOMMEND,SortInfo.QD_ID_SORT_BY_RECOMMEND));
            }
            if(!TextUtils.isEmpty(oneItem.getClick())){
                allSortInfo.add(new SortInfo(SortInfo.QD_SORT_BY_VIP_CLICK,SortInfo.QD_ID_SORT_BY_VIP_CLICK));
            }
            return allSortInfo;
        }else if(Constants.WEB_ZONGHENG.equals(oneItem.getWebName())){
            List<SortInfo> allSortInfo = new ArrayList<>();
            allSortInfo.add(new SortInfo(SortInfo.SORT_BY_DEFAULT,SortInfo.ID_SORT_BY_DEFAULT));
            if(!TextUtils.isEmpty(oneItem.getRaiseNum())){
                allSortInfo.add(new SortInfo(SortInfo.ZH_SORT_BY_RAISE,SortInfo.ZH_ID_SORT_BY_RAISE));
            }
            if(!TextUtils.isEmpty(oneItem.getCommentNum())){
                allSortInfo.add(new SortInfo(SortInfo.ZH_SORT_BY_COMMENT,SortInfo.ZH_ID_SORT_BY_COMMENT));
            }
            if(!TextUtils.isEmpty(oneItem.getWordsNum())){
                allSortInfo.add(new SortInfo(SortInfo.ZH_SORT_BY_WORDS_NUM,SortInfo.ZH_ID_SORT_BY_WORDS));
            }
            if(!TextUtils.isEmpty(oneItem.getRecommend())){
                allSortInfo.add(new SortInfo(SortInfo.ZH_SORT_BY_RECOMMEND,SortInfo.ZH_ID_SORT_BY_RECOMMEND));
            }
            if(!TextUtils.isEmpty(oneItem.getClick())){
                allSortInfo.add(new SortInfo(SortInfo.ZH_SORT_BY_CLICK,SortInfo.ZH_ID_SORT_BY_CLICK));
            }
            return allSortInfo;
        }else {
            return null;
        }
    }
}
