package com.lin.read.filter;

import android.util.Log;

import com.lin.read.filter.qidian.QiDianConstants;

import java.util.List;

public class StringUtils {

	private static String baseRankPage = "https://www.qidian.com/rank/%s?chn=%d&page=%d";
	public static final String INPUTTYPE_FLOAT="INPUTTYPE_FLOAT";
	public static final String INPUTTYPE_INTEGER="INPUTTYPE_INTEGER";

	public static boolean isEmpty(String data) {
		return data == null || data.length() == 0;
	}

	public static String getRankPageUrlByTypeAndPage(String rankTypeKey,
			String bookTypeKey, int page) {
		if (isEmpty(rankTypeKey) || isEmpty(bookTypeKey) || page <= 0) {
			return null;
		}
		try {
			String rankType = (String) getType(QiDianConstants.rankTypeList,
					rankTypeKey);
			int bookType = (int) getType(QiDianConstants.bookTypeList,
					bookTypeKey);
			return String.format(baseRankPage, rankType, bookType, page);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getType(List<KeyValuePair> typeList, String typeKey) {
		if (typeList == null || typeList.size() == 0 || isEmpty(typeKey)) {
			return null;
		}
		for (KeyValuePair item : typeList) {
			if (typeKey.equals(item.getKey())) {
				return item.getValue();
			}
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

}
