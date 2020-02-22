package com.kondie.pocketmechanic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

public class RequestForm extends AppCompatActivity {

    public static Activity activity;
    private String currentImagePath;
    final int TAKE_IMAGE_CODE = 0;
    private ImageView reqPic;
    private EditText reqComment, reqMakeAndModel;
    private TextView reqButt, selectedIssue;
    private int serviceFee;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_form);
        activity = this;
        prefs = getSharedPreferences("PM", Context.MODE_PRIVATE);

        setUpToolbar();
        reqPic = findViewById(R.id.req_pic);
        selectedIssue = findViewById(R.id.selected_issue);
        reqComment = findViewById(R.id.req_comment);
        reqMakeAndModel = findViewById(R.id.req_car_make_and_model);
        reqButt = findViewById(R.id.request_for_mechanic_butt);
        serviceFee = 100;

        selectedIssue.setText(getIntent().getExtras().getString("issue"));
        reqPic.setOnClickListener(openCamera);
        reqButt.setOnClickListener(sendRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.userLocation == null){
            RequestForm.this.finish();
        }
    }

    private View.OnClickListener sendRequest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!prefs.getString("status", "").equalsIgnoreCase("busy")) {
                if (!reqMakeAndModel.getText().toString().equalsIgnoreCase("")) {
                    new AlertDialog.Builder(activity).setCancelable(false).setTitle("Are you sure you want a mechanic?").setMessage("The consultation fee is R" + serviceFee)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new RequestForAMechanic().execute(reqComment.getText().toString(), reqMakeAndModel.getText().toString(), getIntent().getExtras().getString("issue"), String.valueOf(serviceFee));
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else {
                    Snackbar.make(view, "Please fill in the make and model of your car", Snackbar.LENGTH_LONG).show();
                }
            }
            else{
                Snackbar.make(view, "You already have an active request", Snackbar.LENGTH_LONG).show();
            }
        }
    };

    private void setUpToolbar(){

        Toolbar toolbar = findViewById(R.id.req_form_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private View.OnClickListener openCamera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {

                    File imageFileName = null;
                    try {
                        imageFileName = getImageFile();

                    } catch (Exception e) {
//                        Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    if (imageFileName != null) {

                        Uri imageUri = FileProvider.getUriForFile(activity, "com.pocketmechanic.kondie.pocketmechanic.fileprovider", imageFileName);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, TAKE_IMAGE_CODE);
                    }
                }
            }catch (Exception e){
//                Toast.makeText(RequestForm.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };


    private File getImageFile(){

        String imageName = MainActivity.getImageName();
        File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(imageName, ".jpg", fileDir);
            currentImagePath = imageFile.getAbsolutePath();

            return imageFile;

        }catch (Exception e){
//            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_IMAGE_CODE && resultCode == activity.RESULT_OK){

            MainActivity.addPicToGallery(currentImagePath);
            setImageFromPath();
        }
    }

    private void setImageFromPath(){

        int targetH = 520;
        int targetW = 520;
        int maxH = 1024;
        int maxW = 1024;

        BitmapFactory.Options bmOption = new BitmapFactory.Options();
        bmOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentImagePath, bmOption);
        int h = bmOption.outHeight;
        int w = bmOption.outWidth;

        int scaleFactor = Math.min(h/targetH, w/targetW);

        bmOption.inJustDecodeBounds = false;
        bmOption.inSampleSize = scaleFactor;

        Bitmap imageBitmap = BitmapFactory.decodeFile(currentImagePath, bmOption);
        Bitmap rotatedImageBitmap = null;

        try {
            ExifInterface ei = new ExifInterface(currentImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedImageBitmap = rotateBitmap(imageBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedImageBitmap = rotateBitmap(imageBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedImageBitmap = rotateBitmap(imageBitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedImageBitmap = imageBitmap;
            }

        }catch (Exception e){
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        reqPic.setImageBitmap(rotatedImageBitmap);
    }

    private Bitmap rotateBitmap(Bitmap sourceBitmap, float angle){

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }
}
