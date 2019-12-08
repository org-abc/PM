package com.kondie.pocketmechanic;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAct extends AppCompatActivity {

    public static Activity activity;
    private RecyclerView historyList;
    private Toolbar toolbar;
    public static List<HistoryItem> historyItems;
    public static HistoryAdapter historyAdapter;
    private LinearLayoutManager linearLayMan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_act);
        activity = this;

        toolbar = findViewById(R.id.history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyList = findViewById(R.id.history_list);
        setUpHistoryList();
    }

    private void setUpHistoryList(){

        historyItems = new ArrayList<>();
        linearLayMan = new LinearLayoutManager(activity);
        linearLayMan.setOrientation(RecyclerView.VERTICAL);
        historyList.setLayoutManager(linearLayMan);
        historyAdapter = new HistoryAdapter(activity, historyItems, historyList);
        historyList.setAdapter(historyAdapter);

        new GetHistory().execute("5050-00-00 00:00:00");
    }
}
