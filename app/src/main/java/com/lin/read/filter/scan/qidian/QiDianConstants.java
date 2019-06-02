package com.lin.read.filter.scan.qidian;
import com.lin.read.filter.scan.ScanTypeInfo;

import java.util.ArrayList;
import java.util.List;


public class QiDianConstants {
	public static List<ScanTypeInfo> scanRankTypeList;
	public static List<ScanTypeInfo> scanBookTypeList;
	public static List<ScanTypeInfo> scanDateTypeList;

	public static final String QD_RANK_YUE_PIAO ="月票";
	public static final String QD_RANK_HOT_SALE ="热销";
	public static final String QD_RANK_RECOMMEND ="推荐";
	public static final String QD_RANK_COLLECT ="收藏";
	public static final String QD_RANK_SIGN_NEW ="签约";
	public static final String QD_RANK_PUBLIC_NEW ="公众";

	public static final String QD_DATE_WEEK ="周";
	public static final String QD_DATE_MONTH ="月";
	public static final String QD_DATE_TOTALLY ="总";

	public static final String QD_BOOK_TOTALLY ="全部";
	public static final String QD_BOOK_XUAN_HUAN ="玄幻";
	public static final String QD_BOOK_QI_HUAN ="奇幻";
	public static final String QD_BOOK_WU_XIA ="武侠";
	public static final String QD_BOOK_XIAN_XIA ="仙侠";
	public static final String QD_BOOK_DU_SHI ="都市";
	public static final String QD_BOOK_XIAN_SHI ="现实";
	public static final String QD_BOOK_JUN_SHI ="军事";
	public static final String QD_BOOK_LI_SHI ="历史";
	public static final String QD_BOOK_YOU_XI ="游戏";
	public static final String QD_BOOK_TI_YU ="体育";
	public static final String QD_BOOK_KE_HUAN ="科幻";
	public static final String QD_BOOK_LING_YI ="灵异";
	public static final String QD_BOOK_ER_CI_YUAN ="轻小说";

	static{
		scanRankTypeList=new ArrayList<>();
		scanRankTypeList.add(new ScanTypeInfo(QD_RANK_YUE_PIAO, false, "yuepiao"));
		scanRankTypeList.add(new ScanTypeInfo(QD_RANK_HOT_SALE, false, "hotsales"));
		scanRankTypeList.add(new ScanTypeInfo(QD_RANK_RECOMMEND, false, "recom"));
		scanRankTypeList.add(new ScanTypeInfo(QD_RANK_COLLECT, false, "collect"));
		scanRankTypeList.add(new ScanTypeInfo(QD_RANK_SIGN_NEW, false, "signnewbook"));
		scanRankTypeList.add(new ScanTypeInfo(QD_RANK_PUBLIC_NEW, false, "pubnewbook"));

		scanDateTypeList = new ArrayList<>();
		scanDateTypeList.add(new ScanTypeInfo(QD_DATE_WEEK, false, "" + 1));
		scanDateTypeList.add(new ScanTypeInfo(QD_DATE_MONTH, false, "" + 2));
		scanDateTypeList.add(new ScanTypeInfo(QD_DATE_TOTALLY, false, "" + 3));

		scanBookTypeList=new ArrayList<>();
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_TOTALLY, false, "" + -1));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_XUAN_HUAN, false, "" + 21));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_QI_HUAN, false, "" + 1));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_WU_XIA, false, "" + 2));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_XIAN_XIA, false, "" + 22));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_DU_SHI, false, "" + 4));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_XIAN_SHI, false, "" + 15));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_JUN_SHI, false, "" + 6));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_LI_SHI, false, "" + 5));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_YOU_XI, false, "" + 7));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_TI_YU, false, "" + 8));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_KE_HUAN, false, "" + 9));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_LING_YI, false, "" + 10));
		scanBookTypeList.add(new ScanTypeInfo(QD_BOOK_ER_CI_YUAN, false, "" + 12));
	}
}
