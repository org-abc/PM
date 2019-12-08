package com.kondie.pocketmechanic;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

public class CoolLoading {

    public static LinearLayout loadingGeoLay, coolLoadingLay;
    static View toTheLeft, toTheRight;
    static Activity activity;

    public CoolLoading(Activity activity){
        this.activity = activity;
        coolLoadingLay = activity.findViewById(R.id.geo_cool_loading_lay);
        toTheLeft = activity.findViewById(R.id.geo_to_the_left);
        toTheRight = activity.findViewById(R.id.geo_to_the_right);
    }

    public void startCoolLoadingAnim() {

        try {
            coolLoadingLay.setVisibility(View.VISIBLE);
            toTheRight.startAnimation(getMoveRightAnim());
            toTheLeft.startAnimation(getMoveLeftAnim());
        } catch (Exception e) {
        }
    }

    public Animation getBreathingAnim(){

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.bouncy_anim);
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        anim.setInterpolator(interpolator);

        return (anim);
    }

    public Animation getMoveRightAnim() {

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.move_right_from_center);

        return (anim);
    }

    public Animation getMoveLeftAnim() {

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.move_left_from_center);

        return (anim);
    }

    public void stopCoolLoadingAnim() {

        toTheLeft.clearAnimation();
        toTheRight.clearAnimation();
        coolLoadingLay.setVisibility(View.GONE);
    }
}
