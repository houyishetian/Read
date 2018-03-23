package com.lin.read.utils;

import com.lin.read.filter.scan.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class ChapterHandleUtils {
    public static String handleUpdateStr(String chapter) {
        if(StringUtils.isEmpty(chapter)){
            return chapter;
        }
        List<String> regexList = new ArrayList<String>();
        regexList.add("(\\([^\n]*\\))");
        regexList.add("(\\[[^\n]*\\])");
        regexList.add("(（[^\n]*）)");
        regexList.add("(【[^\n]*】)");
        regexList.add("第[0-9一二三四五六七八九十零]更");

        String currentResult = chapter;
        for (String item : regexList) {
            Pattern pattern = Pattern.compile(item);
            Matcher matcher = pattern.matcher(currentResult);
            while (matcher.find()) {
                String findString=matcher.group(0);
                if(!StringUtils.isEmpty(findString)){
                    Pattern patternReal = Pattern.compile("[0-9一二三四五六七八九十零终末上中下]+");
                    Matcher matcherReal = patternReal.matcher(findString.substring(1, findString.length() - 1));
                    if (!matcherReal.matches()) {
                        currentResult = currentResult.replace(findString, "");
                    }
                }
            }
        }
        return currentResult;
    }
}
