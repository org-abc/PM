package com.kondie.pocketmechanic;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryHolder extends RecyclerView.ViewHolder {

    TextView driverName, date, shop, status, items, deliveryFee, amount;
    public HistoryHolder(@NonNull View itemView) {
        super(itemView);

        driverName = itemView.findViewById(R.id.order_deliverer);
        date = itemView.findViewById(R.id.order_date);
        shop = itemView.findViewById(R.id.order_shop);
        status = itemView.findViewById(R.id.order_status);
        items = itemView.findViewById(R.id.order_items);
        deliveryFee = itemView.findViewById(R.id.order_delivery_fee);
        amount = itemView.findViewById(R.id.order_amount);
    }
}
