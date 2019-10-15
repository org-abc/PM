package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by kondie on 2018/08/22.
 */

public class WelcomeAct extends AppCompatActivity {

    ImageView welcomeImg;
    public static  Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_act);
        activity = this;

        welcomeImg = (ImageView) findViewById(R.id.welcome_img);
        //setImage();
        SharedPreferences prefs = getSharedPreferences("PMM", Context.MODE_PRIVATE);
        new SubmitSignInForm().execute(prefs.getString("username", ""), prefs.getString("password", ""), "welcome");
    }

    /*private void setImage(){
        Random random = new Random();
        int num = random.nextInt(5);
        switch (num) {
            case 0:
                welcomeImg.setImageResource(R.drawable.tires);
                break;
            case 1:
                welcomeImg.setImageResource(R.drawable.engine);
                break;
            case 2:
                welcomeImg.setImageResource(R.drawable.electronics);
                break;
            case 3:
                welcomeImg.setImageResource(R.drawable.brakes);
                break;
            case 4:
                welcomeImg.setImageResource(R.drawable.headlights);
                break;
        }
    }*/
}
