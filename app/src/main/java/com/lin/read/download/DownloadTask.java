package com.lin.read.download;

import android.content.Context;
import android.os.AsyncTask;

import com.lin.read.filter.scan.StringUtils;
import com.lin.read.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private OnDownloadListener onDownloadListener;
    private String downloadUrl;
    private String bookId;
    private String savePath;
    private String status;
    private final String STATUS_PROGRESS_CHANGED = "STATUS_PROGRESS_CHANGED";
    private final String STATUS_SUCCESSFUL = "STATUS_SUCCESSFUL";
    private final String STATUS_FAILED = "STATUS_FAILED";

    private Context context;

    private long totalSize = 0;


    private void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public DownloadTask(Context context, String downloadUrl, String savePath, String bookId) {
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        this.bookId = bookId;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void[] params) {
        try {
            HttpURLConnection conn = HttpUtils.getConn(this.downloadUrl, 3);
            if (conn == null) {
                setListener(STATUS_FAILED);
                return null;
            }
            totalSize = conn.getContentLength();
            conn.disconnect();
            List<DownloadInfo> downloadInfos = splitFile(7);
            if (downloadInfos == null || downloadInfos.size() == 0) {
                setListener(STATUS_FAILED);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            setListener(STATUS_FAILED);
        }
        return null;
    }

    private void setListener(String... params) {
        if (this.onDownloadListener == null) {
            return;
        }
        if (params != null && params.length >= 1) {
            switch (params[0]) {
                case STATUS_PROGRESS_CHANGED:
                    if (params.length == 3) {
                        try {
                            this.onDownloadListener.onProgressChanged(Long.parseLong(params[1]), Long.parseLong(params[2]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case STATUS_SUCCESSFUL:
                    this.onDownloadListener.onDownloadSuccessful();
                    break;
                case STATUS_FAILED:
                    this.onDownloadListener.onDownloadFailed();
                    break;
                default:
                    break;
            }
        }
    }

    private List<DownloadInfo> splitFile(int threadCount) {
        if (threadCount < 1 || this.totalSize < 1) {
            return null;
        }
        List<DownloadInfo> downloadInfos = new ArrayList<>();
        long block = (this.totalSize % threadCount == 0) ? (this.totalSize / threadCount) : (this.totalSize / threadCount + 1);
        for (int i = 0; i < threadCount; i++) {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setThreadid(i);
            downloadInfo.setSize(block);
            downloadInfo.setSavePath(this.bookId + File.separator + threadCount + ".temp");
            downloadInfo.setDownloadurl(this.downloadUrl);
            if (i != threadCount - 1) {
                downloadInfo.setStartPosition(i * block);
                downloadInfo.setEndPosition((i + 1) * block - 1);
            } else {
                downloadInfo.setStartPosition(i * block);
                downloadInfo.setEndPosition(this.totalSize - 1);
            }
            downloadInfos.add(downloadInfo);
        }
        return downloadInfos;
    }

    ExecutorService downloadService;

    private void startDownload(List<DownloadInfo> downloadInfos) {
        if (downloadInfos == null || downloadInfos.size() == 0) {
            return;
        }
        downloadService = Executors.newFixedThreadPool(10);
        for (DownloadInfo downloadInfo : downloadInfos) {
            downloadService.execute(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }


    private boolean saveFile(DownloadInfo downloadInfo) {
        if (downloadInfo == null) {
            return false;
        }
        FileOutputStream fout = null;
        InputStream in = null;
        try {
            HttpURLConnection conn = HttpUtils.getConn(downloadInfo.getDownloadurl(), 3, downloadInfo.getStartPosition(), downloadInfo.getEndPosition());
            String externalPath = FileUtils.getExternalDir(context);
            if (StringUtils.isEmpty(externalPath) || StringUtils.isEmpty(downloadInfo.getSavePath())) {
                return false;
            }
            File saveFile = new File(externalPath + File.separator + downloadInfo.getSavePath());
            if (saveFile.exists()) {
                saveFile.delete();
                saveFile.getParentFile().mkdirs();
                saveFile.createNewFile();
            }
            fout = new FileOutputStream(saveFile);
            byte[] bytes = new byte[2048];
            int len = 0;
            in = conn.getInputStream();

            synchronized (downloadInfo) {
                while ((len = in.read(bytes)) != -1) {
                    fout.write(bytes, 0, len);
                    downloadInfo.setDownloadProgress(downloadInfo.getDownloadProgress() + len);
                    setListener(STATUS_PROGRESS_CHANGED, this.totalSize + "", len + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
