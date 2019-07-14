package com.lin.read.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lin.read.R;
import com.lin.read.adapter.SearchMusicAdapter;
import com.lin.read.decoration.ScanBooksItemDecoration;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.music.search.SearchMusicTask;
import com.lin.read.music.search.bean.seachresult.MusicDataSongBean;
import com.lin.read.music.search.bean.seachresult.MusicDataSongItemBean;
import com.lin.read.view.DialogUtil;

import java.util.ArrayList;

public class SearchMusicActivity extends Activity {

    private EditText etMusicKey;
    private Button btnSearch;
    private RecyclerView rcvSearchResult;
    private View viewEmpty;
    private SearchMusicAdapter searchMusicAdapter;
    private ArrayList<MusicDataSongItemBean> allMusicData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        initView();
        initListener();
    }

    private void initView() {
        etMusicKey = findViewById(R.id.et_search_music_key);
        btnSearch = findViewById(R.id.btn_search);
        rcvSearchResult = findViewById(R.id.rcv_search_result);
        viewEmpty = findViewById(R.id.empty_view);
        allMusicData = new ArrayList<>();
        searchMusicAdapter = new SearchMusicAdapter(this, allMusicData);
        rcvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rcvSearchResult.addItemDecoration(new ScanBooksItemDecoration(this));
        rcvSearchResult.setAdapter(searchMusicAdapter);
    }

    private void initListener() {
        etMusicKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                }
                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search() {
        String musicKey = etMusicKey.getText().toString();
        if (StringUtils.isEmpty(musicKey)) {
            Toast.makeText(this, "请输入搜索关键字!", Toast.LENGTH_SHORT).show();
            return;
        }
        hideSoft();
        DialogUtil.getInstance().showLoadingDialog(this);
        new SearchMusicTask(new SearchMusicTask.Listener() {
            @Override
            public void complete(final MusicDataSongBean songBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.getInstance().hideLoadingView();
                        allMusicData.clear();
                        ArrayList<MusicDataSongItemBean> result = songBean == null ? null : songBean.getList();
                        if (result != null && result.size() > 0) {
                            allMusicData.addAll(result);
                        }
                        boolean isEmpty = allMusicData.isEmpty();
                        viewEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                        rcvSearchResult.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                        searchMusicAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).execute(musicKey);
    }

    public void hideSoft() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etMusicKey.getWindowToken(), 0);
    }
}
