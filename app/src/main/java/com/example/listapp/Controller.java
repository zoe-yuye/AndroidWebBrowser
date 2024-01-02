package com.example.listapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.util.Stack;

public class Controller {
    private MainActivity activity;
    private Stack<UrlRecord> historyRecords;
    private Stack<UrlRecord> bookmarks;
    private String currentUrl;
    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    private WebView myWebView;

    public Controller(MainActivity activity, String defaultUrl){
        this.activity = activity;
        historyRecords = new Stack<>();
        bookmarks = new Stack<>();
        setupHomeScreen(defaultUrl);
    }

    public void setupHomeScreen(String defaultUrl){
        activity.setContentView(R.layout.activity_main);
        activity.setCurrentContentViewId(R.layout.activity_main);

        handleUrl(defaultUrl);

        ImageButton goBackBtn = activity.findViewById(R.id.backward);
        goBackBtn.setOnClickListener((view -> {
            if (myWebView.canGoBack()){
                myWebView.goBack();
            }
        }));

        ImageButton goForwardBtn = activity.findViewById(R.id.forward);
        goForwardBtn.setOnClickListener((view -> {
            if (myWebView.canGoForward()){
                myWebView.goForward();
            }
        }));

        ImageButton reloadBtn = activity.findViewById(R.id.reload);
        reloadBtn.setOnClickListener((view -> {
            myWebView.reload();
        }));

        ImageButton markBtn = activity.findViewById(R.id.mark);
        markBtn.setOnClickListener((view -> {
            bookmarks.add(new UrlRecord(myWebView.getUrl(),myWebView.getTitle(),myWebView.getFavicon()));
            Toast.makeText(activity, "Add a Bookmark", Toast.LENGTH_SHORT).show();
        }));
        ImageButton homeBtn = activity.findViewById(R.id.home);
        homeBtn.setOnClickListener((view -> {
            setupHomeScreen(defaultUrl);
        }));

        ImageButton historyBtn = activity.findViewById(R.id.history);
        historyBtn.setOnClickListener((view -> {
            setupHistoryScreen(defaultUrl);
        }));

        ImageButton bookmarkBtn = activity.findViewById(R.id.bookmark);
        bookmarkBtn.setOnClickListener((view -> {
            setupBookmarkScreen(defaultUrl);
        }));

        ImageButton tabBtn = activity.findViewById(R.id.setting);
        tabBtn.setOnClickListener((view -> {
            setupSettingScreen(defaultUrl);
        }));

    }

