package com.lin.read.filter.scan.qidian;

import android.text.TextUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.ScanInfo;
import com.lin.read.filter.search.RegexUtils;
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

	/**
	 * get max page num and book info url from rank page
	 * @return 0--max page num
	 */
//	public static List<Object> getMaxPageAndBookInfoFromRankPage(ScanInfo searchInfo, int page) throws IOException {
//		if(searchInfo==null){
//			return null;
//		}
//		String urlLink = StringKtUtil.Companion.getRequestUrlByScanInfo(searchInfo,page);
//		Log.e("Test","execute start:"+urlLink);
//		HttpURLConnection conn = HttpUtils.getConn(urlLink,3);
//		List<Object> maxPageAndBookUrlList = new ArrayList<Object>();
//		BufferedReader reader = null;
//		if(conn==null){
//			throw new IOException(Constants.EXCEPTION_GET_CONN_ERROR);
//		}else{
//			String unicodeType="UTF-8";
//			reader = new BufferedReader(new InputStreamReader(
//					conn.getInputStream(), unicodeType));
//			String current = null;
//			// whether already get max page
//			boolean getMaxPage = false;
//			int position=0;
//			while ((current = reader.readLine()) != null) {
//				String bookUrl = QiDianRegexUtils
//						.getBookUrlFromRankPage(current);
//				if (bookUrl != null) {
//					ScanBookBean scanBookBean=new ScanBookBean();
//					scanBookBean.setUrl(bookUrl);
//					scanBookBean.setPage(page);
//					scanBookBean.setPosition(position);
//					position++;
//					maxPageAndBookUrlList.add(scanBookBean);
//				} else {
//					if (!getMaxPage) {
//						String maxPage = QiDianRegexUtils
//								.getMaxPage(current);
//						if (maxPage != null) {
//							getMaxPage = true;
//							maxPageAndBookUrlList.add(0, maxPage);
//							break;
//						}
//					}
//				}
//			}
//			if (!getMaxPage) {
//				maxPageAndBookUrlList.add(0, null);
//			}
//			return maxPageAndBookUrlList;
//		}
//	}

