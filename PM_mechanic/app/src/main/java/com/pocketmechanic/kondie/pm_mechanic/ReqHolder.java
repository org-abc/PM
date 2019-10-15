package com.pocketmechanic.kondie.pm_mechanic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by kondie on 2019/01/18.
 */

public class ReqHolder extends RecyclerView.ViewHolder {

    TextView clientProb, clientName, distanceAway, lat, lng, phone, reqId;
    TableLayout req;
    SharedPreferences prefs;

    public ReqHolder(View reqItemView) {
        super(reqItemView);

        prefs = MainActivity.activity.getSharedPreferences("PMM", Context.MODE_PRIVATE);
        clientProb = (TextView) reqItemView.findViewById(R.id.client_problem);
        clientName = (TextView) reqItemView.findViewById(R.id.client_name);
        distanceAway = (TextView) reqItemView.findViewById(R.id.distance_away);
        lat = (TextView) reqItemView.findViewById(R.id.req_lat);
        lng = (TextView) reqItemView.findViewById(R.id.req_lng);
        phone = (TextView) reqItemView.findViewById(R.id.requester_phone);
        reqId = (TextView) reqItemView.findViewById(R.id.req_id);
        req = (TableLayout) reqItemView.findViewById(R.id.req_item);

        if (!prefs.getString("status", "").equalsIgnoreCase("busy")) {
            req.setOnClickListener(openMap);
        }else{
            req.setOnClickListener(openNavMap);
        }
    }

    View.OnClickListener openNavMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity.openSesami();
        }
    };

    View.OnClickListener openMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent gotoNavMap = new Intent(MainActivity.activity, NavMap.class);
            gotoNavMap.putExtra("username", clientName.getText().toString().split(": ")[1]);
            gotoNavMap.putExtra("lat", lat.getText().toString());
            gotoNavMap.putExtra("lng", lng.getText().toString());
            gotoNavMap.putExtra("phone", phone.getText().toString());
            gotoNavMap.putExtra("id", reqId.getText().toString());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("client", clientName.getText().toString().split(": ")[1]);
            editor.putString("lat", lat.getText().toString());
            editor.putString("lng", lng.getText().toString());
            editor.putString("phone", phone.getText().toString());
            editor.putString("id", reqId.getText().toString());
            editor.commit();
            MainActivity.activity.startActivity(gotoNavMap);
        }
    };
}
