package com.lin.read.filter.scan.youshu;

import android.util.Log;
import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.RegexUtils;
import com.lin.read.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YouShuHttpUtil {
    public static ArrayList<Object> getAllBookInfo(SearchInfo searchInfo, int page) throws Exception {
        if (searchInfo == null) {
            return null;
        }
        String url = YouShuRegexUtil.getYouShuUrl(searchInfo, page);
        Log.d("you shu url:", url);
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        ArrayList<Object> result = new ArrayList<>();
        BufferedReader reader = null;
        String unicodeType = StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));
        String current = null;
        while ((current = reader.readLine()) != null) {
            current = current.trim();
            //onclick="ys.common.jumpurl('page',3215)">&raquo;
            List<String> pageInfo = RegexUtils.getDataByRegex(current, "onclick=\"ys.common.jumpurl\\('page',(\\d+)\\)\">&raquo;", Arrays.asList(1));
            if (pageInfo != null && pageInfo.size() > 0) {
                Log.e("page num:", pageInfo.get(0));
                result.add(pageInfo.get(0));
            }

            //<div class="title"><a href="/book/169429" target="_blank">制作人生</a></div><div class="rating"><span class="allstar00"></span><span class="rating_nums"></span><span>(2人评价)</span></div><div class="abstract">作者: 名品<br>字数: 1.83万<br>最后更新: 7<br>综合评分: <span class="num2star">0.0</span>
            //<div class="title"><a href="/book/169428" target="_blank">我开灵异</a></div><div class="rating"><span class="allstar00"></span><span class="rating_nums"></span><span>(2人评价)</span></div><div class="abstract">作者: 天璇<br>字数: 4.15万<br>最后更新: 1<br>综合评分: <span class="num2star">0.0</span>
            //<div class="title"><a href="/book/169407" target="_blank">江湖我独</a></div><div class="rating"><span class="allstar00"></span><span class="rating_nums"></span><span>(6人评价)</span></div><div class="abstract">作者: 修身<br>字数: 3.57万<br>最后更新: 2<br>综合评分: <span class="num2star">0.0</span>
            //<div class="title"><a href="/book/169370" target="_blank">恩爱秀你</a></div><div class="rating"><span class="allstar00"></span><span class="rating_nums"></span><span>(1人评价)</span></div><div class="abstract">作者: 叶涩<br>字数: 3.62万<br>最后更新: 4<br>综合评分: <span class="num2star">0.0</span>
            String regex = "<div class=\"title\"><a href=\"[^\n^\"]+\" target=\"_blank\">([^\n^\"]+)</a></div><div class=\"rating\"><span class=\"allstar00\"></span><span class=\"rating_nums\"></span><span>\\((\\d+)人评价\\)</span></div><div class=\"abstract\">作者: ([^\\n^<]+)<br>字数: ([\\d.]+)万<br>最后更新: ([^\\n^<]+)<br>综合评分: <span class=\"num2star\\\">([\\d.]+)</span>";
            List<Integer> allGroups = Arrays.asList(1,2,3,4,5,6);
            List<List<String>> bookinfos = RegexUtils.getDataByRegexManyData(current, regex, allGroups);
            if (bookinfos != null && bookinfos.size() > 0) {
                Log.e("Test，共：", "" + bookinfos.size());
                for (List<String> item : bookinfos) {
                    BookInfo bookInfo = new BookInfo();
                    bookInfo.setBookName(item.get(0));
                    bookInfo.setWebName(Constants.WEB_YOU_SHU);
                    bookInfo.setWebType(searchInfo.getWebType());
                    bookInfo.setScoreNum(item.get(1));
                    bookInfo.setAuthorName(item.get(2));
                    bookInfo.setWordsNum(item.get(3));
                    bookInfo.setLastUpdate(item.get(4));
                    bookInfo.setScore(item.get(5));
                    result.add(bookInfo);
                }
            }
        }
        return result;
    }
}
