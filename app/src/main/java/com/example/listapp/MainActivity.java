package com.example.listapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private Controller controller;
    private String defaultUrl;
    private int currentContentViewId;
    public void setCurrentContentViewId(int currentContentViewId) {
        this.currentContentViewId = currentContentViewId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int savedThemeId = sharedPreferences.getInt("themeId", R.style.Theme_ListApp);
        setTheme(savedThemeId);
        String savedUrl = sharedPreferences.getString("defaultUrl", "https://www.google.com");
        defaultUrl = savedUrl;
        super.onCreate(savedInstanceState);

        if(controller == null){
            controller = new Controller(this, defaultUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.historyItem:
                if(currentContentViewId != R.layout.history_page){
                    controller.setupHistoryScreen(defaultUrl);
                }
                break;
            case R.id.bookmarkItem:
                controller.setupBookmarkScreen(defaultUrl);
                break;
            case R.id.settingItem:
                controller.setupSettingScreen(defaultUrl);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}