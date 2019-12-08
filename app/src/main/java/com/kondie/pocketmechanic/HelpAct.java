package com.kondie.pocketmechanic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

/**
 * Created by kondie on 2018/01/22.
 */

public class HelpAct extends AppCompatActivity implements PermissionUtils.PermissionResultCallback {

    private Toolbar helpToolbar;
    TextView mphCell, mphEmail, physicalAddress;
    private PermissionUtils permissionUtils;
    private final int REQUEST_CHECK_SETTINGS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_act);

        helpToolbar = findViewById(R.id.help_toolbar);
        setSupportActionBar(helpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mphEmail = findViewById(R.id.mph_email);
        mphCell = findViewById(R.id.mph_cell_numbers);
        physicalAddress = findViewById(R.id.physical_address);

//        physicalAddress.setText("515 Spuy str Sunnyside Garden Sunnyside Pretoria 0181");
//        physicalAddress.setOnClickListener(shopMphOnMap);
        mphCell.setOnClickListener(callMph);
        mphEmail.setOnClickListener(emailMph);
    }

//    private View.OnClickListener shopMphOnMap = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent openMapIntent = new Intent(HelpAct.this, MapActivity.class);
//            startActivity(openMapIntent);
//        }
//    };


    private void checkPermissions(){

        ArrayList<String> permissions=new ArrayList<>();
        permissionUtils=new PermissionUtils(HelpAct.this,this);

        permissions.add(Manifest.permission.CALL_PHONE);

        permissionUtils.check_permission(permissions, "We need your GPS for delivery", REQUEST_CHECK_SETTINGS);
    }

    private void makeCall(){
        String uri = "tel:" + mphCell.getText().toString().trim();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse(uri));

        if (ActivityCompat.checkSelfPermission(HelpAct.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            checkPermissions();
            return;
        }
        startActivity(callIntent);
    }

    private View.OnClickListener callMph = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            makeCall();
        }
    };

    private View.OnClickListener emailMph = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, mphEmail.getText().toString());
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help");
            emailIntent.setType("*/*");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            }
        }
    };

    @Override
    public void PermissionGranted(int request_code) {
        makeCall();
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {

    }

    @Override
    public void NeverAskAgain(int request_code) {

    }
}
