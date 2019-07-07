package com.lin.read.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lin.read.R;
import com.lin.read.filter.scan.SortInfo;
import com.lin.read.utils.NoDoubleClickListener;

import java.util.List;

/**
 * Created by lisonglin on 2018/4/22
 */

public class DialogSortTypeAdapter extends BaseAdapter {
    private List<SortInfo> allInfo;
    private Context context;

    public DialogSortTypeAdapter(Context context, List<SortInfo> allInfo) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_sort_type_dialog, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SortInfo info = allInfo.get(position);
        holder.name.setText(info.getSortText());
        if (position == allInfo.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        holder.name.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(info.getSortId());
                }
            }
        });
        return convertView;
    }

    public interface OnItemSortTypeClickListener {
        void onItemClick(int clickId);
    }

    private OnItemSortTypeClickListener onItemClickListener;

    public void setOnItemSortTypeClickListener(OnItemSortTypeClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder {
        private TextView name;
        private View divider;

        public ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.dialog_sort_name);
            divider = itemView.findViewById(R.id.dialog_sort_divider);
        }
    }
}
