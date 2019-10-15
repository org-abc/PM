package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kondie on 2017/09/06.
 */

public class ReqListAdapter2 extends RecyclerView.Adapter {

    public static Activity activity;
    List<ReqItem> reqItems;
    private LayoutInflater inflater;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private static boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private OnEndOfListListener onEndOfListListener;

    public ReqListAdapter2(final Activity activity, final List<ReqItem> reqItems, RecyclerView reqList){

        this.activity = activity;
        this.reqItems = reqItems;
        inflater = LayoutInflater.from(activity);

        /*setHasStableIds(true);

        final LinearLayoutManager linearLayMan = (LinearLayoutManager) reqList.getLayoutManager();
        reqList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayMan.getItemCount();
                lastVisibleItem = linearLayMan.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && reqItems.size() > 9) {
                    if (onEndOfListListener != null) {
                        onEndOfListListener.onEndOfList();
                        isLoading = true;
                    }
                }
            }
        });*/
    }

    public void setOnEndOfListListener(OnEndOfListListener onEndOfListListener){
        this.onEndOfListListener = onEndOfListListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //if(viewType == VIEW_TYPE_ITEM){
        View reqItemView = inflater.inflate(R.layout.req_item2, parent, false);
        ClientHolder holder = new ClientHolder(reqItemView);
        return holder;
        /*}else{
            View loadingItemView = inflater.inflate(R.layout.list_loading_item, parent, false);
            LoadingHolder lHolder = new LoadingHolder(loadingItemView);
            return lHolder;
        }*/
    }

    public static void setLoaded(){
        isLoading = false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return reqItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return reqItems.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof ClientHolder) {

                ClientHolder rHolder = (ClientHolder) holder;
                ReqItem item = reqItems.get(position);

            }/* else if (holder instanceof LoadingHolder) {

                LoadingHolder lHolder = (LoadingHolder) holder;
                lHolder.startCoolLoadingAnim();
            }*/
        }catch (Exception e){
            Toast.makeText(activity, "adapter "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /*private Bitmap getUserProfilePic(){

        ShopProfile.userProfilePic.buildDrawingCache();
        Bitmap DpBitmap = ShopProfile.userProfilePic.getDrawingCache();

        return DpBitmap;
    }*/
}

