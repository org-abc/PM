package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by kondie on 2018/03/01.
 */

public class MechanicHolder extends RecyclerView.ViewHolder {

    ImageView mechanicPic;
    TextView mechanicId, mechanicHname, mechanicHminFee, mechanicHdistance, mechanicHrating, lat, lng, mechanicPhone;

    public MechanicHolder(View mechanicItemView){
        super(mechanicItemView);

        mechanicPic = (ImageView) mechanicItemView.findViewById(R.id.mechanic_image);
        mechanicId = (TextView) mechanicItemView.findViewById(R.id.mechanic_id);
        mechanicHname = (TextView) mechanicItemView.findViewById(R.id.mechanic_h_name);
        mechanicHminFee = (TextView) mechanicItemView.findViewById(R.id.mechanic_h_min_fee);
        mechanicHdistance = (TextView) mechanicItemView.findViewById(R.id.mechanic_h_distance);
        mechanicHrating = (TextView) mechanicItemView.findViewById(R.id.mechanic_h_rating);
        mechanicPhone = (TextView) mechanicItemView.findViewById(R.id.mechanic_phone);
        lat = (TextView) mechanicItemView.findViewById(R.id.mechanic_lat);
        lng = (TextView) mechanicItemView.findViewById(R.id.mechanic_lng);

        mechanicItemView.setOnClickListener(showMechanicDetails);
    }

    View.OnClickListener showMechanicDetails = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Dialog mechanicInfoDialog = new Dialog(MapActivity.activity);
            mechanicInfoDialog.setContentView(R.layout.mechanic_dialog);
            mechanicInfoDialog.setTitle("Details");
            mechanicInfoDialog.setCancelable(true);

            TextView reqButton = (TextView) mechanicInfoDialog.findViewById(R.id.req_mechanic_button);
            final TextView mechanicName = (TextView) mechanicInfoDialog.findViewById(R.id.mechanic_dialog_name);
            TextView mechanicDistanceAway = (TextView) mechanicInfoDialog.findViewById(R.id.mechanic_dialog_distance_away);
            TextView minFee = (TextView) mechanicInfoDialog.findViewById(R.id.minimum_dialog_fee);

            mechanicName.setText(mechanicHname.getText().toString());
            mechanicDistanceAway.setText(mechanicHdistance.getText().toString());
            minFee.setText(mechanicHminFee.getText().toString());

            reqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoNav = new Intent(MapActivity.activity, NavMap.class);
                    gotoNav.putExtra("username", mechanicName.getText().toString());
                    gotoNav.putExtra("problem", MapActivity.activity.getIntent().getExtras().getString("problem"));
                    gotoNav.putExtra("lat", lat.getText().toString());
                    gotoNav.putExtra("lng", lng.getText().toString());
                    gotoNav.putExtra("phone", mechanicPhone.getText().toString());
                    gotoNav.putExtra("id", mechanicId.getText().toString());

                    SharedPreferences prefs = MapActivity.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("mechanic", mechanicName.getText().toString());
                    editor.putString("problem", MapActivity.activity.getIntent().getExtras().getString("problem"));
                    editor.putString("lat", lat.getText().toString());
                    editor.putString("lng", lng.getText().toString());
                    editor.putString("phone", mechanicPhone.getText().toString());
                    editor.putString("id", mechanicId.getText().toString());
                    gotoNav.putExtra("status", "free");
                    editor.commit();

                    MapActivity.activity.startActivity(gotoNav);
                }
            });

            mechanicInfoDialog.show();
        }
    };
}
