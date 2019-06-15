package com.lin.read.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by lisonglin on 2017/10/13.
 */

public class UIUtils {
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,context.getResources().getDisplayMetrics() );
    }
}
