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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.adapter.DialogWebTypeAdapter;
import com.lin.read.adapter.SearchBookItemAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.GetDownloadInfoTask;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.WebTypeBean;
import com.lin.read.utils.Constants;
import com.lin.read.utils.NoDoubleClickListener;
import com.lin.read.view.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2017/10/11.
 */

public class SearchFragment extends Fragment {

    private final int INDEX_BIQUGE = 0;
    private final int INDEX_NOVEL_80 = 1;
    private final int INDEX_DING_DIAN = 2;

    private final int INDEX_DEFAULT = 2;

    private EditText bookNameEt;
    private Button searchBt;
    private RecyclerView searchResultRcv;
    private ArrayList<BookInfo> allbookInfo;
    private SearchBookItemAdapter searchBookItemadapter;
    private TextView selectTypeTv;
    private String currentSelectWeb;

    private List<WebTypeBean> webTypeList;
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
        webTypeList=getAllWebTypes();
        selectTypeTv.setText(webTypeList.get(INDEX_DEFAULT).getWebName());
        currentSelectWeb = webTypeList.get(INDEX_DEFAULT).getTag();
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
        ListView webTypeLv= (ListView) viewDialog.findViewById(R.id.dialog_select_web_lv);
        DialogWebTypeAdapter adapter=new DialogWebTypeAdapter(getActivity(),webTypeList);
        webTypeLv.setAdapter(adapter);
        adapter.setOnItemWebClickListener(new DialogWebTypeAdapter.OnItemWebClickListener() {
            @Override
            public void onItemClick(WebTypeBean info) {
                currentSelectWeb = info.getTag();
                selectTypeTv.setText(info.getWebName());
                if (selectWebDialog != null && selectWebDialog.isShowing()) {
                    selectWebDialog.dismiss();
                }
            }
        });
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
                }else{
                    Toast.makeText(getActivity(),"未获取到数据!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(getActivity(),"网络请求失败!",Toast.LENGTH_SHORT).show();
                DialogUtil.getInstance().hideLoadingView();
            }
        });
        task.execute(new String[]{bookName,"0"});
    }

    private List<WebTypeBean> getAllWebTypes(){
        List<WebTypeBean> webTypeBeenList=new ArrayList<>();
        WebTypeBean webTypeBean0=new WebTypeBean();
        webTypeBean0.setWebName("笔趣阁");
        webTypeBean0.setTag(Constants.RESOLVE_FROM_BIQUGE);
        webTypeBeenList.add(webTypeBean0);

        WebTypeBean webTypeBean1=new WebTypeBean();
        webTypeBean1.setWebName("80小说");
        webTypeBean1.setTag(Constants.RESOLVE_FROM_NOVEL80);
        webTypeBeenList.add(webTypeBean1);

        WebTypeBean webTypeBean2=new WebTypeBean();
        webTypeBean2.setWebName("顶点");
        webTypeBean2.setTag(Constants.RESOLVE_FROM_DINGDIAN);
        webTypeBeenList.add(webTypeBean2);

        WebTypeBean webTypeBean3=new WebTypeBean();
        webTypeBean3.setWebName("笔下");
        webTypeBean3.setTag(Constants.RESOLVE_FROM_BIXIA);
        webTypeBeenList.add(webTypeBean3);

        return  webTypeBeenList;
    }
}
