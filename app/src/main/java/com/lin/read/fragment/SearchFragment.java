package com.lin.read.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.adapter.SearchBookItemAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.GetDownloadInfoTask;
import com.lin.read.filter.search.novel80.ResolveUtilsFor80;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.view.DialogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2017/10/11.
 */

public class SearchFragment extends Fragment {

    private EditText bookNameEt;
    private Button searchBt;
    private RecyclerView searchResultRcv;
    private ArrayList<BookInfo> allbookInfo;
    private SearchBookItemAdapter searchBookItemadapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_search, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        initView(view);
        return view;
    }

    private void initView(View view){
        bookNameEt= (EditText) view.findViewById(R.id.et_search_bookname);
        searchBt= (Button) view.findViewById(R.id.btn_search);
        searchResultRcv= (RecyclerView) view.findViewById(R.id.rcv_search_result);

        allbookInfo=new ArrayList<>();
        searchBookItemadapter=new SearchBookItemAdapter(getActivity(),allbookInfo);
        searchResultRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchResultRcv.addItemDecoration(new ScanBooksItemDecoration(getActivity()));
        searchResultRcv.setAdapter(searchBookItemadapter);

        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookName=bookNameEt.getText().toString();
                if(StringUtils.isEmpty(bookName)){
                    Toast.makeText(getActivity(), "请输入书名!", Toast.LENGTH_SHORT).show();
                    return;
                }
                search(bookName);
            }
        });
    }

    private void search(final String bookName){
        if(StringUtils.isEmpty(bookName)){
            return;
        }

        DialogUtil.getInstance().showLoadingDialog(this.getActivity());
        GetDownloadInfoTask task=new GetDownloadInfoTask(this.getActivity(), GetDownloadInfoTask.RESOLVE_FROM_NOVEL80, new GetDownloadInfoTask.OnTaskListener() {
            @Override
            public void onSucc(List<BookInfo> allBooks) {
                DialogUtil.getInstance().hideLoadingView();
                if(allBooks!=null){
                    allbookInfo.clear();
                    allbookInfo.addAll(allBooks);
                    searchBookItemadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed() {
                DialogUtil.getInstance().hideLoadingView();
            }
        });
        task.execute(new String[]{bookName,"0"});
    }
}
