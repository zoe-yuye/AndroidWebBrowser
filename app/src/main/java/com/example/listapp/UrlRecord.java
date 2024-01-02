package com.example.listapp;

import android.graphics.Bitmap;

public class UrlRecord {
    public UrlRecord(String url, String title, Bitmap icon) {
        this.url = url;
        this.title = title;
        this.icon = icon;
    }

    private String url;
    private String title;
    private Bitmap icon;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
