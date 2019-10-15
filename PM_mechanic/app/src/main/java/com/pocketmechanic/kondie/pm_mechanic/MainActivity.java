package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static Activity activity;
    public static RecyclerView reqList;
    public static List<ReqItem> reqItems;
    public static ReqListAdapter reqListAdapter;
    private LinearLayoutManager linearLayMan;
    public static Location userLocation;
    private GoogleApiClient gApiClient;
    LocationHelper locationHelper;
    DirHelper dirHelper;
    static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences("PMM", MODE_PRIVATE);
        locationHelper = new LocationHelper(activity);
        dirHelper = new DirHelper(activity);
        locationHelper.checkpermission();
        dirHelper.check_permission();

        try {
            gApiClient = new GoogleApiClient
                    .Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            userLocation = LocationServices.FusedLocationApi.getLastLocation(gApiClient);
                            //lat = userLocation.getLatitude();
                            //lng = userLocation.getLongitude();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        reqList = (RecyclerView) findViewById(R.id.req_list);
        fab.setOnClickListener(showOnMap);

        linearLayMan = new LinearLayoutManager(activity);
        linearLayMan.setOrientation(LinearLayoutManager.VERTICAL);
        reqList.setLayoutManager(linearLayMan);
        reqItems = new ArrayList<>();
        reqListAdapter = new ReqListAdapter(activity, reqItems, reqList);
        reqList.setAdapter(reqListAdapter);


        if (!prefs.getString("status", "").equalsIgnoreCase("busy")) {
            new GetRequests().execute();
        }else{
            openSesami();
        }

    }

    View.OnClickListener showOnMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent gotoMap = new Intent(activity, MapActivity.class);
            startActivity(gotoMap);
        }
    };

    public static void openSesami()
    {
        try {
            Intent gotoNavMap = new Intent(MainActivity.activity, NavMap.class);
            gotoNavMap.putExtra("username", prefs.getString("client", ""));
            gotoNavMap.putExtra("lat", prefs.getString("lat", ""));
            gotoNavMap.putExtra("lng", prefs.getString("lng", ""));
            gotoNavMap.putExtra("phone", prefs.getString("phone", ""));
            gotoNavMap.putExtra("id", prefs.getString("id", ""));
            activity.startActivity(gotoNavMap);
        }catch (Exception e)
        {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static double getDistanceInMeters(double startLat, double startLng, double endLat, double endLng){

        Location startLocation = new Location("");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLng);
        Location endLocation = new Location("");
        endLocation.setLatitude(endLat);
        endLocation.setLongitude(endLng);
        double distanceInMeters = startLocation.distanceTo(endLocation);

        return (distanceInMeters);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
