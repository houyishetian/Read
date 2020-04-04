package com.lin.read.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.lin.read.R

class LaunchActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
            finish()
        }, 200)
    }
}