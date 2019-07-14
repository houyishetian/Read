package com.lin.read.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lin.read.R;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class DialogUtil {
    private static volatile DialogUtil instance;

    private DialogUtil() {

    }

    public static DialogUtil getInstance() {
        if (instance == null) {
            synchronized (DialogUtil.class) {
                if (instance == null) {
                    instance = new DialogUtil();
                    return instance;
                }
            }
        }
        return instance;
    }

    private Dialog loadingDialog;
    public void showLoadingDialog(Context context) {
        hideLoadingView();
        loadingDialog = new Dialog(context, R.style.Dialog_Fullscreen);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewDialog = inflater.inflate(R.layout.dialog_loading, null);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        loadingDialog.setContentView(viewDialog, layoutParams);
    }

    public void hideLoadingView() {
        if (loadingDialog != null ) {
            if(loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }
    }
}
