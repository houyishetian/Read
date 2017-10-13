package com.lin.read.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.lin.read.R;

/**
 * Created by lisonglin on 2017/10/14.
 */

public class LoadingView extends ImageView {
    private Context context;
    public LoadingView(Context context) {
        super(context);
        this.context=context;
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus){
            Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.rotate_scaning_loading);
            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
            circle_anim.setInterpolator(interpolator);
            if (circle_anim != null) {
                startAnimation(circle_anim);  //开始动画
            }
        }
    }
}
