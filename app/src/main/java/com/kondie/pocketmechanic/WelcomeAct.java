package com.kondie.pocketmechanic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by kondie on 2018/08/22.
 */

public class WelcomeAct extends AppCompatActivity {

    ImageView welcomeImg;
    public static Activity activity;
    private static ProgressBar progressBar;
    private static LinearLayout linearLayout;
    private Button retryButt;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_act);
        activity = this;

        welcomeImg = findViewById(R.id.welcome_img);
        setImage();
        progressBar = findViewById(R.id.login_progress_bar);
        retryButt = findViewById(R.id.login_reload_butt);
        linearLayout = findViewById(R.id.failed_to_login);
        prefs = getSharedPreferences("PM", Context.MODE_PRIVATE);
        if (prefs.getString("signUpState", "").equalsIgnoreCase("verification")){
            Intent toVerifyIntent = new Intent(activity, VerifAct.class);
            toVerifyIntent.putExtra("email", prefs.getString("email", ""));
            startActivity(toVerifyIntent);
        }
        else {
            new SubmitSignInForm().execute(prefs.getString("email", ""), prefs.getString("password", ""), "welcome");
        }

        retryButt.setOnClickListener(reload);
    }

    private View.OnClickListener reload = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            new SubmitSignInForm().execute(prefs.getString("email", ""), prefs.getString("password", ""), "welcome");
        }
    };

    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    private void setImage(){
        Random random = new Random();
        int num = random.nextInt(8);
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
            case 5:
                welcomeImg.setImageResource(R.drawable.battery);
            case 6:
                welcomeImg.setImageResource(R.drawable.other);
                break;
        }
    }
}
