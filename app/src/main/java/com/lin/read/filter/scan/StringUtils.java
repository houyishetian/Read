package com.lin.read.filter.scan;

import android.util.Log;

import com.lin.read.filter.scan.qidian.QiDianConstants;
import com.lin.read.filter.BookInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class StringUtils {

	private static String baseRankPage = "https://www.qidian.com/rank/%s?chn=%d&page=%d";
	private static String baseRankPageString = "https://www.qidian.com/rank/%s?chn=%s&page=%s";
	private static String baseBookInfoPage="https://book.qidian.com";
	public static final String INPUTTYPE_FLOAT="INPUTTYPE_FLOAT";
	public static final String INPUTTYPE_INTEGER="INPUTTYPE_INTEGER";

	public static boolean isEmpty(String data) {
		return data == null || data.length() == 0;
	}

	public static String getRankPageUrlByTypeAndPageString(String rankType,
													 String bookType, int page) {
		if (isEmpty(rankType) || isEmpty(bookType) || page <= 0) {
			return null;
		}
		return String.format(baseRankPageString, rankType, bookType, page);
	}

	public static String getBookScoreUrl(String token,String bookId){
		if(isEmpty(token)||isEmpty(bookId)){
			return null;
		}
//		/ajax/comment/index?_csrfToken=iB7tBdYlfk0KZO12QQoqKjF3d9deFyOXC2eU46wZ&bookId=1004895684&pageSize=15
		String resultUrl=baseBookInfoPage+"/ajax/comment/index?_csrfToken=%s&bookId=%s&pageSize=15";
		return String.format(resultUrl,token,bookId);
	}

	public static String getBookDetailsInfo(String bookId){
		if(isEmpty(bookId)){
			return null;
		}
//		https://book.qidian.com/info/1004895684
		return baseBookInfoPage+"/info/"+bookId;
	}

	public static String getBookId(String bookUrl){
		if(isEmpty(bookUrl)){
			return null;
		}
		//  //book.qidian.com/info/1005263115
		String firstHandle=bookUrl.replace("//book.qidian.com/info/","");
		Pattern p=Pattern.compile("\\d+");
		Matcher m=p.matcher(firstHandle);
		if(m.matches()){
			return firstHandle;
		}
		return null;
	}

	public static String setQiDianDefaultValue(String currentInput,String defaultValue,String inputType){
		if(isEmpty(defaultValue)||isEmpty(inputType)){
			return null;
		}
		if(isEmpty(currentInput)){
			return defaultValue;
		}
		if(inputType.equals(INPUTTYPE_INTEGER)){
			try {
				int current=Integer.parseInt(currentInput);
				int defaultOne=Integer.parseInt(defaultValue);
				if(current<defaultOne){
					return defaultValue;
				}
				return currentInput;
			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}else if(inputType.equals(INPUTTYPE_FLOAT)){
			try {
				float current=Float.parseFloat(currentInput);
				float defaultOne=Float.parseFloat(defaultValue);
				if(current<defaultOne){
					return defaultValue;
				}
				return currentInput;
			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static String getAllContentString(HttpURLConnection conn) throws IOException {
		if(conn==null){
			return null;
		}
		InputStream input=conn.getInputStream();
		String unicodeType="UTF-8";
		BufferedReader reader=new BufferedReader(new InputStreamReader(
				input, unicodeType));
		String current=null;
		String result="";
		while((current=reader.readLine())!=null){
			result=result+current;
		}
		reader.close();
		input.close();
		return result;
	}

	public static boolean isWordsNumVipClickRecommendFit(SearchInfo searchInfo, BookInfo bookInfo) {
		if (searchInfo == null || bookInfo == null) {
			return false;
		}
		if (searchInfo.getWordsNum() != null && searchInfo.getRecommend() != null && bookInfo.getWordsNum() != null
				&& bookInfo.getVipClick() != null && bookInfo.getRecommend() != null) {
			try {
				float currentRecommend = Float.parseFloat(bookInfo.getRecommend());
				float searchRecommend = Float.parseFloat(searchInfo.getRecommend());

				float currentWordsNum = Float.parseFloat(bookInfo.getWordsNum());
				float searchWordsNum = Float.parseFloat(searchInfo.getWordsNum());

				return currentRecommend >= searchRecommend && currentWordsNum >= searchWordsNum;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String formatLastUpdate(String srcDate){
		if(isEmpty(srcDate)){
			return null;
		}
		if(srcDate.contains("今天")||srcDate.contains("今日")){
			Date date=new Date();
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			String day=format.format(date);
			Pattern p=Pattern.compile("[ ]?(\\d{2}:\\d{2})");
			Matcher m=p.matcher(srcDate);
			if(m.find()){
				return day+" "+m.group(1)+"更新";
			}
			return null;
		}else if(srcDate.contains("昨日")||srcDate.contains("昨天")){
			Date date=new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			date = calendar.getTime();
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			String day=format.format(date);
			Pattern p=Pattern.compile("[ ]?(\\d{2}:\\d{2})");
			Matcher m=p.matcher(srcDate);
			if(m.find()){
				return day+" "+m.group(1)+"更新";
			}
			return null;
		}else{
			Pattern p=Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}):\\d{2}");
			Matcher m=p.matcher(srcDate);
			if(m.matches()){
				return m.group(1)+"更新";
			}
		}
		return srcDate;
	}

	public static String getCharSet(HttpURLConnection conn){
		if(conn==null){
			Log.e("Test","conn is null,cannot get charset");
			return null;
		}
		String contentType = conn.getContentType();
		//contentType:text/html; charset=gbk
		if(!isEmpty(contentType)){
			Pattern pattern=Pattern.compile("charset=([\\S]+)");
			Matcher matcher=pattern.matcher(contentType);
			if(matcher.find()){
				return matcher.group(1).toUpperCase();
			}
		}
		String url = conn.getURL().toString();
		//如果相应头里面没有编码格式,用下面这种
		CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance();
		codepageDetectorProxy.add(JChardetFacade.getInstance());
		codepageDetectorProxy.add(ASCIIDetector.getInstance());
		codepageDetectorProxy.add(UnicodeDetector.getInstance());
		codepageDetectorProxy.add(new ParsingDetector(false));
		codepageDetectorProxy.add(new ByteOrderMarkDetector());
		try {
			Charset charset = codepageDetectorProxy.detectCodepage(new URL(url));
			String charsetName=charset.name();
			Log.e("Test","get charset with  CodepageDetectorProxy:"+charsetName);
			return charsetName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e("Test","cannot get charset , use UTF-8");
		return "UTF-8";
	}
}
