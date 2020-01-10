package com.kondie.pocketmechanic;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryHolder extends RecyclerView.ViewHolder {

    TextView driverName, date, issue, status, deliveryFee, amount;
    public HistoryHolder(@NonNull View itemView) {
        super(itemView);

        driverName = itemView.findViewById(R.id.order_deliverer);
        date = itemView.findViewById(R.id.order_date);
        status = itemView.findViewById(R.id.order_status);
        issue = itemView.findViewById(R.id.req_issue);
        deliveryFee = itemView.findViewById(R.id.order_delivery_fee);
    }
}
