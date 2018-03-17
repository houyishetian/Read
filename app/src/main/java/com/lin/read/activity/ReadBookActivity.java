package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lin.read.R;
import com.lin.read.filter.BookInfo;
import com.lin.read.utils.Constants;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class ReadBookActivity extends Activity {

    private BookInfo bookInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);
        Intent intent = getIntent();
        if (intent != null) {
            bookInfo = intent.getParcelableExtra(Constants.KEY_SKIP_TO_READ);
        }
    }
}
