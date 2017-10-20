package com.lin.read.filter.qidian;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.lin.read.filter.SearchInfo;
import com.lin.read.filter.StringUtils;
import com.lin.read.filter.qidian.entity.QiDianBookInfo;
import com.lin.read.filter.qidian.entity.ScoreJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QiDianHttpUtils {
	public static final String TOKEY_KEY="_csrfToken";
	public static final String EXCEPTION_GET_CONN_ERROR="EXCEPTION_GET_CONN_ERROR";
	public static String getToken() throws IOException {
		String url = "https://book.qidian.com/";
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		HttpURLConnection conn = getConn(url,3);
		if(conn==null){
			throw new IOException(EXCEPTION_GET_CONN_ERROR);
		}
		conn=getConn(url,3);
		if(conn==null){
			throw new IOException(EXCEPTION_GET_CONN_ERROR);
		}
		CookieStore cookieJar = manager.getCookieStore();
		List<HttpCookie> cookies = cookieJar.getCookies();
		for (HttpCookie cookie : cookies) {
			if(TOKEY_KEY.equals(cookie.getName())){
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * get max page num and book info url from rank page
	 * @return 0--max page num
	 */
	public static List<String> getMaxPageAndBookInfoFromRankPage(SearchInfo searchInfo,int page) throws IOException {
		if(searchInfo==null){
			return null;
		}
		String rankType=searchInfo.getRankType();
		String bookType=searchInfo.getBookType();
		String urlLink= StringUtils.getRankPageUrlByTypeAndPageString(rankType, bookType, page);
		if(!StringUtils.isEmpty(urlLink)&&("recom".equals(rankType)||"fin".equals(rankType))){
			urlLink=urlLink+"&dateType="+searchInfo.getDateType();
		}
		Log.e("Test","execute start:"+urlLink);
		HttpURLConnection conn = getConn(urlLink,3);
		List<String> maxPageAndBookUrlList = new ArrayList<String>();
		BufferedReader reader = null;
		if(conn==null){
			throw new IOException(EXCEPTION_GET_CONN_ERROR);
		}else{
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String current = null;
			// whether already get max page
			boolean getMaxPage = false;
			while ((current = reader.readLine()) != null) {
				String bookUrl = QiDianRegexUtils
						.getBookUrlFromRankPage(current);
				if (bookUrl != null) {
					maxPageAndBookUrlList.add(bookUrl);
				} else {
					if (!getMaxPage) {
						String maxPage = QiDianRegexUtils
								.getMaxPage(current);
						if (maxPage != null) {
							getMaxPage = true;
							maxPageAndBookUrlList.add(0, maxPage);
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

	/**
	 * get max page num and book info url from rank page
	 * @param urlLink
	 * @return 0--max page num
	 */
	public static List<String> getLatestBookInfoFromRankPage(String urlLink) throws IOException {
		HttpURLConnection conn = getConn(urlLink,3);
		List<String> maxPageAndBookUrlList = new ArrayList<String>();
		BufferedReader reader = null;
		if(conn==null){
			throw new IOException(EXCEPTION_GET_CONN_ERROR);
		}else {
			try {
				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String current = null;
				// whether already get max page
				boolean getMaxPage = false;
				while ((current = reader.readLine()) != null) {
					String bookUrl = QiDianRegexUtils
							.getBookUrlFromRankPage(current);
					if (bookUrl != null) {
						maxPageAndBookUrlList.add(bookUrl);
					} else {
						if (!getMaxPage) {
							String maxPage = QiDianRegexUtils
									.getMaxPage(current);
							if (maxPage != null) {
								getMaxPage = true;
								maxPageAndBookUrlList.add(0, maxPage);
								break;
							}
						}
					}
				}
				if (!getMaxPage) {
					maxPageAndBookUrlList.add(0, null);
				}
				return maxPageAndBookUrlList;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public static QiDianBookInfo getBookScoreInfo(SearchInfo searchInfo, String token, String bookUrl,int totalNum) throws IOException {
		//book.qidian.com/info/1005263115
		if(totalNum<=0){
			return null;
		}
		String url=StringUtils.getBookScoreUrl(token,StringUtils.getBookId(bookUrl));
		if(StringUtils.isEmpty(url)){
			return null;
		}
		HttpURLConnection conn=getConn(url,3);
		if(conn==null){
			throw new IOException(EXCEPTION_GET_CONN_ERROR);
		}

//		{
//			"data": {
//			"rate": 9.2,
//					"userCount": 16,
//					"iRated": 0,
//					"totalCnt": 16,
//					"pageMax": 2,
//					"iRateStar": 0,
//					"pageIndex": 1,
//					"commentInfo": [
//			{
//				"userId": 228492547,
//					"rateId": 70696,
//					"star": 5,
//					"comment": "",
//					"like": 0,
//					"time": "2017-09-27 22:26",
//					"userIcon": "//facepic.qidian.com/qd_face/349573/0/50",
//					"nickName": "书友20170118204818925",
//					"fanLevel": 0
//			}
//        ]
//		},
//			"code": 0,
//				"msg": "suc"
//		}

		String content = StringUtils.getAllContentString(conn);
		ScoreJson scoreJson = new GsonBuilder().create().fromJson(content, ScoreJson.class);
		try {
			String currentScore = scoreJson.getData().getRate();
			String currentScoreNum = scoreJson.getData().getUserCount();
			String searchScore = searchInfo.getScore();
			String searchScoreNum = searchInfo.getScoreNum();

			float currentScoref = Float.parseFloat(currentScore);
			float searchScoref = Float.parseFloat(searchScore);

			int currentScoreNumi = Integer.parseInt(currentScoreNum);
			int searchScoreNumi = Integer.parseInt(searchScoreNum);

			if (currentScoref >= searchScoref && currentScoreNumi >= searchScoreNumi) {
				QiDianBookInfo qiDianBookInfo = new QiDianBookInfo();
				qiDianBookInfo.setScore(currentScore);
				qiDianBookInfo.setScoreNum(currentScoreNum);
				return qiDianBookInfo;
			}
			return null;
		} catch (Exception e) {
			Log.e("Test","解析失败："+url+",进行下一次");
			e.printStackTrace();
			return getBookScoreInfo(searchInfo,token,bookUrl,totalNum-1);
		}
	}

	public static QiDianBookInfo getBookDetailsInfo(SearchInfo searchInfo, QiDianBookInfo qiDianBookInfo, String bookUrl) throws IOException {
		if (searchInfo == null || qiDianBookInfo == null || StringUtils.isEmpty(bookUrl)) {
			return null;
		}
		//book.qidian.com/info/1005263115
		String url = StringUtils.getBookDetailsInfo(StringUtils.getBookId(bookUrl));
		if (StringUtils.isEmpty(url)) {
			return null;
		}
		HttpURLConnection conn = getConn(url,3);
		if (conn == null) {
			throw new IOException();
		}
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		String current = null;
		// whether already get max page
		boolean getMaxPage = false;
		while ((current = reader.readLine()) != null) {
			if (qiDianBookInfo.getBookName() == null && qiDianBookInfo.getAuthorName() == null) {
				QiDianRegexUtils.getQiDianBookNameAndAuthorName(qiDianBookInfo, current);
			}
			if (qiDianBookInfo.getLastChapter() == null && qiDianBookInfo.getLastUpdate() == null) {
				QiDianRegexUtils.getQiDianLastUpdateAndLastChapter(qiDianBookInfo, current);
			}
			if (qiDianBookInfo.getWordsNum() == null && qiDianBookInfo.getVipClick() == null && qiDianBookInfo.getRecommend() == null) {
				QiDianRegexUtils.getQiDianWordsNumVipClickRecommend(qiDianBookInfo, current);
			}
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (StringUtils.isWordsNumVipClickRecommendFit(searchInfo, qiDianBookInfo)) {
			Log.e("Test","书籍符合条件："+qiDianBookInfo.toString());
			return qiDianBookInfo;
		}
		return null;
	}


	
	/**
	 * get an available connection
	 * @param urlLink
	 * @return
	 */
	public static HttpURLConnection getConn(String urlLink,int totalNum) throws IOException {
		if (StringUtils.isEmpty(urlLink)) {
			Log.e("Test","empty url:"+urlLink);
			return null;
		}
		if(totalNum<=0){
			return null;
		}
		URL url = new URL(urlLink);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(20000);
		conn.setReadTimeout(20000);
		int code = conn.getResponseCode();
		if (code == 200) {
			return conn;
		} else if (code == 301 || code == 302) {
			String location = conn.getHeaderField("Location");
			System.out.println("location=" + location);
			url = new URL(location);
			conn = (HttpURLConnection) url.openConnection();
			code = conn.getResponseCode();
			if (code == 200) {
				return conn;
			}
			Log.e("Test","redirect code error url:"+code+",url="+urlLink);
			return getConn(urlLink,totalNum-1);
		}
		Log.e("Test","code error url:"+code+",url="+urlLink);
		return getConn(urlLink,totalNum-1);
	}
}
