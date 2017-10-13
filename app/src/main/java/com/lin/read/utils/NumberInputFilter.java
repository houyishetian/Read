package com.lin.read.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by lisonglin on 2017/10/13.
 */

public class NumberInputFilter implements InputFilter {
    private int maxLen;

    public NumberInputFilter(int maxLen) {
        //输入不能大于6位或小于0
        if (maxLen > 6 || maxLen <= 0) {
            maxLen = 4;
        }
        this.maxLen = maxLen;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String newInput = source.toString();
        String oldInput = dest.toString();

        //中间插入数据，只要长度允许，都可以输入，比如粘贴11
        if(dstart>0){
            //超出范围，无法输入
            if (newInput.length() + oldInput.length() > maxLen) {
                return "";
            }
            return newInput;
        }else{
            //起始位置插入数据，需要看下插入数据的有效位数
            try {
                int newNum = Integer.parseInt(newInput);
                newInput = newNum + "";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            //超出范围，无法输入；不能在第一位插入0
            if ("0".equals(newInput)||newInput.length() + oldInput.length() > maxLen) {
                return "";
            }
            return newInput;
        }
    }
}
