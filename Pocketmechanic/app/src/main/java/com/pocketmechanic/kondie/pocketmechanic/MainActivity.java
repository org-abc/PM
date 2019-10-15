package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    TableRow flatTire, engine, battery,brakes, electronics, lights, towing;
    public static Activity activity;
    public static Location userLocation;
    private GoogleApiClient gApiClient;
    LocationHelper locationHelper;
    DirHelper dirHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationHelper = new LocationHelper(activity);
        dirHelper = new DirHelper(activity);
        locationHelper.checkpermission();
        dirHelper.check_permission();

        flatTire = (TableRow) findViewById(R.id.option_tires);
        engine = (TableRow) findViewById(R.id.option_engine);
        battery = (TableRow) findViewById(R.id.option_battery);
        brakes = (TableRow) findViewById(R.id.option_brakes);
        electronics = (TableRow) findViewById(R.id.option_electronics);
        lights = (TableRow) findViewById(R.id.option_lights);
        towing = (TableRow) findViewById(R.id.option_towing);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }

        flatTire.setOnClickListener(fixTire);
        engine.setOnClickListener(fixEngine);
        battery.setOnClickListener(fixBattery);
        brakes.setOnClickListener(fixBrakes);
        electronics.setOnClickListener(fixElectronics);
        lights.setOnClickListener(fixLights);
        towing.setOnClickListener(tow);

        if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
        {
            openSesami();
        }
    }

    void openSesami(){
        SharedPreferences prefs = getSharedPreferences("PM", MODE_PRIVATE);
        Intent gotoNav = new Intent(activity, NavMap.class);
        Toast.makeText(activity, prefs.getString("mechanic", ""), Toast.LENGTH_SHORT).show();
        gotoNav.putExtra("username", prefs.getString("mechanic", ""));
        gotoNav.putExtra("problem", prefs.getString("problem", ""));
        gotoNav.putExtra("lat", prefs.getString("lat", ""));
        gotoNav.putExtra("lng", prefs.getString("lng", ""));
        gotoNav.putExtra("status", "busy");
        gotoNav.putExtra("id", prefs.getString("id", ""));
        startActivity(gotoNav);
    }

    View.OnClickListener tow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "tow");
                startActivity(showHelp);
            }
        }
    };
    View.OnClickListener fixTire = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "tires");
                startActivity(showHelp);
            }
        }
    };
    View.OnClickListener fixEngine = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "engine");
                startActivity(showHelp);
            }
        }
    };
    View.OnClickListener fixBattery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "battery");
                startActivity(showHelp);
            }
        }
    };
    View.OnClickListener fixBrakes = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "brakes");
                startActivity(showHelp);
            }
        }
    };
    View.OnClickListener fixElectronics = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "electronics");
                startActivity(showHelp);
            }
        }
    };
    View.OnClickListener fixLights = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getExtras().getString("status").equalsIgnoreCase("busy"))
            {
                openSesami();
            }
            else {
                Toast.makeText(activity, "Loading", Toast.LENGTH_SHORT).show();
                Intent showHelp = new Intent(activity, MapActivity.class);
                showHelp.putExtra("problem", "lights");
                startActivity(showHelp);
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "to do___", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
