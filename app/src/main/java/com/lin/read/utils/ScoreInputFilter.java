package com.lin.read.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by lisonglin on 2017/10/13.
 */

public class ScoreInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        //控制输入范围 8.0-10
        String newInput = source.toString();
        String oldInput = dest.toString();
        //最大10.0 是四位数，不能再输入了
        if (oldInput.length() >= 4) {
            return "";
        }
        //当前未输入，可输入 1/8/8./8.1/9/9./9.1/10/10./10.0
        if (oldInput.length() == 0) {
            if (newInput.matches("1|[89]{1}[\\.]?|[89]{1}\\.[\\d]{1}|10[\\.]?|10\\.0")) {
                return newInput;
            }
            return "";
        } else if (oldInput.length() == 1) {
            //当前输入1位，可能有8/9/1
            //第一位
            if (dstart == 0) {
                //只允许插入8./9.
                if (newInput.matches("[89]\\.")) {
                    return newInput;
                }
                return "";
            } else {
                //已输入8/9，只允许输入./.1
                if (oldInput.matches("[89]{1}")) {
                    if (newInput.matches("\\.[\\d]?")) {
                        return newInput;
                    }
                    return "";
                } else if ("1".equals(oldInput)) {
                    //已输入1，只允许输入 0/0./0.0
                    if (newInput.matches("0\\.?|0\\.0")) {
                        return newInput;
                    }
                    return "";
                }
                return "";
            }
        } else if (oldInput.length() == 2) {
            //当前已输入2位，可能是8./10
            //不许新数据放在第一位
            if (dstart == 0) {
                return "";
            }else if(dstart==1){
                //允许10中间插入0.，变成10.0
                if("10".equals(oldInput)){
                    if(newInput.matches("0\\.")){
                        return newInput;
                    }
                }
                return "";
            }else if (dstart == 2) {
                //已输入8.，只能输入1
                if (oldInput.matches("[89]{1}\\.")) {
                    if (newInput.matches("[\\d]{1}")) {
                        return newInput;
                    }
                    return "";
                } else if ("10".equals(oldInput)) {
                    //已输入10，只能输入./.0
                    if (newInput.matches("\\.|\\.0")) {
                        return newInput;
                    }
                    return "";
                }
                return "";
            }
            return "";
        }else if(oldInput.length()==3){
            //已输入3位，可能是8.9/10.
            //已输入8.3，不许再输入了
            if(oldInput.matches("[89]\\.[\\d]{1}")){
                return "";
            }
            if(oldInput.matches("10\\.")){
                if(newInput.matches("0")){
                    return newInput;
                }
            }
            return  "";
        }
        return "";
    }
}
