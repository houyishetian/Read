package com.lin.read.download;

/**
 * Created by lisonglin on 2018/3/1.
 */

public class DownloadInfo {
    private int threadid;//线程id
    private long startPosition;//下载的起始位置
    private long endPosition;//下载的结束位置
    private long size;//大小
    private long downloadProgress;//该条线程已经下载的大小
    private String downloadurl;//下载地址
    private String savePath;//保存路径

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getThreadid() {
        return threadid;
    }

    public void setThreadid(int threadid) {
        this.threadid = threadid;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(long downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }
}
