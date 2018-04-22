package com.lin.read.filter.scan.zongheng;

import com.lin.read.filter.scan.ScanTypeInfo;
import com.lin.read.filter.scan.SearchInfo;
import com.lin.read.filter.scan.StringUtils;

/**
 * Created by lisonglin on 2018/4/15.
 */

public class ZongHengStringUtil {
    public static String getSearchUrl(SearchInfo searchInfo, int page) {
        ScanTypeInfo rankInfo = searchInfo.getRankInfo();
        ScanTypeInfo bookTypeInfo = searchInfo.getBookTypeInfo();
        ScanTypeInfo dateInfo = searchInfo.getDateInfo();

        if (rankInfo == null || StringUtils.isEmpty(rankInfo.getText()) || page <= 0) {
            return null;
        }
        //http://book.zongheng.com/ranknow/male/r1/c0/q0/1.html
        String url = "http://book.zongheng.com/ranknow/male/%s/%s/q0/%d.html";
        String param0 = null;
        String param1 = null;
        //月票榜 点击榜 新书榜 红票榜 黑票榜 捧场榜 潜力大作榜 今日畅销榜 新书订阅榜 热门作品更新榜
        switch (rankInfo.getText()) {
            case ZongHengConstants.ZH_RANK_YUE_PIAO:
                param0 = rankInfo.getId();
                param1 = bookTypeInfo.getId();
                break;
            case ZongHengConstants.ZH_RANK_CLICK:
                param0 = dateInfo.getId();
                param1 = bookTypeInfo.getId();
                break;
            case ZongHengConstants.ZH_RANK_NEW_BOOK:
                param0 = rankInfo.getId();
                param1 = bookTypeInfo.getId();
                break;
            case ZongHengConstants.ZH_RANK_HONG_PIAO:
            case ZongHengConstants.ZH_RANK_HEI_PIAO:
                param0 = dateInfo.getId();
                param1 = bookTypeInfo.getId();
                break;
            case ZongHengConstants.ZH_RANK_PENG_CHANG:
            case ZongHengConstants.ZH_RANK_QIAN_LI:
            case ZongHengConstants.ZH_RANK_CHANG_XIAO:
            case ZongHengConstants.ZH_RANK_DING_YUE:
                param0 = rankInfo.getId();
                param1 = "c0";
                break;
            case ZongHengConstants.ZH_RANK_RE_MEN:
                param0 = dateInfo.getId();
                param1 = "c0";
                break;
            default:
                return null;
        }
        return String.format(url, param0, param1,page);
    }
}
