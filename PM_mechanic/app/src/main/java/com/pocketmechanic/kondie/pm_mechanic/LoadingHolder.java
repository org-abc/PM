package com.pocketmechanic.kondie.pm_mechanic;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kondie on 2017/09/30.
 */

public class LoadingHolder extends RecyclerView.ViewHolder{

    private static View toTheLeft, toTheRight;

    public LoadingHolder(View loadingItemView){
        super(loadingItemView);

        toTheLeft = loadingItemView.findViewById(R.id.list_to_the_left);
        toTheRight = loadingItemView.findViewById(R.id.list_to_the_right);

        startCoolLoadingAnim();
    }

    public static void startCoolLoadingAnim(){

        try {
            //toTheRight.startAnimation(MainActivity.getMoveRightAnim(MainActivity.activity));
            //toTheLeft.startAnimation(MainActivity.getMoveLeftAnim(MainActivity.activity));
        }catch (Exception e){
            //Toast.makeText(MainActivity.activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void stopCoolLoadingAnim(){

        toTheLeft.clearAnimation();
        toTheRight.clearAnimation();
    }

}
