package com.lin.read.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.adapter.BookMarksAdapter;
import com.lin.read.bookmark.BookMarkBean;
import com.lin.read.bookmark.BookMarkSharePres;
import com.lin.read.utils.Constants;
import com.lin.read.view.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2017/10/11.
 */

public class BookMarksFragment extends Fragment {
    private TextView selectAllTv;
    private TextView deleteTv;
    private TextView totalNumTv;
    private ListView bookMarksLv;
    private List<BookMarkBean> allBookMarks;
    private BookMarksAdapter bookMarksAdapter;
    private final int CLICK_SELECT_ALL=0;
    private final int CLICK_CANCEL_ALL=1;
    private int currentClickSelectNum = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_book_marks, null);
        initView(view);
        getData();
        return view;
    }

    private void initView(View view) {
        selectAllTv = (TextView) view.findViewById(R.id.book_marks_select_all);
        deleteTv = (TextView) view.findViewById(R.id.book_marks_delete);
        totalNumTv = (TextView) view.findViewById(R.id.book_marks_total_num);
        bookMarksLv = (ListView) view.findViewById(R.id.book_marks_lv);
        deleteTv.setEnabled(false);

        allBookMarks=new ArrayList<>();
        bookMarksAdapter = new BookMarksAdapter(getActivity(), this ,allBookMarks);
        bookMarksLv.setAdapter(bookMarksAdapter);

        bookMarksLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectAllTv.setVisibility(View.VISIBLE);
                deleteTv.setVisibility(View.VISIBLE);
                bookMarksAdapter.selectAll(false);
                currentClickSelectNum = CLICK_CANCEL_ALL;
                return false;
            }
        });

        selectAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentClickSelectNum==CLICK_SELECT_ALL){
                    currentClickSelectNum=CLICK_CANCEL_ALL;
                    bookMarksAdapter.selectAll(false);
                }else{
                    currentClickSelectNum=CLICK_SELECT_ALL;
                    bookMarksAdapter.selectAll(true);
                }
            }
        });

        bookMarksAdapter.setOnCheckBoxClickListener(new BookMarksAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClick() {
                List<BookMarkBean> selectedItems = bookMarksAdapter.getAllSelectMarks();
                if (selectedItems != null && selectedItems.size() == allBookMarks.size()) {
                    currentClickSelectNum=CLICK_SELECT_ALL;
                }else{
                    currentClickSelectNum=CLICK_CANCEL_ALL;
                }
                if (selectedItems != null && selectedItems.size() > 0) {
                    deleteTv.setEnabled(true);
                }else{
                    deleteTv.setEnabled(false);
                }
            }
        });
        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }

    public boolean isEditMode(){
        return selectAllTv.getVisibility()==View.VISIBLE;
    }

    public void exitEditMode(){
        selectAllTv.setVisibility(View.GONE);
        deleteTv.setVisibility(View.GONE);
        bookMarksAdapter.cancelSelect();
    }

    private void getData(){
        DialogUtil.getInstance().showLoadingDialog(getActivity());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final List<BookMarkBean> booksMarkList = BookMarkSharePres.getBookMarkBeans(getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.getInstance().hideLoadingView();
                        if(booksMarkList!=null){
                            totalNumTv.setText(String.format("共%s条书签",""+booksMarkList.size()));
                            allBookMarks.clear();
                            allBookMarks.addAll(booksMarkList);
                            bookMarksAdapter.notifyDataSetChanged();
                            selectAllTv.setVisibility(View.GONE);
                            deleteTv.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!isHidden()){
            getData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.READ_REQUEST_CODE) {
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteData(){
        DialogUtil.getInstance().showLoadingDialog(getActivity());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BookMarkSharePres.deleteBookMarks(getActivity(),bookMarksAdapter.getAllSelectMarks());
                final List<BookMarkBean> booksMarkList = BookMarkSharePres.getBookMarkBeans(getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.getInstance().hideLoadingView();
                        if(booksMarkList!=null){
                            totalNumTv.setText(String.format("共%s条书签",""+booksMarkList.size()));
                            allBookMarks.clear();
                            allBookMarks.addAll(booksMarkList);
                            bookMarksAdapter.notifyDataSetChanged();
                            selectAllTv.setVisibility(View.GONE);
                            deleteTv.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
