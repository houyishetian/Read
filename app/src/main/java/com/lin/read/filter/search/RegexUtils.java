package com.lin.read.filter.search;

import com.lin.read.filter.scan.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class RegexUtils {
    public static List<String> getDataByRegex(String content, String regex, List<Integer> allGroup) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(regex)
                || allGroup == null || allGroup.size() == 0) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        try {
            if (matcher.find()) {
                List<String> resultList = new ArrayList<String>();
                for (Integer group : allGroup) {
                    resultList.add(matcher.group(group));
                }
                return resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<List<String>> getDataByRegexManyData(String content, String regex, List<Integer> allGroup) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(regex)
                || allGroup == null || allGroup.size() == 0) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        List<List<String>> resultList = new ArrayList<>();
        try {
            while (matcher.find()) {
                List<String> currentList = new ArrayList<String>();
                for (Integer group : allGroup) {
                    currentList.add(matcher.group(group));
                }
                resultList.add(currentList);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
