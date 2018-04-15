package com.lin.read.filter.scan.zongheng;

import com.lin.read.filter.scan.ScanTypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/4/15.
 */

public class ZongHengConstants {
    public static List<ScanTypeInfo> scanRankTypeList;
    //月票榜 点击榜 新书榜 红票榜 黑票榜 捧场榜 潜力大作榜 今日畅销榜 新书订阅榜 热门作品更新榜
    public static final String ZH_RANK_YUE_PIAO="月票";
    public static final String ZH_RANK_CLICK="点击";
    public static final String ZH_RANK_NEW_BOOK="新书";
    public static final String ZH_RANK_HONG_PIAO="红票";
    public static final String ZH_RANK_HEI_PIAO="黑票";
    public static final String ZH_RANK_PENG_CHANG="捧场";
    public static final String ZH_RANK_QIAN_LI="潜力";
    public static final String ZH_RANK_CHANG_XIAO="畅销";
    public static final String ZH_RANK_DING_YUE="订阅";
    public static final String ZH_RANK_RE_MEN="热门";

    //分类： 全部 奇幻玄幻 武侠仙侠 历史军事 都市娱乐 竞技同人 科幻游戏 悬疑灵异
    public static List<ScanTypeInfo> scanBookTypeList;
    public static final String ZH_BOOK_ALL="全部";
    public static final String ZH_BOOK_QI_HUAN="奇幻";
    public static final String ZH_BOOK_XIAN_XIA="仙侠";
    public static final String ZH_BOOK_JUN_SHI="军事";
    public static final String ZH_BOOK_DU_SHI="都市";
    public static final String ZH_BOOK_TONG_REN="同人";
    public static final String ZH_BOOK_KE_HUAN="科幻";
    public static final String ZH_BOOK_XUAN_YI="悬疑";

    //百度小说月票榜 潜力月票榜
    public static List<ScanTypeInfo> scanYuePiaoSubList;
    public static final String ZH_YUE_PIAO_BAIDU="百度";
    public static final String ZH_YUE_PIAO_QIAN_LI="潜力";

    //日 周 月
    public static List<ScanTypeInfo> scanClickDateTypeList;

    //日 周 月
    public static List<ScanTypeInfo> scanHongPiaoDateTypeList;

    //日 周 月
    public static List<ScanTypeInfo> scanHeiPiaoDateTypeList;

    //日 周 月
    public static List<ScanTypeInfo> scanReMenDateTypeList;
    
    public static final String DATE_DAY="日";
    public static final String DATE_WEEK="周";
    public static final String DATE_MONTH="月";

    static{
        //月票榜 点击榜 新书榜 红票榜 黑票榜 捧场榜 潜力大作榜 今日畅销榜 新书订阅榜 热门作品更新榜
        scanRankTypeList=new ArrayList<>();
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_YUE_PIAO, false, "r1"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_CLICK, false, "r4"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_NEW_BOOK, false, "r14"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_HONG_PIAO, false, "r7"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_HEI_PIAO, false, "r10"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_PENG_CHANG, false, "r133"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_QIAN_LI, false, "r12"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_CHANG_XIAO, false, "r2"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_DING_YUE, false, "r13"));
        scanRankTypeList.add(new ScanTypeInfo(ZH_RANK_RE_MEN, false, "r16"));

        //百度小说月票榜 潜力月票榜
        scanYuePiaoSubList = new ArrayList<>();
        scanYuePiaoSubList.add(new ScanTypeInfo(ZH_YUE_PIAO_BAIDU, false, "c0"));
        scanYuePiaoSubList.add(new ScanTypeInfo(ZH_YUE_PIAO_QIAN_LI, false, "c19"));

        //分类： 全部 奇幻玄幻 武侠仙侠 历史军事 都市娱乐 竞技同人 科幻游戏 悬疑灵异
        scanBookTypeList =new ArrayList<>();
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_ALL,false,"c0"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_QI_HUAN,false,"c1"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_XIAN_XIA,false,"c3"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_JUN_SHI,false,"c6"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_DU_SHI,false,"c9"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_TONG_REN,false,"c21"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_KE_HUAN,false,"c15"));
        scanBookTypeList.add(new ScanTypeInfo(ZH_BOOK_XUAN_YI,false,"c18"));

        scanClickDateTypeList = new ArrayList<>();
        scanClickDateTypeList.add(new ScanTypeInfo(DATE_DAY, false, "r3"));
        scanClickDateTypeList.add(new ScanTypeInfo(DATE_WEEK, false, "r4"));
        scanClickDateTypeList.add(new ScanTypeInfo(DATE_MONTH, false, "r5"));

        scanHongPiaoDateTypeList = new ArrayList<>();
        scanHongPiaoDateTypeList.add(new ScanTypeInfo(DATE_DAY, false, "r6"));
        scanHongPiaoDateTypeList.add(new ScanTypeInfo(DATE_WEEK, false, "r7"));
        scanHongPiaoDateTypeList.add(new ScanTypeInfo(DATE_MONTH, false, "r8"));

        scanHeiPiaoDateTypeList = new ArrayList<>();
        scanHeiPiaoDateTypeList.add(new ScanTypeInfo(DATE_DAY, false, "r9"));
        scanHeiPiaoDateTypeList.add(new ScanTypeInfo(DATE_WEEK, false, "r10"));
        scanHeiPiaoDateTypeList.add(new ScanTypeInfo(DATE_MONTH, false, "r11"));

        scanReMenDateTypeList = new ArrayList<>();
        scanReMenDateTypeList.add(new ScanTypeInfo(DATE_DAY, false, "r15"));
        scanReMenDateTypeList.add(new ScanTypeInfo(DATE_WEEK, false, "r16"));
        scanReMenDateTypeList.add(new ScanTypeInfo(DATE_MONTH, false, "r17"));
    }
}
