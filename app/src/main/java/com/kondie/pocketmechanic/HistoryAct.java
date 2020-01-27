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
    public static HistoryAdapter historyAdapter;
    private LinearLayoutManager linearLayMan;
    private static ProgressBar progressBar, loadMoreProgressBar;
    private static LinearLayout linearLayout;
    private Button retryButt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_act);
        activity = this;

        toolbar = findViewById(R.id.history_toolbar);
        progressBar = findViewById(R.id.history_progress_bar);
        loadMoreProgressBar = findViewById(R.id.load_more_history_progress_bar);
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
            if (MainActivity.historyItems.size() == 0) {
                new GetHistory().execute(MainActivity.lastHistoryDate);
            }
            else{
                progressBar.setVisibility(View.GONE);
            }
        }
    };

    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static ProgressBar getLoadMoreProgressBar() {
        return loadMoreProgressBar;
    }

    private void setUpHistoryList(){

        linearLayMan = new LinearLayoutManager(activity);
        linearLayMan.setOrientation(RecyclerView.VERTICAL);
        historyList.setLayoutManager(linearLayMan);
        historyAdapter = new HistoryAdapter(activity, MainActivity.historyItems, historyList);
        historyAdapter.setOnEndOfListListener(new OnEndOfListListener() {
            @Override
            public void onEndOfList() {
                loadMoreProgressBar.setVisibility(View.VISIBLE);
                new GetHistory().execute(MainActivity.lastHistoryDate);
            }
        });
        historyList.setAdapter(historyAdapter);

        if (MainActivity.historyItems.size() == 0) {
            new GetHistory().execute(MainActivity.lastHistoryDate);
        }
        else{
            progressBar.setVisibility(View.GONE);
        }
    }
}
