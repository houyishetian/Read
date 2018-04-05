package com.lin.read.filter.search;

/**
 * Created by lisonglin on 2018/3/12.
 */

public class DownloadInfoFactory {

    public static final int BOOK_NOVAL_80 = 1;
    private static volatile DownloadInfoFactory instance;

    private DownloadInfoFactory(int type) {

    }

    public static DownloadInfoFactory getInstance(int type) {
        if (instance == null) {
            synchronized (DownloadInfoFactory.class) {
                if (instance == null) {
                    instance = new DownloadInfoFactory(type);
                    return instance;
                }
            }
        }
        return instance;
    }
}
