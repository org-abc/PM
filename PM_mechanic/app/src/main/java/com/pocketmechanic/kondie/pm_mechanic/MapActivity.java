package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kondie on 2018/02/27.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Toolbar toolbar;
    private SharedPreferences prefs;
    private GoogleMap gMap;
    RecyclerView reqList;
    public static List<ReqItem> clientItems;
    public static ReqListAdapter2 reqListAdapter2;
    private LinearLayoutManager linearLayMan2;
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
        prefs = getSharedPreferences("PM_mechanic", MODE_PRIVATE);
        clientItems = new ArrayList<>();
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mechanics_map);
        mapFrag.getMapAsync(MapActivity.this);

        reqList = (RecyclerView) findViewById(R.id.all_available_mechanic_list);
        linearLayMan2 = new LinearLayoutManager(activity);
        linearLayMan2.setOrientation(LinearLayoutManager.HORIZONTAL);
        reqList.setLayoutManager(linearLayMan2);
        reqListAdapter2 = new ReqListAdapter2(activity, clientItems, reqList);
        reqList.setAdapter(reqListAdapter2);

        // clientItems = MainActivity.reqItems;
        for (int c = 0; c < MainActivity.reqItems.size(); c++) {
            ReqItem item = new ReqItem();
            item.setDistance(MainActivity.reqItems.get(c).getDistance());
            item.setLat(MainActivity.reqItems.get(c).getLat());
            item.setName(MainActivity.reqItems.get(c).getName());
            item.setProblem(MainActivity.reqItems.get(c).getProblem());
            item.setLng(MainActivity.reqItems.get(c).getLng());

            clientItems.add(item);
        }
        reqListAdapter2.notifyDataSetChanged();

        //new GetAllAvailableMechanics().execute();
    }

   /* private void setUserMarker(final GoogleMap googleMap){

        long delayTime = 5000;
        float zoomLevel = 0;

        if (MainActivity.userLocation != null){

            for(int c=0; c<mechanicItems.size(); c++){
                MechanicItem item = mechanicItems.get(c);
                float tempZoomLevel = (float) getZoomForMiters(MainActivity.getDistanceInMeters(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude(), item.getCarLat(), item.getCarLng()), item.getCarLat());
                if (tempZoomLevel > zoomLevel){
                    zoomLevel = tempZoomLevel;
                }
            }
            LatLng userLoc = new LatLng(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().title(clientName).position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, zoomLevel/2));
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUserMarker(googleMap);
                }
            }, delayTime);
        }
    }*/

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

    private void addMarkersOnMap(){

        if (clientItems.size() > 0) {
            for (int c = 0; c < clientItems.size(); c++) {

                ReqItem item = clientItems.get(c);
                LatLng carLoc = new LatLng(item.getLat(), item.getLng());
                gMap.addMarker(new MarkerOptions().position(carLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)));
                gMap.setOnMarkerClickListener(MapActivity.this);
            }
            //setUserMarker(gMap);
        }else{
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
