package com.example.listapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Stack;

public class MyAdapter extends ArrayAdapter<UrlRecord> {
    private final Context context;
    private final Stack<UrlRecord> urlRecords;
    private final Controller controller;

    public MyAdapter(Context context, Stack<UrlRecord> historyRecords, Controller controller) {
        super(context, R.layout.list_item_layout, historyRecords);
        this.context = context;
        this.urlRecords = historyRecords;
        this.controller = controller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_layout, null);
        }

        int reversedPosition = getCount() - 1 - position;
        UrlRecord currentRecord = urlRecords.get(reversedPosition);

        ImageView iconImageView = view.findViewById(R.id.iconImageView);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        ImageView deleteRecord = view.findViewById(R.id.deleteRecord);

        iconImageView.setImageBitmap(currentRecord.getIcon());

        titleTextView.setOnClickListener(icon -> {
            String url = currentRecord.getUrl();
            controller.setupHomeScreen(url);
        });

        String title =  currentRecord.getTitle();
        if (title.length() >= 40) {
            title = title.substring(0, 40) + "...";
        }
        titleTextView.setText(title);

        deleteRecord.setOnClickListener(delete -> {
            urlRecords.remove(currentRecord);
            this.notifyDataSetChanged();
        });
        return view;
    }

}
