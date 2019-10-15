package com.pocketmechanic.kondie.pm_admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kondie on 2018/02/17.
 */

public class CreateNewShopAct extends AppCompatActivity {

    Toolbar createNewShopToolbar;
    public static Activity activity;
    ImageView shopIcon;
    public static TextView passNoMatch, wrongPin, shopAddress;
    EditText pass, confirmPass, pin, username, lname, fname, email, phone, bio, minimum_fee;
    Button findStoreButton, saveShopButton;
    final int FIND_SHOP_CODE = 0;
    final int OPEN_GALLERY_CODE = 1;
    private String shopId, shopName, shopLat, shopLng, website;
    private boolean isIconSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_shop_act);
        activity = this;

        isIconSet = false;
        shopId = "";
        createNewShopToolbar = (Toolbar) findViewById(R.id.new_shop_toolbar);
        setSupportActionBar(createNewShopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shopIcon = (ImageView) findViewById(R.id.add_shop_icon);
        passNoMatch = (TextView) findViewById(R.id.pass_no_match_notif);
        wrongPin = (TextView) findViewById(R.id.get_the_fuck_out);
        shopAddress = (TextView) findViewById(R.id.create_shop_address);
        pass = (EditText) findViewById(R.id.shop_pass_et);
        confirmPass = (EditText) findViewById(R.id.confirm_shop_pass_et);
        pin = (EditText) findViewById(R.id.i_fucks_with_it_pin);
        username = (EditText) findViewById(R.id.username);
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        bio = (EditText) findViewById(R.id.bio);
        minimum_fee = (EditText) findViewById(R.id.minimum_fee);
        findStoreButton = (Button) findViewById(R.id.find_store_button);
        saveShopButton = (Button) findViewById(R.id.save_shop);

        findStoreButton.setOnClickListener(findShop);
        saveShopButton.setOnClickListener(saveSelectedShop);
        shopIcon.setOnClickListener(openGalley);
    }

    private View.OnClickListener openGalley = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent openGalleryIntent = new Intent();
            openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            openGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(openGalleryIntent, "Select icon"), OPEN_GALLERY_CODE);
        }
    };

    private View.OnClickListener saveSelectedShop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isIconSet && shopId != "" && !pass.getText().toString().equals("") &&
                    !confirmPass.getText().toString().equals("") && !pin.getText().toString().equals("")
                    && !username.getText().toString().equals("") && !fname.getText().toString().equals("")
                    && !lname.getText().toString().equals("") && !email.getText().toString().equals("")
                    && !phone.getText().toString().equals("") && !minimum_fee.getText().toString().equals("")
                    && !bio.getText().toString().equals("")) {
                if (pass.getText().toString().length() >= 6) {
                    if (pass.getText().toString().equals(confirmPass.getText().toString())) {
                        passNoMatch.setVisibility(View.GONE);
                        new SaveNewShop().execute(shopId, pass.getText().toString(), confirmPass.getText().toString(), pin.getText().toString(), getShopIconString(), getImageName(),
                                shopName, shopLat, shopLng, website, username.getText().toString(), fname.getText().toString()
                                , lname.getText().toString(), email.getText().toString(), phone.getText().toString()
                                , bio.getText().toString(), minimum_fee.getText().toString());
                    } else {
                        passNoMatch.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(CreateNewShopAct.this, "The password must be at lest 6 characters long", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CreateNewShopAct.this, "Set everything, even the icon", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener findShop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent findShopIntent = new PlacePicker.IntentBuilder().build(activity);
                startActivityForResult(findShopIntent, FIND_SHOP_CODE);
            }catch (Exception e){
                Toast.makeText(CreateNewShopAct.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String getShopIconString(){

        shopIcon.buildDrawingCache();
        Bitmap shopIconBitmap = shopIcon.getDrawingCache();

        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        shopIconBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStream);
        byte[] byteArr = byteArrOutStream.toByteArray();

        String iconString = Base64.encodeToString(byteArr, Base64.DEFAULT);

        return (iconString);
    }

    private String getImageName(){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageName = "Specials_" + shopName + "_" + timeStamp;

        return (imageName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FIND_SHOP_CODE){

            if (resultCode == RESULT_OK){
                Place shop = PlacePicker.getPlace(activity, data);
                shopId = shop.getId();
                try {
                    website = shop.getWebsiteUri().toString();
                }catch (Exception e){
                    website = "";
                }
                shopName = shop.getName().toString();
                shopLat = String.valueOf(shop.getLatLng().latitude);
                shopLng = String.valueOf(shop.getLatLng().longitude);
                shopAddress.setText(shop.getAddress() + ", " + shopName);
                shopAddress.setVisibility(View.VISIBLE);
                findStoreButton.setText("change shop");
            }
        }else if (requestCode == OPEN_GALLERY_CODE){

            if (resultCode == RESULT_OK){

                Uri shopIconUri = data.getData();
                shopIcon.setImageURI(shopIconUri);
                isIconSet = true;
            }
        }
    }
}
