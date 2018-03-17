package com.lin.read.download;

public interface OnDownloadListener {
	void onProgressChanged(long totalSize, long currentSizeUpdate);
	void onDownloadSuccessful();
	void onDownloadFailed();
}
