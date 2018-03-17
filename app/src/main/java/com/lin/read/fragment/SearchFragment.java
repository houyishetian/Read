package com.lin.read.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.adapter.SearchBookItemAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.GetDownloadInfoTask;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.view.DialogUtil;

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
    private TextView selectTypeTv;
    private int currentSelectWeb = GetDownloadInfoTask.RESOLVE_FROM_BIQUGE;
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
        selectTypeTv = (TextView) view.findViewById(R.id.select_web);
        selectTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectWebDialog();
            }
        });

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
                hideSoft();
                search(bookName);
            }
        });
    }

    public void hideSoft(){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(bookNameEt.getWindowToken(),0);
    }

    Dialog selectWebDialog;
    private void showSelectWebDialog(){
        selectWebDialog = new Dialog(this.getActivity(), R.style.Dialog_Fullscreen);
        selectWebDialog.show();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View viewDialog = inflater.inflate(R.layout.dialog_search_select_web, null);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        selectWebDialog.setContentView(viewDialog, layoutParams);
        View webNovel80=viewDialog.findViewById(R.id.web_novel_80);
        webNovel80.setTag(GetDownloadInfoTask.RESOLVE_FROM_NOVEL80);
        View webBiQuGe=viewDialog.findViewById(R.id.web_biquge);
        webBiQuGe.setTag(GetDownloadInfoTask.RESOLVE_FROM_BIQUGE);
        webNovel80.setOnClickListener(new WebSelectItemsClickListener());
        webBiQuGe.setOnClickListener(new WebSelectItemsClickListener());
    }

    class WebSelectItemsClickListener extends NoDoubleClickListener {
        @Override
        public void onNoDoubleClick(View v) {
            Object o=v.getTag();
            if(o!=null){
                try {
                    int currentItem= (int) o;
                    currentSelectWeb = currentItem;
                    selectTypeTv.setText(((TextView) v).getText().toString());
                    if (selectWebDialog != null && selectWebDialog.isShowing()) {
                        selectWebDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void search(final String bookName){
        if(StringUtils.isEmpty(bookName)){
            return;
        }

        DialogUtil.getInstance().showLoadingDialog(this.getActivity());
        GetDownloadInfoTask task=new GetDownloadInfoTask(this.getActivity(), currentSelectWeb, new GetDownloadInfoTask.OnTaskListener() {
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
