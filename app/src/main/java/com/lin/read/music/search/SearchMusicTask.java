package com.lin.read.music.search;

import android.os.AsyncTask;
import com.lin.read.music.search.bean.seachresult.MusicDataSongBean;

public class SearchMusicTask extends AsyncTask<String, Void, MusicDataSongBean> {

    private Listener listener;

    public SearchMusicTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected MusicDataSongBean doInBackground(String[] objects) {
        if (objects == null || objects.length <= 0) {
            return null;
        }
        String searchKey = objects[0];
        return MusicHttpUtil.searchMusic(searchKey);
    }

    @Override
    protected void onPostExecute(MusicDataSongBean songBean) {
        super.onPostExecute(songBean);
        if (listener != null) {
            listener.complete(songBean);
        }
    }

    public interface Listener {
        void complete(MusicDataSongBean songBean);
    }
}
