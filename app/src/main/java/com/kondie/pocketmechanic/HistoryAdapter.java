package com.kondie.pocketmechanic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> implements OnEndOfListListener {

    Activity activity;
    List<HistoryItem> historyItems;
    LayoutInflater inflater;
    private static boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private OnEndOfListListener onEndOfListListener;
    SharedPreferences prefs;

    public HistoryAdapter(Activity activity, final List<HistoryItem> historyItems, RecyclerView historyList) {
        this.activity = activity;
        this.historyItems = historyItems;
        inflater = LayoutInflater.from(activity);
        prefs = activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
        setHasStableIds(true);

        final LinearLayoutManager linearLayMan = (LinearLayoutManager) historyList.getLayoutManager();
        historyList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayMan.getItemCount();
                lastVisibleItem = linearLayMan.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && historyItems.size() % 10 == 0) {
                    if (onEndOfListListener != null) {
                        onEndOfListListener.onEndOfList();
                    }
                }
            }
        });
    }

    @Override
    public void onEndOfList() {
        isLoading = true;
    }

    public void setOnEndOfListListener(OnEndOfListListener onEndOfListListener) {
        this.onEndOfListListener = onEndOfListListener;
    }

    public static void setLoaded() {
        isLoading = false;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View historyItemView = inflater.inflate(R.layout.history_item, parent, false);
        HistoryHolder holder = new HistoryHolder(historyItemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        HistoryItem item = historyItems.get(position);

        try {
            holder.driverName.setText(item.getDriverName());
            holder.deliveryFee.setText("Service fee: R" + item.getServiceFee());
            holder.issue.setText(item.getIssue() + " problem");
            if (item.getStatus().equals("cancel")){
                holder.status.setText("canceled");
                holder.status.setTextColor(activity.getResources().getColor(R.color.red));
            }else {
                holder.status.setText(item.getStatus());
                holder.status.setTextColor(activity.getResources().getColor(R.color.green));
            }
            holder.date.setText(item.getDateCreated());

        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
