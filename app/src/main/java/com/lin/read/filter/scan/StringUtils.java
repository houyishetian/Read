package com.lin.read.filter.scan;

import android.text.format.DateFormat;
import android.util.Log;
import info.monitorenter.cpdetector.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static boolean isEmpty(String data) {
		return data == null || data.length() == 0;
	}

	public static String getBookDetailsInfo(String bookId){
		if(isEmpty(bookId)){
			return null;
		}
//		https://book.qidian.com/info/1004895684
//		return baseBookInfoPage+"/info/"+bookId;
		return String.format("https://m.qidian.com/book/%s",bookId);
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

	public static String getAllContentString(HttpURLConnection conn) throws IOException {
		if(conn==null){
			return null;
		}
		InputStream input=conn.getInputStream();
		String unicodeType="UTF-8";
		BufferedReader reader=new BufferedReader(new InputStreamReader(
				input, unicodeType));
		String current;
		String result="";
		while((current=reader.readLine())!=null){
			result=result+current;
		}
		reader.close();
		input.close();
		return result;
	}

//	public static boolean isWordsNumFit(SearchInfo searchInfo, BookInfo bookInfo){
//		try {
//			float currentWordsNum = Float.parseFloat(bookInfo.getWordsNum());
//			float searchWordsNum = Float.parseFloat(searchInfo.getWordsNum());
//			return currentWordsNum >= searchWordsNum;
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	public static boolean isScoreAndScoreNumFie(SearchInfo searchInfo, BookInfo bookInfo){
//		try {
//			String currentScore = bookInfo.getScore();
//			String currentScoreNum = bookInfo.getScoreNum();
//			String searchScore = searchInfo.getScore();
//			String searchScoreNum = searchInfo.getScoreNum();
//			float currentScoref = Float.parseFloat(currentScore);
//			float searchScoref = Float.parseFloat(searchScore);
//			int currentScoreNumi = Integer.parseInt(currentScoreNum);
//			int searchScoreNumi = Integer.parseInt(searchScoreNum);
//			return currentScoref >= searchScoref && currentScoreNumi >= searchScoreNumi;
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		return false;
//	}

	public static String formatLastUpdate(String srcDate){
		if(isEmpty(srcDate)){
			return null;
		}
		if(srcDate.contains("今天")||srcDate.contains("今日")){
			String day = DateFormat.format("yyyy-MM-dd", new Date()).toString();
			Pattern p = Pattern.compile("[ ]?(\\d{2}:\\d{2})");
			Matcher m = p.matcher(srcDate);
			if (m.find()) {
				return day + " " + m.group(1) + "更新";
			}
			return null;
		}else if(srcDate.contains("昨日")||srcDate.contains("昨天")){
			Date date=new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			date = calendar.getTime();
			String day=DateFormat.format("yyyy-MM-dd", date).toString();
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

	public static String parseDataByTenThousand(String num){
		if(isEmpty(num)){
			return null;
		}
		try {
			int parse = Integer.parseInt(num);
			return String.format(Locale.CHINA,"%.2f", parse/10000f);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
