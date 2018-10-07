package com.lin.read.music.search;

import android.os.AsyncTask;
import com.lin.read.music.search.bean.vkey.VKeyBean;

public class GetVKeyTask extends AsyncTask<String, Void, VKeyBean> {
    private Listener listener;

    public GetVKeyTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected VKeyBean doInBackground(String[] objects) {
        VKeyBean result = MusicHttpUtil.getVKey();
        return result;
    }

    @Override
    protected void onPostExecute(VKeyBean vKeyBean) {
        super.onPostExecute(vKeyBean);
        if (listener != null) {
            listener.complete(vKeyBean);
        }
    }

    public interface Listener {
        void complete(VKeyBean vKeyBean);
    }
}
