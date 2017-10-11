package com.lin.read.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lin.read.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lisonglin on 2017/10/11.
 */

public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 3000);
    }
}
