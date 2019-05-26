package com.lin.read.filter.search;

import android.text.TextUtils;
import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.aishuwang.AiShuWangParseLinkUtils;
import com.lin.read.utils.ChapterHandleUtils;
import com.lin.read.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class ResolveChapterUtils {
    public static List<BookChapterInfo> getChapterInfo(BookInfo bookInfo)
            throws IOException {
        if (bookInfo == null) {
            return null;
        }
        String url = bookInfo.getBookLink();
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        String regex = null;
        List<Integer> allGroups = Arrays.asList(new Integer[]{1, 2});
        String uniCodeType=StringUtils.getCharSet(conn);
        switch (bookInfo.getWebType()) {
            case Constants.RESOLVE_FROM_NOVEL80:
                //<li><a rel="nofollow" href="http://www.qiushu.cc/t/67053/17977700.html">第1章 脱去球衣，换上西服</a></li>
                regex = "<li><a rel=\"nofollow\" href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></li>";
                break;
            case Constants.RESOLVE_FROM_BIQUGE:
                //<dd><a href="http://www.biquge5200.com/51_51647/19645389.html">第一章 十六年</a></dd>
                regex = "<dd><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></dd>";
                break;
            case Constants.RESOLVE_FROM_DINGDIAN:
                //<td class="L"><a href="16460819.html">第二章 青牛镇</a></td> -->https://www.x23us.com/html/0/328/16460819.html
                //<td class="L"><a href="http://www.23us.so/files/article/html/10/10674/7219354.html">新书《一念永恒》，VIP上架啦</a></td>
                regex = "<td class=\"L\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></td>";
                break;
            case Constants.RESOLVE_FROM_BIXIA:
                //<dd> <a style="" href="/0_743/5168336.html">第1814章 安检环节?</a></dd>
                regex = "<dd> <a style=\"\" href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></dd>";
                break;
            case Constants.RESOLVE_FROM_AISHU:
                //<div class="clc"><a href="/xs/180445/20519569/">第一章 黄山真君和九洲一号群</a></div>
                regex = "<div class=\"clc\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></div>";
                break;
            case Constants.RESOLVE_FROM_QINGKAN:
                //<li><a href="https://www.qingkan9.com/book/daojun_7399/34175873.html">第一章 没白来</a></li>
                regex="li><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></li>";
                break;
            default:
                return null;
        }
        List<BookChapterInfo> result = new ArrayList<BookChapterInfo>();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), uniCodeType));

        String current = null;
        Log.e("Test", "开始解析目录:"+bookInfo.getWebType()+"-->"+bookInfo.getBookLink());
        //for BiXia start
        boolean isFirstDtAlreadyFind = false;
        boolean isSecondDtAlreadyFind = false;
        //for BiXia end

        //for Ai Shu Wang start
        boolean isResolveAiShuStart=false;
        //for Ai Shu Wang end
        while ((current = reader.readLine()) != null) {
            if(Constants.RESOLVE_FROM_DINGDIAN.equals(bookInfo.getWebType())){
                List<List<String>> currentResult = RegexUtils.getDataByRegexManyData(current.trim(), regex, allGroups);
                if (currentResult != null && currentResult.size() > 0) {
                    for (List<String> item : currentResult) {
                        if (item != null && item.size() == allGroups.size()) {
                            BookChapterInfo bookChapterInfo = new BookChapterInfo();
                            bookChapterInfo.setWebType(bookInfo.getWebType());
                            bookChapterInfo.setChapterLink(bookInfo.getBookLink() + item.get(0));
                            String oriChapterName = item.get(1);
                            bookChapterInfo.setChapterNameOri(oriChapterName);
                            bookChapterInfo.setChapterName(ChapterHandleUtils.handleUpdateStr(oriChapterName));
                            result.add(bookChapterInfo);
                        }
                    }
                    break;
                }
            } else if (Constants.RESOLVE_FROM_AISHU.equals(bookInfo.getWebType())) {
                if(isResolveAiShuStart){
                    if (current.trim().equals("<div class=\"clear\"></div>")) {
                        isResolveAiShuStart = false;
                        break;
                    }
                    List<List<String>> currentResult = RegexUtils.getDataByRegexManyData(current.trim(), regex, allGroups);
                    if (currentResult != null && currentResult.size() > 0) {
                        for (List<String> item : currentResult) {
                            if (item != null && item.size() == allGroups.size()) {
                                BookChapterInfo bookChapterInfo = new BookChapterInfo();
                                bookChapterInfo.setWebType(bookInfo.getWebType());
                                bookChapterInfo.setChapterLink(AiShuWangParseLinkUtils.parseLink(item.get(0)));
                                String oriChapterName = item.get(1);
                                bookChapterInfo.setChapterNameOri(oriChapterName);
                                bookChapterInfo.setChapterName(ChapterHandleUtils.handleUpdateStr(oriChapterName));
                                result.add(bookChapterInfo);
                            }
                        }
                        break;
                    }
                }else{
                    if(current.trim().equals("<div class=\"neirong\">")){
                        isResolveAiShuStart=true;
                    }
                }
            }else if(Constants.RESOLVE_FROM_BIXIA.equals(bookInfo.getWebType())){
                if (!isFirstDtAlreadyFind) {
                    if (current.trim().contains("<dt>")) {
                        isFirstDtAlreadyFind = true;
                    }
                    continue;
                } else {
                    if(!isSecondDtAlreadyFind){
                        if (current.trim().contains("<dt>")) {
                            isSecondDtAlreadyFind = true;
                        }
                        continue;
                    }
                }
                if (current.trim().equals("</dl>")) {
                    break;
                }
                List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), regex, allGroups);
                if (currentResult != null && currentResult.size() == allGroups.size()) {
                    BookChapterInfo bookChapterInfo=new BookChapterInfo();
                    bookChapterInfo.setWebType(bookInfo.getWebType());
                    String bookLinkOri=currentResult.get(0);
                    if (!TextUtils.isEmpty(bookLinkOri) && !bookLinkOri.startsWith("https://") && !bookLinkOri.startsWith("http://")) {
                        bookLinkOri="https://www.bixia.org/"+bookLinkOri;
                    }
                    bookChapterInfo.setChapterLink(bookLinkOri);
                    String oriChapterName = currentResult.get(1);
                    bookChapterInfo.setChapterNameOri(oriChapterName);
                    bookChapterInfo.setChapterName(ChapterHandleUtils.handleUpdateStr(oriChapterName));
                    result.add(bookChapterInfo);
                }
            }else{
                List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), regex, allGroups);
                if (currentResult != null && currentResult.size() == allGroups.size()) {
                    BookChapterInfo bookChapterInfo=new BookChapterInfo();
                    bookChapterInfo.setWebType(bookInfo.getWebType());
                    bookChapterInfo.setChapterLink(currentResult.get(0));
                    String oriChapterName = currentResult.get(1);
                    bookChapterInfo.setChapterNameOri(oriChapterName);
                    bookChapterInfo.setChapterName(ChapterHandleUtils.handleUpdateStr(oriChapterName));
                    result.add(bookChapterInfo);
                }
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getChapterContent(BookChapterInfo bookChapterInfo)
            throws IOException {
        if (bookChapterInfo == null) {
            return null;
        }
        String url;
        if (bookChapterInfo.isComplete()) {
            url = bookChapterInfo.getChapterLink();
        } else {
            url = bookChapterInfo.getNextLink();
        }
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }

        String uniCodeType = StringUtils.getCharSet(conn);
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), uniCodeType));

        String current = null;
        Log.e("Test", "开始解析章节内容："+bookChapterInfo.getChapterLink());

        String resultContent="";

        boolean isStart=false;

        //
        String nextLink = null;

        while ((current = reader.readLine()) != null) {
            if(Constants.RESOLVE_FROM_BIQUGE.equals(bookChapterInfo.getWebType())){
                String biqugeRegex = "<div id=\"content\">([^\n]{1,})";;
                List<Integer> biqugeGroups = Arrays.asList(new Integer[]{1});
                List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), biqugeRegex, biqugeGroups);
                if (currentResult != null && currentResult.size() == biqugeGroups.size()) {
                    resultContent=currentResult.get(0);
                    break;
                }
            } else if (Constants.RESOLVE_FROM_NOVEL80.equals(bookChapterInfo.getWebType())) {
                if (!isStart && "<div class=\"book_content\" id=\"content\">".equals(current.trim())) {
                    isStart = true;
                    continue;
                }
                if (isStart && current.contains("<div class=\"con_l\"><script>read_di()")) {
                    //<a href="/t/53604/" target="_blank">一婚到底，总裁大人难伺候最新章节</a><div class="contads r"><script type="text/javascript">reads();</script></div>
                    //<a href="/t/18882/" target="_blank">豪门盛宠：魅色首席诱情人全文阅读</a><div class="contads l"><script type="text/javascript">reads();</script></div>
                    //<strong>在线阅读天火大道Http://wWw.qiushu.cc/</strong>
                    String removeRegex = "(<strong>([^\n^\"]+)</strong>)|(<a href=\"([^\n^\"]+)\" target=\"_blank\">([^\n^\"]+)</a><div class=\"contads (r|l)\"><script type=\"text/javascript\">reads\\(\\);</script></div>)";
                    resultContent = RegexUtils.replaceDataByRegex(resultContent, removeRegex);
                    isStart = false;
                    break;
                }
                if (isStart) {
                    resultContent += current;
                }
            }else if(Constants.RESOLVE_FROM_DINGDIAN.equals(bookChapterInfo.getWebType())){
                String dingdianRegex = "<dd id=\"contents\">([^\n]{1,})</dd>";;
                List<Integer> biqugeGroups = Arrays.asList(new Integer[]{1});
                List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), dingdianRegex, biqugeGroups);
                if (currentResult != null && currentResult.size() == biqugeGroups.size()) {
                    resultContent=currentResult.get(0);
                    break;
                }
            }else if(Constants.RESOLVE_FROM_BIXIA.equals(bookChapterInfo.getWebType())){
                if (!isStart && "<div id=\"content\">".equals(current.trim())) {
                    isStart = true;
                    continue;
                }
                if (isStart && current.trim().equals("<script>chaptererror();</script>")) {
                    isStart = false;
                    break;
                }
                if (isStart) {
                    resultContent += current;
                }
            } else if(Constants.RESOLVE_FROM_AISHU.equals(bookChapterInfo.getWebType())){
                if(current.trim().startsWith("<div id=\"chapter_content\">")){
                    resultContent = current.trim().replace("<div id=\"chapter_content\">","")
                            .replace("<script language=\"javascript\">setFontSize();</script>","");
                    break;
                }
            }else if(Constants.RESOLVE_FROM_QINGKAN.equals(bookChapterInfo.getWebType())){
                //<div id="content"><div id="txtright"><script src="https://www.qk6.org/file/script/9.js"></script></div><!--go-->
                //https://www.qk6.org
                String domain = null;
                try {
                    URI uri = new URI(bookChapterInfo.getChapterLink());
                    domain = uri.getScheme()+"://"+uri.getHost();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                final String startContentStr = "<div id=\"content\"><div id=\"txtright\"><script src=\""+domain+"/file/script/9.js\"></script></div><!--go-->";
                if(!isStart && current.trim().startsWith(startContentStr)){
                    resultContent = current.trim().replace(startContentStr,"");
                    continue;
                }
                //<a class="current"><b>1</b></a>
                String currentFlagRegex = "<a class=\"current\"><b>\\d+</b></a>";
                if (!isStart && current.trim().matches(currentFlagRegex)) {
                    isStart = true;
                    continue;
                }
                if(isStart){
                    //<a href="https://www.qingkan9.com/book/daojun_7399/34175995_1.html">1</a>
                    String pageRegex = "<a href=\"([^\n]{1,})\">(\\d){1,}</a>";;
                    List<Integer> pageGroups = Arrays.asList(1,2);
                    List<List<String>> currentResult = RegexUtils.getDataByRegexManyData(current.trim(), pageRegex, pageGroups);
                    if (currentResult != null && currentResult.size() >0) {
                        nextLink = currentResult.get(0).get(0);
                        break;
                    }
                }
            }else{
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
        if(!StringUtils.isEmpty(nextLink)){
            bookChapterInfo.setComplete(false);
            bookChapterInfo.setNextLink(nextLink);
            if (resultContent != null && resultContent.endsWith("<br /><br />")) {
                resultContent = resultContent.substring(0, resultContent.length() - "<br /><br />".length());
            }
        }else{
            bookChapterInfo.setComplete(true);
            bookChapterInfo.setNextLink(null);
        }
        return resultContent;
    }
}
