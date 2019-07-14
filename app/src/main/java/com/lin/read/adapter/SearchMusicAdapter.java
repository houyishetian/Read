package com.lin.read.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lin.read.R;
import com.lin.read.activity.SearchMusicActivity;
import com.lin.read.music.search.GetVKeyTask;
import com.lin.read.music.search.MusicLinkUtil;
import com.lin.read.music.search.bean.seachresult.MusicDataSongItemBean;
import com.lin.read.music.search.bean.seachresult.MusicSingerBean;
import com.lin.read.music.search.bean.vkey.VKeyBean;
import com.lin.read.view.DialogUtil;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2017/10/17.
 */

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.ViewHolder> {
    private ArrayList<MusicDataSongItemBean> allMusicData;
    private Context context;

    public SearchMusicAdapter(Context context, ArrayList<MusicDataSongItemBean> allMusicData) {
        this.allMusicData = allMusicData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_music, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MusicDataSongItemBean musicData = allMusicData.get(position);
        holder.musicName.setText(musicData.getName());
        String singers = getSingers(musicData);
        holder.singerName.setText(singers == null ? "--" : singers);
        holder.musicDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(musicData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allMusicData == null ? 0 : allMusicData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView musicName;
        private TextView singerName;
        private View musicDownload;

        public ViewHolder(View itemView) {
            super(itemView);
            musicName = itemView.findViewById(R.id.music_name);
            singerName = itemView.findViewById(R.id.music_singer);
            musicDownload = itemView.findViewById(R.id.music_download);
        }
    }

    private String getSingers(MusicDataSongItemBean musicData) {
        try {
            ArrayList<MusicSingerBean> singers = musicData.getSinger();
            StringBuilder result = new StringBuilder();
            for (MusicSingerBean item : singers) {
                result.append(item.getName()).append("、");
            }
            if (result.length() > 0) {
                result.deleteCharAt(result.length() - 1);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onClickItem(final MusicDataSongItemBean musicData) {
        DialogUtil.getInstance().showLoadingDialog(context);
        new GetVKeyTask(new GetVKeyTask.Listener() {
            @Override
            public void complete(final VKeyBean vKeyBean) {
                ((SearchMusicActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DialogUtil.getInstance().hideLoadingView();
                            String media_mid = musicData.getFile().getMedia_mid();
                            String vKey = vKeyBean.getData().getItems().get(0).getVkey();
                            String downloadLink = MusicLinkUtil.getDownloadUrl(media_mid,vKey);
                            if(downloadLink!=null){
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(downloadLink);
                                intent.setData(content_url);
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).execute();
    }
}
