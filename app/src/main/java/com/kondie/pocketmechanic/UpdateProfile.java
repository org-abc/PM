package com.kondie.pocketmechanic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UpdateProfile extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences prefs;
    private EditText fname, lname, phone;
    private ImageView userDp;
    private TextView updateButt;
    public static Activity activity;
    final int OPEN_GALLERY_CODE = 0;
    private boolean isDpChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        activity = this;
        prefs = getSharedPreferences("PM", MODE_PRIVATE);
        toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fname = findViewById(R.id.edit_client_fname);
        lname = findViewById(R.id.edit_client_lname);
        phone = findViewById(R.id.edit_client_phone);
        userDp = findViewById(R.id.edit_user_dp);
        updateButt = findViewById(R.id.finish_edit_button);

        fname.setText(prefs.getString("lname", ""));
        lname.setText(prefs.getString("fname", ""));
        phone.setText(prefs.getString("phone", ""));
        Picasso.with(this).load(prefs.getString("imagePath", "")).into(userDp);

        updateButt.setOnClickListener(updateIt);
        userDp.setOnClickListener(openGalley);
    }

    View.OnClickListener updateIt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!fname.getText().toString().equalsIgnoreCase("") &&
                    !lname.getText().toString().equalsIgnoreCase("") &&
                    !phone.getText().toString().equalsIgnoreCase(""))
            {
                new UpdateUserProfile().execute(fname.getText().toString(),
                        lname.getText().toString(),
                        phone.getText().toString(),
                        getShopIconString(),
                        getImageName());
            }
            else
            {
                Toast.makeText(activity, "Fill in all the details", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener openGalley = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent openGalleryIntent = new Intent();
            openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            openGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(openGalleryIntent, "Select icon"), OPEN_GALLERY_CODE);
        }
    };

    private String getShopIconString(){

        BitmapDrawable bitmapDrawable = (BitmapDrawable) userDp.getDrawable();
        Bitmap shopIconBitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        shopIconBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStream);
        byte[] byteArr = byteArrOutStream.toByteArray();

        String iconString = Base64.encodeToString(byteArr, Base64.DEFAULT);

        return (iconString);
    }

    private String getImageName(){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageName = "Skopo_user_" + fname.getText().toString() + "_" + timeStamp;

        if (isDpChanged) {
            return (imageName);
        }else{
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_GALLERY_CODE){

            if (resultCode == RESULT_OK){

                Uri shopIconUri = data.getData();
                userDp.setImageURI(shopIconUri);
                isDpChanged = true;
            }
        }
    }
}
