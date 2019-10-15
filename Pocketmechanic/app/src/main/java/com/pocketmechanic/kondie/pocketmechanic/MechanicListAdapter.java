package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kondie on 2018/03/01.
 */

public class MechanicListAdapter extends RecyclerView.Adapter<MechanicHolder> {

    Activity activity;
    List<MechanicItem> mechanicItems;
    LayoutInflater inflater;

    public MechanicListAdapter(Activity activity, List<MechanicItem> mechanicItems){

        this.activity = activity;
        this.mechanicItems = mechanicItems;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mechanicItems.size();
    }

    @Override
    public MechanicHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mechanicItemView = inflater.inflate(R.layout.mechanic_item, parent, false);
        MechanicHolder holder = new MechanicHolder(mechanicItemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(MechanicHolder holder, int position) {

        MechanicItem item = mechanicItems.get(position);

        try {
            holder.mechanicHname.setText(item.getMechanicName());
            holder.mechanicId.setText(String.valueOf(item.getMechanicId()));
            holder.mechanicHrating.setText(String.valueOf(item.getRating()));
            holder.mechanicHdistance.setText("0 km away");
            holder.mechanicHminFee.setText("Minimum fee: R" + String.valueOf(item.getMinFee()));
            holder.lat.setText(String.valueOf(item.getLat()));
            holder.lng.setText(String.valueOf(item.getLng()));
            holder.mechanicPhone.setText(String.valueOf(item.getPhone()));
            Picasso.with(activity).load(item.getMechanicImagePath()).into(holder.mechanicPic);
        }catch (Exception e){
            Toast.makeText(activity, "Adapt" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
