package com.lin.read.filter.search;

/**
 * Created by lisonglin on 2018/3/20.
 */

public class WebTypeBean {
    private String webName;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    @Override
    public String toString() {
        return "WebTypeBean{" +
                "webName='" + webName + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
