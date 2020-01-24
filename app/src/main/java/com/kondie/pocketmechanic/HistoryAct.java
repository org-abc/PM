package com.kondie.pocketmechanic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
    private static ProgressBar progressBar;
    private static LinearLayout linearLayout;
    private Button retryButt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_act);
        activity = this;

        toolbar = findViewById(R.id.history_toolbar);
        progressBar = findViewById(R.id.history_progress_bar);
        retryButt = findViewById(R.id.history_reload_butt);
        linearLayout = findViewById(R.id.failed_to_load_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyList = findViewById(R.id.history_list);
        setUpHistoryList();

        retryButt.setOnClickListener(reload);
    }

    private View.OnClickListener reload = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            new GetHistory().execute("5050-00-00 00:00:00");
        }
    };

    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
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
