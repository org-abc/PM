package com.kondie.pocketmechanic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserProfile extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences prefs;
    private TextView fullName, email, phone, editProfileButt;
    private ImageView userDp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        try {
            prefs = getSharedPreferences("PM", MODE_PRIVATE);
            toolbar = findViewById(R.id.user_profile_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            fullName = findViewById(R.id.user_profile_full_name);
            email = findViewById(R.id.user_profile_email);
            phone = findViewById(R.id.user_profile_phone);
            editProfileButt = findViewById(R.id.edit_profile);
            userDp = findViewById(R.id.user_profile_dp);

            setValues();

            editProfileButt.setOnClickListener(openEditProfile);
        }catch (Exception e){
        }
    }

    private void setValues(){
        fullName.setText(prefs.getString("lname", "") + " " + prefs.getString("fname", ""));
        email.setText(prefs.getString("email", ""));
        phone.setText(prefs.getString("phone", ""));
        Picasso.with(this).load(prefs.getString("imagePath", "").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.user_icon).into(userDp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    private View.OnClickListener openEditProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent toEditIntent = new Intent(UserProfile.this, UpdateProfile.class);
            startActivity(toEditIntent);
        }
    };
}
