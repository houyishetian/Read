package com.lin.read.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.search.WebTypeBean;
import com.lin.read.utils.NoDoubleClickListener;

import java.util.List;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class DialogWebTypeAdapter extends BaseAdapter {
    private List<WebTypeBean> allInfo;
    private Context context;

    public DialogWebTypeAdapter(Context context, List<WebTypeBean> allInfo) {
        this.allInfo = allInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.allInfo == null ? 0 : allInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_web_dialog, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final WebTypeBean info = allInfo.get(position);
        holder.name.setText(info.getWebName());
        if (position == allInfo.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        holder.name.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(info);
                }
            }
        });
        return convertView;
    }

    public interface OnItemWebClickListener {
        void onItemClick(WebTypeBean info);
    }

    private OnItemWebClickListener onItemClickListener;

    public void setOnItemWebClickListener(OnItemWebClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder {
        private TextView name;
        private View divider;

        public ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.dialog_web_name);
            divider = itemView.findViewById(R.id.dialog_web_divider);
        }
    }
}
