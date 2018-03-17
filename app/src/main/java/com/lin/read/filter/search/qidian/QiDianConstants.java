package com.lin.read.filter.search.qidian;
import com.lin.read.filter.search.KeyValuePair;
import com.lin.read.filter.search.ScanTypeInfo;

import java.util.ArrayList;
import java.util.List;


public class QiDianConstants {
	public static List<KeyValuePair> rankTypeList;

	public static List<KeyValuePair> dateTypeList;

	public static List<KeyValuePair> bookTypeList;

	public static List<ScanTypeInfo> scanWebTypeList;
	public static List<ScanTypeInfo> scanRankTypeList;
	public static List<ScanTypeInfo> scanBookTypeList;
	public static List<ScanTypeInfo> scanDateTypeList;

	public static final String WEB_QIDIAN="起点";
	public static final String WEB_ZONGHENG="纵横";
	public static final String WEB_17K="17k";

	public static final String RANK_YUE_PIAO="月票";
	public static final String RANK_HOT_SALE="热销";
	public static final String RANK_VIP_CLICK="会员";
	public static final String RANK_RECOMMEND="推荐";
	public static final String RANK_COLLECT="收藏";
	public static final String RANK_FINAL="完本";
	public static final String RANK_SIGN_NEW="签约";
	public static final String RANK_PUBLIC_NEW="公众";

	public static final String DATE_WEEK="周";
	public static final String DATE_MONTH="月";
	public static final String DATE_TOTALLY="总";

	public static final String BOOK_TOTALLY="全部";
	public static final String BOOK_XUAN_HUAN="玄幻";
	public static final String BOOK_QI_HUAN="奇幻";
	public static final String BOOK_WU_XIA="武侠";
	public static final String BOOK_XIAN_XIA="仙侠";
	public static final String BOOK_DU_SHI="都市";
	public static final String BOOK_XIAN_SHI="现实";
	public static final String BOOK_JUN_SHI="军事";
	public static final String BOOK_LI_SHI="历史";
	public static final String BOOK_YOU_XI="游戏";
	public static final String BOOK_TI_YU="体育";
	public static final String BOOK_KE_HUAN="科幻";
	public static final String BOOK_LING_YI="灵异";
	public static final String BOOK_ER_CI_YUAN="二次元";

	static{
		scanWebTypeList=new ArrayList<>();
		scanWebTypeList.add(new ScanTypeInfo(WEB_QIDIAN,false,"1"));
		scanWebTypeList.add(new ScanTypeInfo(WEB_ZONGHENG,false,"2"));
		scanWebTypeList.add(new ScanTypeInfo(WEB_17K,false,"3"));

		rankTypeList=new ArrayList<>();
		rankTypeList.add(new KeyValuePair(RANK_YUE_PIAO,"yuepiao"));
		rankTypeList.add(new KeyValuePair(RANK_HOT_SALE,"hotsales"));
		rankTypeList.add(new KeyValuePair(RANK_VIP_CLICK,"click"));
		rankTypeList.add(new KeyValuePair(RANK_RECOMMEND,"recom"));
		rankTypeList.add(new KeyValuePair(RANK_COLLECT,"collect"));
		rankTypeList.add(new KeyValuePair(RANK_FINAL,"fin"));
		rankTypeList.add(new KeyValuePair(RANK_SIGN_NEW,"signnewbook"));
		rankTypeList.add(new KeyValuePair(RANK_PUBLIC_NEW,"pubnewbook"));

		scanRankTypeList=new ArrayList<>();
		for(KeyValuePair item:rankTypeList){
			ScanTypeInfo typeInfo=new ScanTypeInfo();
			typeInfo.setChecked(false);
			typeInfo.setText(item.getKey());
			typeInfo.setId(""+item.getValue());
			scanRankTypeList.add(typeInfo);
		}

		dateTypeList=new ArrayList<>();
		dateTypeList.add(new KeyValuePair(DATE_WEEK,1));
		dateTypeList.add(new KeyValuePair(DATE_MONTH,2));
		dateTypeList.add(new KeyValuePair(DATE_TOTALLY,3));
		scanDateTypeList=new ArrayList<>();
		for(KeyValuePair item:dateTypeList){
			ScanTypeInfo typeInfo=new ScanTypeInfo();
			typeInfo.setChecked(false);
			typeInfo.setText(item.getKey());
			typeInfo.setId(""+item.getValue());
			scanDateTypeList.add(typeInfo);
		}

		bookTypeList=new ArrayList<>();
		bookTypeList.add(new KeyValuePair(BOOK_TOTALLY, -1));
		bookTypeList.add(new KeyValuePair(BOOK_XUAN_HUAN, 21));
		bookTypeList.add(new KeyValuePair(BOOK_QI_HUAN, 1));
		bookTypeList.add(new KeyValuePair(BOOK_WU_XIA, 2));
		bookTypeList.add(new KeyValuePair(BOOK_XIAN_XIA, 22));
		bookTypeList.add(new KeyValuePair(BOOK_DU_SHI, 4));
		bookTypeList.add(new KeyValuePair(BOOK_XIAN_SHI, 15));
		bookTypeList.add(new KeyValuePair(BOOK_JUN_SHI, 6));
		bookTypeList.add(new KeyValuePair(BOOK_LI_SHI, 5));
		bookTypeList.add(new KeyValuePair(BOOK_YOU_XI, 7));
		bookTypeList.add(new KeyValuePair(BOOK_TI_YU, 8));
		bookTypeList.add(new KeyValuePair(BOOK_KE_HUAN, 9));
		bookTypeList.add(new KeyValuePair(BOOK_LING_YI, 10));
		bookTypeList.add(new KeyValuePair(BOOK_ER_CI_YUAN, 12));

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
