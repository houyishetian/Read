package com.lin.read.filter.scan.qidian;

import android.text.TextUtils;
import com.lin.read.filter.scan.ScanInfo;
import com.lin.read.utils.StringKtUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class QiDianHttpUtils {
//	public static final String TOKEY_KEY="_csrfToken";
//	public static String getToken() throws IOException {
////		String url = "https://www.qidian.com/";
//		String url = "https://book.qidian.com/info/1004608738";
//		CookieManager manager = new CookieManager();
//		CookieHandler.setDefault(manager);
//		HttpURLConnection conn = HttpUtils.getConn(url,3);
//		if(conn==null){
//			throw new IOException(Constants.EXCEPTION_GET_CONN_ERROR);
//		}
//		conn=HttpUtils.getConn(url,3);
//		if(conn==null){
//			throw new IOException(Constants.EXCEPTION_GET_CONN_ERROR);
//		}
//		CookieStore cookieJar = manager.getCookieStore();
//		List<HttpCookie> cookies = cookieJar.getCookies();
//		for (HttpCookie cookie : cookies) {
//			if(TOKEY_KEY.equals(cookie.getName())){
//				return cookie.getValue();
//			}
//		}
//		return null;
//	}
//	public static BookInfo getBookScoreInfo(SearchInfo searchInfo, String token, String bookUrl, int totalNum) throws IOException {
//		//book.qidian.com/info/1005263115
//		if(totalNum<=0){
//			return null;
//		}
//		String url=StringUtils.getBookScoreUrl(token,StringUtils.getBookId(bookUrl));
//		if(StringUtils.isEmpty(url)){
//			return null;
//		}
//		HttpURLConnection conn=HttpUtils.getConn(url,3);
//		if(conn==null){
//			throw new IOException(Constants.EXCEPTION_GET_CONN_ERROR);
//		}
//
////		{
////			"data": {
////			"rate": 9.2,
////					"userCount": 16,
////					"iRated": 0,
////					"totalCnt": 16,
////					"pageMax": 2,
////					"iRateStar": 0,
////					"pageIndex": 1,
////					"commentInfo": [
////			{
////				"userId": 228492547,
////					"rateId": 70696,
////					"star": 5,
////					"comment": "",
////					"like": 0,
////					"time": "2017-09-27 22:26",
////					"userIcon": "//facepic.qidian.com/qd_face/349573/0/50",
////					"nickName": "书友20170118204818925",
////					"fanLevel": 0
////			}
////        ]
////		},
////			"code": 0,
////				"msg": "suc"
////		}
//
//		String content = StringUtils.getAllContentString(conn);
//		ScoreJson scoreJson = new GsonBuilder().create().fromJson(content, ScoreJson.class);
//		try {
//			String currentScore = scoreJson.getData().getRate();
//			String currentScoreNum = scoreJson.getData().getUserCount();
//			String searchScore = searchInfo.getScore();
//			String searchScoreNum = searchInfo.getScoreNum();
//
//			float currentScoref = Float.parseFloat(currentScore);
//			float searchScoref = Float.parseFloat(searchScore);
//
//			int currentScoreNumi = Integer.parseInt(currentScoreNum);
//			int searchScoreNumi = Integer.parseInt(searchScoreNum);
//
//			if (currentScoref >= searchScoref && currentScoreNumi >= searchScoreNumi) {
//				BookInfo bookInfo = new BookInfo();
//				bookInfo.setScore(currentScore);
//				bookInfo.setScoreNum(currentScoreNum);
//				return bookInfo;
//			}
//			return null;
//		} catch (Exception e) {
//			Log.e("Test","解析失败："+url+",进行下一次");
//			e.printStackTrace();
//			return getBookScoreInfo(searchInfo,token,bookUrl,totalNum-1);
//		}
//	}
//
}
