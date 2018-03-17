package com.lin.read.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.search.ScanTypeInfo;

import java.util.List;

/**
 * Created by lisonglin on 2017/10/12.
 */

public class ScanTypeAdapter extends RecyclerView.Adapter<ScanTypeAdapter.ViewHolder> {
    private Context context;
    private List<ScanTypeInfo> data;

    private boolean isHold = false;

    public ScanTypeAdapter(Context context, List<ScanTypeInfo> data) {
        this.context = context;
        this.data = data;
        setDefaultChecked("");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_type, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        isHold = true;
        final ScanTypeInfo typeInfo = data.get(position);
        holder.text.setText(typeInfo.getText());
        holder.checkBox.setChecked(typeInfo.isChecked());
        isHold = false;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.scan_type_text);
            checkBox = (CheckBox) itemView.findViewById(R.id.scan_type_checkbox);

            checkBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (checkBox.isChecked()) {
                        return true;
                    }
                    return false;
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isHold) {
                        if(isChecked){
                            if(onRankTypeItemClickListener!=null){
                                onRankTypeItemClickListener.onItemClick(text.getText().toString());
                            }
                        }
                        setCheckedStatus(text.getText().toString());
                    }
                }
            });
        }
    }

    private void setCheckedStatus(String text) {
        if (data == null || data.size() == 0 || TextUtils.isEmpty(text)) {
            return;
        }
        for (ScanTypeInfo item : data) {
            if (text.equals(item.getText())) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    public void setDefaultChecked(String text) {
        if (data == null || data.size() == 0) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            for (int i = 0; i < data.size(); i++) {
                if (i == 0) {
                    data.get(i).setChecked(true);
                } else {
                    data.get(i).setChecked(false);
                }
            }
            return;
        }
        for (ScanTypeInfo item : data) {
            if (text.equals(item.getText())) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
    }

    public ScanTypeInfo getCheckedInfo() {
        if (data == null || data.size() == 0) {
            return null;
        }
        for (ScanTypeInfo item : data) {
            if (item.isChecked()) {
                return item;
            }
        }
        return null;
    }

    public String getCheckedText() {
        if (data == null || data.size() == 0) {
            return null;
        }
        for (ScanTypeInfo item : data) {
            if (item.isChecked()) {
                return item.getText();
            }
        }
        return null;
    }

    public interface OnRankTypeItemClickListener{
        void onItemClick(String clickText);
    }

    private OnRankTypeItemClickListener onRankTypeItemClickListener;

    public void setOnRankTypeItemClickListener(OnRankTypeItemClickListener onRankTypeItemClickListener) {
        this.onRankTypeItemClickListener = onRankTypeItemClickListener;
    }
}
