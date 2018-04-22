package com.lin.read.filter.scan.zongheng;

import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.ScanBookBean;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.scan.qidian.QiDianRegexUtils;
import com.lin.read.filter.search.RegexUtils;
import com.lin.read.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lisonglin on 2018/4/22.
 */

public class ZongHengHttpUtils {

    /**
     * get max page num and book info url from rank page
     * @return 0--max page num
     */
    public static List<Object> getMaxPageAndBookInfoFromRankPage(SearchInfo searchInfo, int page) throws IOException {
        if(searchInfo==null){
            return null;
        }
        String urlLink = ZongHengStringUtil.getSearchUrl(searchInfo,page);
        Log.e("Test","execute start:"+urlLink);
        HttpURLConnection conn = HttpUtils.getConn(urlLink,3);
        List<Object> maxPageAndBookUrlList = new ArrayList<Object>();
        BufferedReader reader = null;
        if(conn==null){
            throw new IOException(Constants.EXCEPTION_GET_CONN_ERROR);
        }else{
            String unicodeType="UTF-8";
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), unicodeType));
            String current = null;
            // whether already get max page
            boolean getMaxPage = false;
            int position=0;
            while ((current = reader.readLine()) != null) {
                //<a class="fs14" href="http://book.zongheng.com/book/726652.html" title="医品至尊" target="_blank">医品至尊</a>
                String bookUrlRegex = "<a class=\"fs14\" href=\"([^\n^\"]{1,})\" title=\"([^\n^\"]{1,})\" target=\"_blank\">([^\n^\"]{1,})</a>";
                List<Integer> bookUrlGroups = Arrays.asList(1, 3);
                List<String> bookUrlResult = RegexUtils.getDataByRegex(current, bookUrlRegex, bookUrlGroups);
                if (bookUrlResult != null && bookUrlResult.size() == bookUrlGroups.size()) {
                    ScanBookBean scanBookBean=new ScanBookBean();
                    scanBookBean.setUrl(bookUrlResult.get(0));
                    scanBookBean.setPage(page);
                    scanBookBean.setPosition(position);
                    scanBookBean.setBookName(bookUrlResult.get(1));
                    position++;
                    maxPageAndBookUrlList.add(scanBookBean);
                }else{
                    if (!getMaxPage) {
                        //<div class="pagenumber pagebar" page="1" count="4" total="166">
                        String maxPageRegex = "<div class=\"pagenumber pagebar\" page=\"\\d+\" count=\"(\\d+)\" total=\"\\d+\"";
                        List<Integer> maxPageGroups = Arrays.asList(1);
                        List<String> maxPageResult = RegexUtils.getDataByRegex(current, maxPageRegex, maxPageGroups);
                        if (maxPageResult != null && maxPageResult.size() == maxPageGroups.size()) {
                            getMaxPage = true;
                            maxPageAndBookUrlList.add(0, maxPageResult.get(0));
                            break;
                        }
                    }
                }
            }
            if (!getMaxPage) {
                maxPageAndBookUrlList.add(0, null);
            }
            return maxPageAndBookUrlList;
        }
    }

    public static BookInfo getBookDetailsInfo(SearchInfo searchInfo,String bookUrl,String bookName) throws IOException {
        if (searchInfo == null || StringUtils.isEmpty(bookUrl) || StringUtils.isEmpty(bookName)) {
            return null;
        }
        //http://book.zongheng.com/book/726652.html
        if (StringUtils.isEmpty(bookUrl)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConn(bookUrl,3);
        if (conn == null) {
            throw new IOException();
        }
        BufferedReader reader = null;
        String unicodeType="UTF-8";
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));
        String current = null;
        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookName(bookName);
        boolean isStartResulveLastUpdate=false;
        while ((current = reader.readLine()) != null) {
            if (!isStartResulveLastUpdate) {
                if(current.trim().equals("<div class=\"uptime\">\n")){
                    isStartResulveLastUpdate=true;
                    continue;
                }
                //<em>·</em>作者：<a href="http://home.zongheng.com/show/userInfo/36130499.html" title="纯黑色祭奠作品集" target="_blank">纯黑色祭奠</a>
                String authorRegex = "<em>·</em>作者：<a href=\"([^\n^\"]{1,})\" title=\"([^\n^\"]{1,})\" target=\"_blank\">([^\n^\"]{1,})</a>";
                List<Integer> authorGroups = Arrays.asList(3);
                List<String> authorResult = RegexUtils.getDataByRegex(current, authorRegex, authorGroups);
                if (authorResult != null && authorResult.size() == authorGroups.size()) {
                    bookInfo.setAuthorName(authorResult.get(0));
                    continue;
                }
                //<em>·</em>分类：<a href="http://book.zongheng.com/store/c9/c1097/b9/u0/p1/v9/s9/t0/ALL.html">都市娱乐</a>
                String bookTypeRegex = "<em>·</em>分类：<a href=\"([^\n^\"]{1,})\">([^\n^\"]{1,})</a>";
                List<Integer> bookTypeGroups = Arrays.asList(2);
                List<String> bookTypeResult = RegexUtils.getDataByRegex(current, bookTypeRegex, bookTypeGroups);
                if (bookTypeResult != null && bookTypeResult.size() == bookTypeGroups.size()) {
                    bookInfo.setBookType(bookTypeResult.get(0));
                    continue;
                }

                //<em>·</em>字数：<span title="885343字">885343</span>字
                String wordsNumRegex = "<em>·</em>字数：<span title=\"(\\d+)(字|万字)\">";
                List<Integer> wordsNumGroups = Arrays.asList(1);
                List<String> wordsNumResult = RegexUtils.getDataByRegex(current, wordsNumRegex, wordsNumGroups);
                if (wordsNumResult != null && wordsNumResult.size() == wordsNumGroups.size()) {
                    bookInfo.setWordsNum(StringUtils.parseDataByTenThousand(wordsNumResult.get(0)));
                    continue;
                }

                //<p><span class="tit">总点击：</span>379429</p>
                //<p><span class="tit">总收藏：</span>1886</p>
                //<p><span class="tit">总推荐：</span>3279</p>
                //<p><span class="tit">评论数：</span>224</p>
                //<p><span class="tit">捧场人次：</span>156</p>
                String clickRegex = "<p><span class=\"tit\">总点击：</span>(\\d+)</p>";
                List<Integer> clickGroups = Arrays.asList(1);
                List<String> clickResult = RegexUtils.getDataByRegex(current, clickRegex, clickGroups);
                if (clickResult != null && clickResult.size() == clickGroups.size()) {
                    bookInfo.setVipClick(StringUtils.parseDataByTenThousand(clickResult.get(0)));
                    continue;
                }

                String recommendRegex = "<p><span class=\"tit\">总推荐：</span>(\\d+)</p>";
                List<Integer> recommendGroups = Arrays.asList(1);
                List<String> recommendResult = RegexUtils.getDataByRegex(current, recommendRegex, recommendGroups);
                if (recommendResult != null && recommendResult.size() == recommendGroups.size()) {
                    bookInfo.setRecommend(StringUtils.parseDataByTenThousand(recommendResult.get(0)));
                    continue;
                }

                String commentRegex = "<p><span class=\"tit\">评论数：</span>(\\d+)</p>";
                List<Integer> commentGroups = Arrays.asList(1);
                List<String> commentResult = RegexUtils.getDataByRegex(current, commentRegex, commentGroups);
                if (commentResult != null && commentResult.size() == commentGroups.size()) {
                    bookInfo.setCommentNum(commentResult.get(0));
                    continue;
                }

                String raiseRegex = "<p><span class=\"tit\">捧场人次：</span>(\\d+)</p>";
                List<Integer> raiseGroups = Arrays.asList(1);
                List<String> raiseResult = RegexUtils.getDataByRegex(current, raiseRegex, raiseGroups);
                if (raiseResult != null && raiseResult.size() == raiseGroups.size()) {
                    bookInfo.setRaiseNum(raiseResult.get(0));
                    continue;
                }
            }else{
                //        	<div class="uptime">
            	//·8个月前<br>
                bookInfo.setLastUpdate(current.trim().replaceAll("<br>","").replaceAll("·",""));
                break;
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		if (StringUtils.isWordsNumVipClickRecommendFit(searchInfo, bookInfo) &&  StringUtils.isCommentRaiseFit(searchInfo,bookInfo)) {
			Log.e("Test","书籍符合条件："+ bookInfo.toString());
			return bookInfo;
		}
        return null;
    }
}
