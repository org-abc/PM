package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kondie on 2018/02/27.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Toolbar toolbar;
    private SharedPreferences prefs;
    private GoogleMap gMap;
    RecyclerView mechanicList;
    public static List<MechanicItem> mechanicItems;
    public static MechanicListAdapter mechanicListAdapter;
    private LinearLayoutManager linearLayMan;
    private final int EQUATOR_LENGTH = 40075000;
    public static Activity activity;
    public static int distance;
    public static ProgressDialog pDialog;
    public static AlertDialog alertDialog;
    public static AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.map_frag);
        activity = this;

        toolbar = (Toolbar) findViewById(R.id.mechanic_map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = getSharedPreferences("PM", MODE_PRIVATE);
        mechanicItems = new ArrayList<>();
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mechanics_map);
        mapFrag.getMapAsync(MapActivity.this);

        mechanicList = (RecyclerView) findViewById(R.id.all_available_mechanic_list);
        linearLayMan = new LinearLayoutManager(activity);
        linearLayMan.setOrientation(LinearLayoutManager.HORIZONTAL);
        mechanicList.setLayoutManager(linearLayMan);
        mechanicListAdapter = new MechanicListAdapter(activity, mechanicItems);
        mechanicList.setAdapter(mechanicListAdapter);

        new GetMechanics().execute("5050-00-00 00:00:00");
    }

    private void setUserMarker(final GoogleMap googleMap){

        long delayTime = 5000;
        float zoomLevel = 0;

        if (MainActivity.userLocation != null){

            for(int c=0; c<mechanicItems.size(); c++){
                MechanicItem item = mechanicItems.get(c);
                float tempZoomLevel = (float) getZoomForMiters(MainActivity.getDistanceInMeters(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude(), item.getLat(), item.getLng()),MainActivity.userLocation.getAltitude());
                if (tempZoomLevel > zoomLevel){
                    zoomLevel = tempZoomLevel;
                }
            }
            LatLng userLoc = new LatLng(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude());
            //googleMap.addMarker(new MarkerOptions().title(clientName).position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, zoomLevel/2));
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUserMarker(googleMap);
                }
            }, delayTime);
        }
    }

    private double getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return (displayMetrics.widthPixels);
    }

    private double getZoomForMiters(double meters, double lat) {

        final double latAdjustment = Math.cos(Math.PI * lat / 180);
        final double arg = EQUATOR_LENGTH * getScreenWidth() * latAdjustment / (meters * 256);

        return (Math.log(arg) / Math.log(2));
    }

    private void setMapType() {

        switch (prefs.getString("mapType", "")) {

            case "normal":
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "terrain":
                gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "hybrid":
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "satellite":
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void addMarkersOnMap() {

        if (mechanicItems.size() > 0) {
            for (int c = 0; c < mechanicItems.size(); c++) {

                MechanicItem item = mechanicItems.get(c);
                LatLng carLoc = new LatLng(item.getLat(), item.getLng());
                gMap.addMarker(new MarkerOptions().position(carLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_taxi)));
                gMap.setOnMarkerClickListener(MapActivity.this);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(carLoc, 15));
            }
            setUserMarker(gMap);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addMarkersOnMap();
                }
            }, 2000);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            gMap = googleMap;
            setMapType();
            addMarkersOnMap();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            gMap.setMyLocationEnabled(true);
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int c=0; c<mechanicItems.size(); c++) {

            if (marker.getId().equalsIgnoreCase("m" + c)) {

                Dialog mechanicInfoDialog = new Dialog(MapActivity.activity);
                mechanicInfoDialog.setContentView(R.layout.mechanic_dialog);
                mechanicInfoDialog.setTitle("Details");
                mechanicInfoDialog.setCancelable(true);

                TextView reqButton = (TextView) mechanicInfoDialog.findViewById(R.id.req_mechanic_button);
                final TextView mechanicName = (TextView) mechanicInfoDialog.findViewById(R.id.mechanic_dialog_name);
                TextView mechanicDistanceAway = (TextView) mechanicInfoDialog.findViewById(R.id.mechanic_dialog_distance_away);
                TextView minFee = (TextView) mechanicInfoDialog.findViewById(R.id.minimum_dialog_fee);

                final int currentIndex = c;
                final MechanicItem item = mechanicItems.get(currentIndex);

                mechanicName.setText(item.getMechanicName());
                mechanicDistanceAway.setText(String.valueOf(MainActivity.getDistanceInMeters(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLatitude(), item.getLat(), item.getLng())) + " m away");
                minFee.setText(String.valueOf("Minimum fee: " + item.getMinFee()));

                reqButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gotoNav = new Intent(MapActivity.activity, NavMap.class);
                        gotoNav.putExtra("username", mechanicName.getText().toString());
                        gotoNav.putExtra("problem", MapActivity.activity.getIntent().getExtras().getString("problem"));
                        gotoNav.putExtra("lat", String.valueOf(item.getLat()));
                        gotoNav.putExtra("lng", String.valueOf(item.getLng()));
                        gotoNav.putExtra("phone", String.valueOf(item.getPhone()));
                        gotoNav.putExtra("id", String.valueOf(item.getMechanicId()));

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("mechanic", mechanicName.getText().toString());
                        editor.putString("problem", MapActivity.activity.getIntent().getExtras().getString("problem"));
                        editor.putString("lat", String.valueOf(item.getLat()));
                        editor.putString("lng", String.valueOf(item.getLng()));
                        editor.putString("phone", String.valueOf(item.getPhone()));
                        editor.putString("id", String.valueOf(item.getMechanicId()));
                        gotoNav.putExtra("status", "free");
                        editor.commit();

                        MapActivity.activity.startActivity(gotoNav);
                    }
                });

                mechanicInfoDialog.show();
            }
        }

        return true;
    }
}
