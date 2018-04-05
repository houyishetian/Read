package com.lin.read.filter.scan;

/**
 * Created by lisonglin on 2017/10/12.
 */

public class ScanTypeInfo {
    private String text;
    private boolean checked;
    private String id;

    public ScanTypeInfo(String text, boolean checked, String id) {
        this.text = text;
        this.checked = checked;
        this.id = id;
    }

    public ScanTypeInfo() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ScanTypeInfo{" +
                "text='" + text + '\'' +
                ", checked=" + checked +
                ", id='" + id + '\'' +
                '}';
    }
}
