package com.lin.read.pinyin;

import android.util.Log;

import com.lin.read.filter.scan.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/22.
 */

public class PinYinUtils {
    public static final String ERROR_INPUT_ERROR = "ERROR_INPUT_NULL";
    public static final String ERROR_PARSE_FAILED = "ERROR_PARSE_FAILED";

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失
     * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen
     * ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param nameChar 汉字
     * @return 拼音
     */
    private static List<String> converterToSpell(char nameChar) throws Exception {
        List<String> result = new ArrayList<>();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        if (nameChar > 128) {
            try {
                // 取得当前汉字的所有全拼
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(
                        nameChar, defaultFormat);
                if (strs != null && strs.length > 0) {
                    for (String itemStr : strs) {
                        String item = itemStr.toLowerCase();
                        if (!result.contains(item)) {
                            result.add(item);
                        }
                    }
                    return result;
                } else {
                    throw new Exception(ERROR_PARSE_FAILED);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
                throw new Exception(ERROR_PARSE_FAILED);
            }
        } else {
            Log.e("Test", nameChar + " not a chinest");
        }
        result.add(String.valueOf(nameChar));
        return result;
    }

    public static List<String> getAllPinYin(String chinese) throws Exception {
        if (StringUtils.isEmpty(chinese)) {
            throw new Exception(ERROR_INPUT_ERROR);
        }
        List<List<String>> parseResult = new ArrayList<>();
        char[] nameChar = chinese.toCharArray();
        for (char item:nameChar) {
            List<String> currentResult = converterToSpell(item);
            if(currentResult==null||currentResult.size()==0){
                throw new Exception(ERROR_PARSE_FAILED);
            }else{
                parseResult.add(currentResult);
            }
        }
        List<String> currentGroup = parseResult.get(0);
        for (int i = 1; i < parseResult.size(); i++) {
            List<String> tempResult = new ArrayList<>();
            for (String currentGroupItem : currentGroup) {
                for (String currentParseItem : parseResult.get(i)) {
                    tempResult.add(currentGroupItem+currentParseItem);
                }
            }
            currentGroup.clear();
            currentGroup.addAll(tempResult);
        }
        return currentGroup;
    }
}
