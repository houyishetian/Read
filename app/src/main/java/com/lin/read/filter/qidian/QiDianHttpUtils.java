package com.lin.read.filter.qidian;

import com.lin.read.filter.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QiDianHttpUtils {
	public static String getToken() {
		String url = "https://www.qidian.com/";
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		HttpURLConnection conn = getConn(url);
		if (url != null) {
			CookieStore cookieJar = manager.getCookieStore();
			List<HttpCookie> cookies = cookieJar.getCookies();
			for (HttpCookie cookie : cookies) {
				System.out.println(cookie);
			}
		}
		return null;
	}

	/**
	 * get max page num and book info url from rank page
	 * @return 0--max page num
	 */
	public static List<String> getMaxPageAndBookInfoFromRankPage(String rankType,String bookType,int page) {
		String urlLink= StringUtils.getRankPageUrlByTypeAndPage(rankType, bookType, page);
		HttpURLConnection conn = getConn(urlLink);
		List<String> maxPageAndBookUrlList = new ArrayList<String>();
		BufferedReader reader = null;
		if (conn != null) {
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

	/**
	 * get max page num and book info url from rank page
	 * @param urlLink
	 * @return 0--max page num
	 */
	public static List<String> getLatestBookInfoFromRankPage(String urlLink) {
		HttpURLConnection conn = getConn(urlLink);
		List<String> maxPageAndBookUrlList = new ArrayList<String>();
		BufferedReader reader = null;
		if (conn != null) {
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
	
	/**
	 * get an available connection
	 * @param urlLink
	 * @return
	 */
	public static HttpURLConnection getConn(String urlLink) {
		if(StringUtils.isEmpty(urlLink)){
			return null;
		}
		try {
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
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
