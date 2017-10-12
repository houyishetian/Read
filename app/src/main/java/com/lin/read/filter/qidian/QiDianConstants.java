package com.lin.read.filter.qidian;
import com.lin.read.filter.KeyValuePair;
import com.lin.read.filter.ScanTypeInfo;

import java.util.ArrayList;
import java.util.List;


public class QiDianConstants {
	public static List<KeyValuePair> rankTypeList;

	public static List<KeyValuePair> dateTypeList;

	public static List<KeyValuePair> bookTypeList;

	public static List<ScanTypeInfo> scanWebTypeList;
	public static List<ScanTypeInfo> scanRankTypeList;
	public static List<ScanTypeInfo> scanBookTypeList;

	static{
		scanWebTypeList=new ArrayList<>();
		scanWebTypeList.add(new ScanTypeInfo("起点",false,"1"));
		scanWebTypeList.add(new ScanTypeInfo("纵横",false,"2"));
		scanWebTypeList.add(new ScanTypeInfo("17k",false,"3"));

		rankTypeList=new ArrayList<>();
		rankTypeList.add(new KeyValuePair("月票","yuepiao"));
		rankTypeList.add(new KeyValuePair("热销","hotsales"));
		rankTypeList.add(new KeyValuePair("会员","click"));
		rankTypeList.add(new KeyValuePair("推荐","recom"));
		rankTypeList.add(new KeyValuePair("收藏","collect"));
		rankTypeList.add(new KeyValuePair("完本","fin"));
		rankTypeList.add(new KeyValuePair("签约","signnewbook"));
		rankTypeList.add(new KeyValuePair("公众","pubnewbook"));

		scanRankTypeList=new ArrayList<>();
		for(KeyValuePair item:rankTypeList){
			ScanTypeInfo typeInfo=new ScanTypeInfo();
			typeInfo.setChecked(false);
			typeInfo.setText(item.getKey());
			typeInfo.setId(""+item.getValue());
			scanRankTypeList.add(typeInfo);
		}

		dateTypeList=new ArrayList<>();
		dateTypeList.add(new KeyValuePair("周",1));
		dateTypeList.add(new KeyValuePair("月",2));
		dateTypeList.add(new KeyValuePair("总",3));

		bookTypeList=new ArrayList<>();
		bookTypeList.add(new KeyValuePair("全部", -1));
		bookTypeList.add(new KeyValuePair("玄幻", 21));
		bookTypeList.add(new KeyValuePair("奇幻", 1));
		bookTypeList.add(new KeyValuePair("武侠", 2));
		bookTypeList.add(new KeyValuePair("仙侠", 22));
		bookTypeList.add(new KeyValuePair("都市", 4));
		bookTypeList.add(new KeyValuePair("现实", 15));
		bookTypeList.add(new KeyValuePair("军事", 6));
		bookTypeList.add(new KeyValuePair("历史", 5));
		bookTypeList.add(new KeyValuePair("游戏", 7));
		bookTypeList.add(new KeyValuePair("体育", 8));
		bookTypeList.add(new KeyValuePair("科幻", 9));
		bookTypeList.add(new KeyValuePair("灵异", 10));
		bookTypeList.add(new KeyValuePair("二次元", 12));

		scanBookTypeList=new ArrayList<>();
		for(KeyValuePair item:bookTypeList){
			ScanTypeInfo typeInfo=new ScanTypeInfo();
			typeInfo.setChecked(false);
			typeInfo.setText(item.getKey());
			typeInfo.setId(""+item.getValue());
			scanBookTypeList.add(typeInfo);
		}
	}
}
