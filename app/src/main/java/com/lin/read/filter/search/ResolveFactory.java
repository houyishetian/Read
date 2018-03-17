package com.lin.read.filter.search;

import com.lin.read.filter.search.biquge.ResolveUtilsForBiQuGe;
import com.lin.read.filter.search.novel80.ResolveUtilsFor80;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class ResolveFactory {
    private static volatile ResolveFactory instance;

    public static ResolveFactory getInstance(int currentResolveType) {
        if (instance == null) {
            synchronized (ResolveFactory.class) {
                if (instance == null) {
                    instance = new ResolveFactory();
                    return instance;
                }
            }
        }
        return instance;
    }

    private ResolveFactory() {

    }

    private ResolveFactory(int currentResolveType) {
        this.currentResolveType = currentResolveType;
    }

    public static final int RESOLVE_FROM_NOVEL80 = 0;
    public static final int RESOLVE_FROM_BIQUGE = 1;
    private int currentResolveType;

    public ResolveUtils getResolveUtils() {
        ResolveUtils resolveUtils;
        switch (currentResolveType) {
            case RESOLVE_FROM_NOVEL80:
                resolveUtils = new ResolveUtilsFor80();
                break;
            case RESOLVE_FROM_BIQUGE:
                resolveUtils = new ResolveUtilsForBiQuGe();
                break;
            default:
                resolveUtils = null;
                break;
        }
        return resolveUtils;
    }
}
