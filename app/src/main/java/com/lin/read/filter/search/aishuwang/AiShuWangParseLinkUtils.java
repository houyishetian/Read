package com.lin.read.filter.search.aishuwang;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/3/25.
 */

public class AiShuWangParseLinkUtils {
    public static String parseLink(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.startsWith("//")) {
            return "http:" + data;
        }
        //http://www.22ff.com/
        if (data.startsWith("/")) {
            return "http://www.22ff.com" + data;
        }
        return data;
    }

    public static String parseDownloadLink(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        //http://www.22ff.com/xs/180297.txt --> http://67.229.159.202/full/181/180297.txt
        //http://www.22ff.com/xs/37758.txt --> http://67.229.159.202/full/232/231584.txt
        Pattern pattern=Pattern.compile("/(\\d{5,})\\.txt");
        Matcher matcher=pattern.matcher(data);
        if(matcher.find()){
            String bookId=matcher.group(1);
            try {
                String page = Integer.parseInt(bookId.substring(0, bookId.length() - 3)) + 1 + "";
                return "http://67.229.159.202/full/" + page + matcher.group();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
