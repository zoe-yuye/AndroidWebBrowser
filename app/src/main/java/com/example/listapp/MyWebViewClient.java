package com.example.listapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MyWebViewClient extends WebViewClient {

    ProgressBar progressBar;
    EditText input;
    Controller controller;
    public MyWebViewClient(ProgressBar progressBar, EditText input, Controller controller){
        this.progressBar = progressBar;
        this.input = input;
        this.controller = controller;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(favicon == null){
            favicon = BitmapFactory.decodeResource(view.getResources(), R.drawable.image);
        }
        super.onPageStarted(view, url, favicon);
        progressBar.setVisibility(View.VISIBLE);
        input.setText(view.getUrl());
        controller.setCurrentUrl(view.getUrl());
        controller.addHistoryRecord(new UrlRecord(view.getUrl(), view.getTitle(), view.getFavicon()));
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
