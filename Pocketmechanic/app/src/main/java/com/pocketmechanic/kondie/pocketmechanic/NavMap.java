package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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

public class NavMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Toolbar toolbar;
    private SharedPreferences prefs;
    private GoogleMap gMap;
    private final int EQUATOR_LENGTH = 40075000;
    public static Activity activity;
    public static int distance;
    public static ProgressDialog pDialog;
    public static AlertDialog alertDialog;
    public static AlertDialog.Builder dialogBuilder;
    public static double lat, lng;
    public static String mechanic;
    public static Marker marker;
    ImageView callMechanic;
    TextView cancelHelp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.nav_map);
        activity = this;

        lat = Double.valueOf(getIntent().getExtras().getString("lat"));
        lng = Double.valueOf(getIntent().getExtras().getString("lng"));
        mechanic = getIntent().getExtras().getString("username");
        toolbar = (Toolbar) findViewById(R.id.nav_map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = getSharedPreferences("PM", MODE_PRIVATE);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mechanics_map);
        mapFrag.getMapAsync(NavMap.this);

        callMechanic = (ImageView) findViewById(R.id.call_mechanic);
        cancelHelp = (TextView) findViewById(R.id.cancel_help);

        callMechanic.setOnClickListener(call);
        cancelHelp.setOnClickListener(cancelIt);

        if (getIntent().getExtras().getString("status").equalsIgnoreCase("free")) {
            new SendReq().execute(mechanic, getIntent().getExtras().getString("problem"));
        }
    }

    View.OnClickListener cancelIt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(NavMap.this, getIntent().getExtras().getString("id"), Toast.LENGTH_SHORT).show();
            new SendBackResponse().execute(getIntent().getExtras().getString("username"), "c_canceled", getIntent().getExtras().getString("id"));
        }
    };

    private View.OnClickListener call = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + getIntent().getExtras().getString("phone")));
            startActivity(callIntent);
        }
    };

    public static void refreshLoc() {
        LatLng newLoc = new LatLng(lat, lng);
        marker.setPosition(newLoc);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.userLocation != null) {
                    new GetAndSendLatLng().execute(mechanic, "both");
                }
            }
        }, 10000);
    }

    private void setUserMarker(final GoogleMap googleMap) {

        long delayTime = 5000;
        float zoomLevel = 0;

        if (MainActivity.userLocation != null) {

            float tempZoomLevel = (float) getZoomForMiters(MainActivity.getDistanceInMeters(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude(), lat, lng), MainActivity.userLocation.getAltitude());
            zoomLevel = tempZoomLevel;

            LatLng userLoc = new LatLng(lat, lng);
            marker = googleMap.addMarker(new MarkerOptions().title(getIntent().getExtras().getString("username")).position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_taxi)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, zoomLevel / 2));
        } else {
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

   /* private void addMarkersOnMap(){

        if (mechanicItems.size() > 0) {
            for (int c = 0; c < mechanicItems.size(); c++) {

                MechanicItem item = mechanicItems.get(c);
                LatLng carLoc = new LatLng(item.getCarLat(), item.getCarLng());
                gMap.addMarker(new MarkerOptions().position(carLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.naruto)));
                gMap.setOnMarkerClickListener(MapActivity.this);
            }
            setUserMarker(gMap);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addMarkersOnMap();
                }
            }, 2000);
        }
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            gMap = googleMap;
            setMapType();
            setUserMarker(gMap);
            refreshLoc();
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
            //addMarkersOnMap();
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        /*for (int c=0; c<willzoCarItems.size(); c++) {

            if (marker.getId().equalsIgnoreCase("m" + c)) {

                final Dialog profileDialog = new Dialog(activity);
                profileDialog.setContentView(R.layout.select_car_dialog);
                profileDialog.setTitle("Title");
                profileDialog.setCancelable(true);

                ImageView carImage = (ImageView) profileDialog.findViewById(R.id.select_car_image);
                ImageView driverImage = (ImageView) profileDialog.findViewById(R.id.select_driver_dp);
                TextView driverName = (TextView) profileDialog.findViewById(R.id.select_driver_name);
                RatingBar driverRating = (RatingBar) profileDialog.findViewById(R.id.select_driver_rating);
                Button cancelButton = (Button) profileDialog.findViewById(R.id.cancel_selection_button);
                Button selectButton = (Button) profileDialog.findViewById(R.id.select_car_button);

                final int currentIndex = c;
                final MechanicItem item = mechanicItems.get(currentIndex);

                driverName.setText(item.getDriverName());
                driverRating.setRating((float) item.getRating());
                Picasso.with(activity).load(item.getCarImagePath()).into(carImage);
                Picasso.with(activity).load(item.getDriverPicPath()).into(driverImage);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileDialog.dismiss();
                    }
                });
                selectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new GetDistance().execute((float) MainActivity.userLocation.getLatitude(), (float) MainActivity.userLocation.getLongitude(), (float) item.getCarLat(), (float) item.getCarLng(), (float)0);
                        new GetDistance().execute((float) MainActivity.userLocation.getLatitude(), (float) MainActivity.userLocation.getLongitude(), (float) getIntent().getExtras().getDouble("destLat"), (float) getIntent().getExtras().getDouble("destLng"), (float)1);
                        pDialog = new ProgressDialog(activity);
                        pDialog.setMessage("Calculation distance...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        dialogBuilder = new AlertDialog.Builder(activity);
                        dialogBuilder.setCancelable(false);
                        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                                profileDialog.dismiss();
                            }
                        });
                        dialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    Intent toNavIntent = new Intent(WillzoMap.activity, NavMap.class);
                                    toNavIntent.putExtra("driverName", item.getDriverName());
                                    toNavIntent.putExtra("driverId", item.getDriverId());
                                    toNavIntent.putExtra("lat", (float) item.getCarLat());
                                    toNavIntent.putExtra("lng", (float) item.getCarLng());
                                    startActivity(toNavIntent);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String clientId = String.valueOf(MainActivity.userLocation.getLatitude() + MainActivity.userLocation.getLongitude());
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("clientId", clientId);
                                            editor.putString("driverId", item.getDriverId());
                                            editor.putFloat("driverLat", (float)item.getCarLat());
                                            editor.putFloat("driverLng", (float)item.getCarLng());
                                            editor.putFloat("destLat", Float.valueOf(destLat));
                                            editor.putFloat("destLng", Float.valueOf(destLng));
                                            editor.commit();

                                            new RequestRide().execute(item.getDriverId(), clientId, String.valueOf(MainActivity.userLocation.getLatitude()), String.valueOf(MainActivity.userLocation.getLongitude()), clientDest, destLat, destLng, clientName, clientNumber);

                                        }
                                    }, 2000);

                                }catch (Exception e){
                                    Toast.makeText(WillzoMap.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                profileDialog.show();
            }
        }*/

        return true;
    }
}
