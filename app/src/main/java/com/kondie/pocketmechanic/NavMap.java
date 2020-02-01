package com.kondie.pocketmechanic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by kondie on 2018/02/04.
 */

public class NavMap extends AppCompatActivity implements OnMapReadyCallback {

    public static Toolbar toolbar;
    public static GoogleMap navGoogleMap;
    static SharedPreferences prefs;
    private final int EQUATOR_LENGTH = 40075000;
    public static Activity activity;
    public static String mechanicName;
    public static Float mechanicLat, mechanicLng;
    public static LatLng mechanicLatLng;
    SharedPreferences.Editor editor;
    static Marker clientMarker, mechanicMarker;
    public static ImageView callMechanicFb, mechanicDp;
    private static ProgressBar navMapLoader;
    private ImageView backButt;
    private static String resp;
    public static PolylineOptions lineOptions;
    private Dialog mechanicProfileDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.nav_map);
        prefs = getSharedPreferences("PM", MODE_PRIVATE);
        editor = prefs.edit();
        activity = this;

        try {
            toolbar = findViewById(R.id.nav_toolbar);
            setSupportActionBar(toolbar);
            backButt = findViewById(R.id.back_from_nav);
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nav_map_frag);
            mapFrag.getMapAsync(NavMap.this);
            mechanicLat = (float) 0.0;
            mechanicLng = (float) 0.0;
            callMechanicFb = findViewById(R.id.call_mechanic_fb);
            mechanicDp = findViewById(R.id.mechanic_dp);
            navMapLoader = findViewById(R.id.nav_map_loader);

            callMechanicFb.setOnClickListener(callMechanic);
            backButt.setOnClickListener(goBack);
            mechanicDp.setOnClickListener(openMechanicDialog);
        }catch (Exception e){
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener openMechanicDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                mechanicProfileDialog = new Dialog(activity);
                mechanicProfileDialog.setContentView(R.layout.mechanic_profile);
                mechanicProfileDialog.setCancelable(true);

                ImageView mechanicDialogDp = mechanicProfileDialog.findViewById(R.id.mechanic_dialog_dp);
                TextView mechanicDialogName = mechanicProfileDialog.findViewById(R.id.mechanic_dialog_name);
                RatingBar mechanicDialogRating = mechanicProfileDialog.findViewById(R.id.mechanic_dialog_rating);

                mechanicDialogName.setText(prefs.getString("mechanicFname", "") + " " + prefs.getString("mechanicLname", ""));
                mechanicDialogRating.setRating(prefs.getFloat("mechanicRating", 0));
                Picasso.with(activity).load(prefs.getString("mechanicImagePath", "").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.user_icon).into(mechanicDialogDp);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(mechanicProfileDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                mechanicProfileDialog.show();
                mechanicProfileDialog.getWindow().setAttributes(lp);
            }catch (Exception e){
//                Toast.makeText(NavMap.this, "" + e, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener goBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            activity.finish();
        }
    };

    private View.OnClickListener callMechanic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + prefs.getString("mechanicPhone", "")));
            startActivity(callIntent);
        }
    };

    private double getScreenWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return (displayMetrics.widthPixels);
    }

    private double getZoomForMiters(double meters, double lat){

        final double latAdjustment = Math.cos(Math.PI * lat / 180);
        final double arg = EQUATOR_LENGTH * getScreenWidth() * latAdjustment / (meters * 256);

        return (Math.log(arg) / Math.log(2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.userLocation == null){
            NavMap.this.finish();
        }
        else {
            new CheckResponse().execute();
        }
    }

    private void setMapType() {

        switch (prefs.getString("mapType", "")) {

            case "normal":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "terrain":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "hybrid":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "satellite":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public static void updateLoc(){

//        navGoogleMap.clear();
        mechanicLatLng = new LatLng(mechanicLat, mechanicLng);
        clientMarker.setPosition(MainActivity.dropOffLoc);
        mechanicMarker.setPosition(mechanicLatLng);
//        setUpMarkersAndDirections();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CheckResponse().execute();
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.cancel_nav:
//                if (!resp.equals("accept")){
                    new AlertDialog.Builder(NavMap.activity).setCancelable(false).setMessage("Are you sure you want to cancel this request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CancelNav().execute();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
//                }
//                else{
//                    Toast.makeText(activity, "Is too late to cancel the order", Toast.LENGTH_LONG).show();
//                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void startCheckingForMechanicResponse(String response){

        resp = response;
        if (response.equalsIgnoreCase("decline")){
            new AlertDialog.Builder(activity).setCancelable(false).setMessage("Your request was denied")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    }).show();
        }else if (response.equalsIgnoreCase("accept") || response.equalsIgnoreCase("arrived")) {

            try {
                Picasso.with(activity).load(prefs.getString("mechanicImagePath", "").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.user_icon).into(mechanicDp);
            } catch (Exception e) {
            }
            mechanicDp.setVisibility(View.VISIBLE);
            callMechanicFb.setVisibility(View.VISIBLE);
            navMapLoader.setVisibility(View.GONE);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new CheckResponse().execute();
                }
            }, 5000);
        }
    }

    public static void setUpMarkersAndDirections(){

//        if (lineOptions != null){
//            navGoogleMap.addPolyline(lineOptions);
//        }
        mechanicLatLng = new LatLng(mechanicLat, mechanicLng);
        clientMarker = navGoogleMap.addMarker(new MarkerOptions().position(MainActivity.dropOffLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)));
        navGoogleMap.addMarker(new MarkerOptions().position(new LatLng(prefs.getFloat("shopLat", (float)0), prefs.getFloat("shopLng", (float) 0))).title("Restaurant"));
//        new GetDirections().execute(mechanicLat, mechanicLng, prefs.getFloat("shopLat", (float)0), prefs.getFloat("shopLng", (float) 0), (float) 0);
//        new GetDirections().execute(prefs.getFloat("shopLat", (float)0), prefs.getFloat("shopLng", (float) 0), (float) MainActivity.dropOffLoc.latitude, (float) MainActivity.dropOffLoc.longitude, (float) 1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            navGoogleMap = googleMap;
            setMapType();

            navGoogleMap.setMyLocationEnabled(true);
            mechanicLatLng = new LatLng(mechanicLat, mechanicLng);
            mechanicMarker = navGoogleMap.addMarker(new MarkerOptions().position(mechanicLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_taxi)));
            clientMarker = navGoogleMap.addMarker(new MarkerOptions().position(MainActivity.dropOffLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)));
            navGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.dropOffLoc, 15));

            editor.putFloat("mechanicLat", mechanicLat);
            editor.putFloat("mechanicLng", mechanicLng);
            editor.putString("mechanicName", mechanicName);
            editor.putString("navStatus", "active");
            editor.commit();
            new CheckResponse().execute();
        }catch (Exception e){
//            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
