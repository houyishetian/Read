package com.lin.read.utils;

import android.content.Context;
import android.os.Environment;

import com.lin.read.R;
import com.lin.read.filter.scan.StringUtils;

import java.io.File;

/**
 * Created by lisonglin on 2018/3/1.
 */

public class FileUtils {
    public static boolean clearTemp(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File bookDir = new File(path);
        if (!bookDir.exists()) {
            return true;
        }
        if (bookDir.isDirectory()) {
            File[] itemFiles = bookDir.listFiles();
            boolean result = true;
            for (File itemFile : itemFiles) {
                boolean current = clearTemp(itemFile.getAbsolutePath());
                result = result && current;
            }
            if (result) {
                return bookDir.delete();
            }
            return false;
        } else {
            return bookDir.delete();
        }
    }

    public static String getExternalDir(Context context){
        File externalFile=Environment.getExternalStorageDirectory();
        if (externalFile == null) {
            return null;
        }
        return externalFile.getAbsolutePath()+File.separator+context.getString(R.string.app_name);
    }
}