    public void handleUrl(String defaultUrl){
        EditText input = (EditText) activity.findViewById(R.id.urlInput);
        ProgressBar progressBar = activity.findViewById(R.id.progressBar);
        myWebView = activity.findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        myWebView.loadUrl(defaultUrl);

        myWebView.setWebViewClient(new MyWebViewClient(progressBar, input, this));
        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }

        });

        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("INPUT","url entered: "+input.getText());
                    String url = input.getText().toString();
                    if(Patterns.WEB_URL.matcher(url).matches()){
                        myWebView.loadUrl(url);
                    }else{
                        url = "https://www.google.com/search?q=" + input.getText();
                        myWebView.loadUrl(url);
                    }
                    input.setText("");
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void setupHistoryScreen(String defaultUrl){
        clearHistoryScreen(defaultUrl);
        loadHistory();
    }
    public void clearHistoryScreen(String defaultUrl){
        activity.setContentView(R.layout.history_page);
        activity.setCurrentContentViewId(R.layout.history_page);
        ImageButton homeBtn = activity.findViewById(R.id.home_history);

        homeBtn.setOnClickListener((view -> {
            setupHomeScreen(defaultUrl);
        }));

        ImageButton backBtn = activity.findViewById(R.id.back_history);

        backBtn.setOnClickListener((view -> {
            setupHomeScreen(currentUrl);
        }));

        ImageButton clearBtn = activity.findViewById(R.id.clearHistory);

        clearBtn.setOnClickListener((view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("Clear History");
            alertDialogBuilder.setMessage("Do you wish to clear all the history?");

            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("history_records");
                    myWebView.clearHistory();
                    historyRecords.clear();
                    clearHistoryScreen(defaultUrl);
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }));

    }
    public void addHistoryRecord(UrlRecord historyRecord){
        historyRecords.add(historyRecord);
    }
    public void loadHistory(){
        ListView listView = activity.findViewById(R.id.historyRecords);
        MyAdapter myAdapter = new MyAdapter(activity.getApplicationContext(),historyRecords,this);
        listView.setAdapter(myAdapter);
    }
    public void setupBookmarkScreen(String defaultUrl){
        activity.setContentView(R.layout.bookmark_page);
        activity.setCurrentContentViewId(R.layout.bookmark_page);

        ImageButton homeBtn = activity.findViewById(R.id.home_bookmark);

        homeBtn.setOnClickListener((view -> {
            setupHomeScreen(defaultUrl);
        }));

        ImageButton backBtn = activity.findViewById(R.id.back_bookmark);

        backBtn.setOnClickListener((view -> {
            setupHomeScreen(currentUrl);
        }));

        ImageButton clearBtn = activity.findViewById(R.id.clearBookmark);

        clearBtn.setOnClickListener((view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("Clear Bookmark");
            alertDialogBuilder.setMessage("Do you wish to clear all the bookmarks?");

            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("bookmarks");
                    bookmarks.clear();
                    setupBookmarkScreen(defaultUrl);

                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }));

        ListView listView = activity.findViewById(R.id.bookmarks);

        MyAdapter myAdapter = new MyAdapter(activity.getApplicationContext(), bookmarks,this);
        listView.setAdapter(myAdapter);
    }

    public void setupSettingScreen(String defaultUrl){
        activity.setContentView(R.layout.setting_page);
        activity.setCurrentContentViewId(R.layout.setting_page);

        ImageButton homeBtn = activity.findViewById(R.id.home_setting);

        homeBtn.setOnClickListener((view -> {
            setupHomeScreen(defaultUrl);
        }));

        ImageButton backBtn = activity.findViewById(R.id.back_setting);

        backBtn.setOnClickListener((view -> {
            setupHomeScreen(currentUrl);
        }));

        setSpinner();
        setHomePageEditText();
    }

    public void setSpinner(){
        Spinner themeSpinner = activity.findViewById(R.id.themeSpinner);
        String[] themes = { "Theme-Orange", "Theme 1-Pink", "Theme 2-Green", "Theme 3-Brown"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_spinner_item, themes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int themeId = 0;
                String selectedTheme= themes[position];
                if (selectedTheme.equals("Theme-Orange")) {
                    themeId = R.style.Theme_ListApp;
                } else if (selectedTheme.equals("Theme 1-Pink")) {
                    themeId = R.style.Theme1_ListApp;
                } else if (selectedTheme.equals("Theme 2-Green")) {
                    themeId = R.style.Theme2_ListApp;
                } else if (selectedTheme.equals("Theme 3-Brown")) {
                    themeId = R.style.Theme3_ListApp;
                }

                SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("themeId", themeId);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        Button applyBtn = activity.findViewById(R.id.applyTheme);
        applyBtn.setOnClickListener(view -> {
            activity.recreate();
        });
    }
    public void setHomePageEditText(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Button button = activity.findViewById(R.id.applyHomePage);
        button.setOnClickListener(view -> {
            EditText editText = activity.findViewById(R.id.homePageSetter);
            String url = editText.getText().toString();
            hideKeyboard();
            if(Patterns.WEB_URL.matcher(url).matches()){
                editor.putString("defaultUrl", url);
                editor.apply();
                activity.recreate();
                Toast.makeText(activity, "Set a New HomePage", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "URL is not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