//	/**
//	 * get max page num and book info url from rank page
//	 * @param urlLink
//	 * @return 0--max page num
//	 */
//	public static List<String> getLatestBookInfoFromRankPage(String urlLink) throws IOException {
//		HttpURLConnection conn = HttpUtils.getConn(urlLink,3);
//		List<String> maxPageAndBookUrlList = new ArrayList<String>();
//		BufferedReader reader = null;
//		if(conn==null){
//			throw new IOException(Constants.EXCEPTION_GET_CONN_ERROR);
//		}else {
//			try {
//				String unicodeType=StringUtils.getCharSet(conn);
//				reader = new BufferedReader(new InputStreamReader(
//						conn.getInputStream(), unicodeType));
//				String current = null;
//				// whether already get max page
//				boolean getMaxPage = false;
//				while ((current = reader.readLine()) != null) {
//					String bookUrl = QiDianRegexUtils
//							.getBookUrlFromRankPage(current);
//					if (bookUrl != null) {
//						maxPageAndBookUrlList.add(bookUrl);
//					} else {
//						if (!getMaxPage) {
//							String maxPage = QiDianRegexUtils
//									.getMaxPage(current);
//							if (maxPage != null) {
//								getMaxPage = true;
//								maxPageAndBookUrlList.add(0, maxPage);
//								break;
//							}
//						}
//					}
//				}
//				if (!getMaxPage) {
//					maxPageAndBookUrlList.add(0, null);
//				}
//				return maxPageAndBookUrlList;
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (reader != null) {
//					try {
//						reader.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
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

	public static BookInfo getBookDetailsInfo(ScanInfo searchInfo, Reader inputReader) throws IOException {
		BufferedReader reader= new BufferedReader(inputReader);
		String current = null;
		boolean isResolvingScore = false;
		BookInfo bookInfo = new BookInfo();
		while ((current = reader.readLine()) != null) {
			current = current.trim();
			/*resolve book last update start*/
			//<p class="gray ell" id="ariaMuLu" role="option">2小时前<span class="char-dot">·</span>连载至第717章 外来的尚</p>
			List<String> resolveLastUpdateResult = RegexUtils.getDataByRegex(current, "id=\"ariaMuLu\" role=\"option\">([^\n]{1,})<span class=\"char-dot\">·</span>连载至([^\n]{1,})</p>", Arrays.asList(1,2));
			if (resolveLastUpdateResult != null && resolveLastUpdateResult.size() != 0) {
				bookInfo.setLastUpdate(resolveLastUpdateResult.get(0));
				bookInfo.setLastChapter(resolveLastUpdateResult.get(1));
				continue;
			}

			/*resolve book last update end*/

			/*resolve book words num start*/
			//<p class="book-meta" role="option">182.22万字<span class="char-pipe">|</span>连载</p>
			List<String> resolveBookWordsNumResult = RegexUtils.getDataByRegex(current, "<p class=\"book-meta\" role=\"option\">([0-9.]{1,})万字", Arrays.asList(1));
			if (resolveBookWordsNumResult != null && resolveBookWordsNumResult.size() != 0) {
				bookInfo.setWordsNum(resolveBookWordsNumResult.get(0));
//				if(!StringUtils.isWordsNumFit(searchInfo,bookInfo)){
//					return null;
//				}
				continue;
			}
			/*resolve book words num end*/

			/*resolve book author start*/
			//<a href="/author/4362633" role="option"><aria>作者：</aria>志鸟村<aria>级别：</aria>
			List<String> resolveBookAuthorResult = RegexUtils.getDataByRegex(current, "role=\"option\"><aria>作者：</aria>([^\n^<]{1,})<aria>", Arrays.asList(1));
			if (resolveBookAuthorResult != null && resolveBookAuthorResult.size() != 0) {
				bookInfo.setAuthorName(resolveBookAuthorResult.get(0));
				continue;
			}
			/*resolve book author end*/

			/*resolve book name start*/
			//<h2 class="book-title">大医凌然</h2>
			List<String> resolveBookNameResult = RegexUtils.getDataByRegexMatch(current, "<h2 class=\"book-title\">([^\n^<]{1,})</h2>", Arrays.asList(1));
			if (resolveBookNameResult != null && resolveBookNameResult.size() != 0) {
				bookInfo.setBookName(resolveBookNameResult.get(0));
				continue;
			}
			/*resolve book name end*/

			/*resolve score start*/
			if(!isResolvingScore && current.equals("<div class=\"book-score\" role=\"option\">")){
				isResolvingScore = true;
				continue;
			}
			if(isResolvingScore){
				//<span class="gray">9分/618人评过</span>
				List<String> resolveScoreResult = RegexUtils.getDataByRegexMatch(current, "<span class=\"gray\">([0-9.]{1,})分/([0-9]{1,})人评过</span>", Arrays.asList(1,2));
				if (resolveScoreResult != null && resolveScoreResult.size() != 0) {
					bookInfo.setScore(resolveScoreResult.get(0));
					bookInfo.setScoreNum(resolveScoreResult.get(1));
					continue;
				}
				if(current.equals("</div>")){
					isResolvingScore = false;
					if(TextUtils.isEmpty(bookInfo.getScore())){
						bookInfo.setScore("0");
					}
					if(TextUtils.isEmpty(bookInfo.getScoreNum())){
						bookInfo.setScoreNum("0");
					}
//					if(!StringUtils.isScoreAndScoreNumFie(searchInfo,bookInfo)){
//						return null;
//					}
				}
			}
			/*resolve score end*/
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(StringKtUtil.Companion.compareFilterInfo(searchInfo,bookInfo)) return bookInfo;
		return null;
	}
}
