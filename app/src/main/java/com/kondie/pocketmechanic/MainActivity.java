package com.kondie.pocketmechanic;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback{

    public static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    public static Activity activity;
    static SharedPreferences prefs;
    public static String selectedShopImagePath;
    public static String selectedShopId;
    SharedPreferences.Editor editor;
    public static CoolLoading coolLoading;
    public static Location userLocation;
    public static LatLng selectedShopLocation;
    private GoogleApiClient gApiClient;
    private boolean isManual = false;
    private final int REQUEST_CHECK_SETTINGS = 1;
    private final int SET_LOCATION_MANUALLY = 2;
    private Dialog setLocationDialog;
    Dialog menuDialog;
    Dialog fullMapDialog;
    public static GoogleMap mMap;
    Button changeLocButt;
    Button keepLocButt;
    public static LatLng dropOffLoc;
    ProgressBar fullMapLoading;
    private PermissionUtils permissionUtils;
    public static final String CHANNEL_ID = "0";
    public static final String CHANNEL_NAME = "pm channel";
    public static final String CHANNEL_DESC = "Notification Channel";
    public static final int NOTIF_ID = 1;
    public static boolean isSelectedShopOpened;
    private Button trackButt;
    public static String lastHistoryDate = "5050-00-00 00:00:00";
    public static List<HistoryItem> historyItems;

    private TableRow towing, tires, engine, battery, brakes, electronics, lights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        prefs = getSharedPreferences("PM", Context.MODE_PRIVATE);
        editor = prefs.edit();
        setContentView(R.layout.activity_main);
        try {
            sendTokenAndGetUserInfo();
            if (!prefs.getString("fname", "").equals("")) {
                setUserDrawerInfo((NavigationView) findViewById(R.id.nav_view));
            }

            setGApiClient();
            createLocationRequest();

            setUpToolbar();

            coolLoading = new CoolLoading(activity);
            trackButt = findViewById(R.id.track_mechanic_butt);
            historyItems = new ArrayList<>();

            trackButt.setOnClickListener(startTracking);
            setUpOptionList();

            if (prefs.getString("requestId", "").equals("")) {
                openFullMap();
            } else {
                dropOffLoc = new LatLng(prefs.getFloat("dropOffLat", (float) 0.0), prefs.getFloat("dropOffLng", (float) 0.0));
                userLocation = new Location("");
                userLocation.setLongitude(dropOffLoc.longitude);
                userLocation.setLatitude(dropOffLoc.latitude);
            }
            createNotificationChannel();
        }catch (NullPointerException e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
        }
        try {
            if (getIntent().getExtras().getString("track") != null && getIntent().getExtras().getString("track").equals("yes")) {
                if (!prefs.getString("orderId", "").equals("")) {
                    Intent toNavIntent = new Intent(activity, NavMap.class);
                    activity.startActivity(toNavIntent);
                }
            }
        }catch (NullPointerException e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendTokenAndGetUserInfo(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (task.isSuccessful()){
                    new GetUserInfo().execute(task.getResult().getToken());
                }
                else{
                    new GetUserInfo().execute("");
                }
            }
        });
    }

    private View.OnClickListener startTracking = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (prefs.getString("status", "").equals("busy")){
                Intent toNavIntent = new Intent(activity, NavMap.class);
                activity.startActivity(toNavIntent);
            }
            else{
                Toast.makeText(MainActivity.this, "You don't have any active request", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void setUpToolbar(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpOptionList(){
        towing = findViewById(R.id.option_towing);
        tires = findViewById(R.id.option_tires);
        engine = findViewById(R.id.option_engine);
        battery = findViewById(R.id.option_battery);
        brakes = findViewById(R.id.option_brakes);
        electronics = findViewById(R.id.option_electronics);
        lights = findViewById(R.id.option_lights);

        towing.setOnClickListener(showForm);
        tires.setOnClickListener(showForm);
        engine.setOnClickListener(showForm);
        battery.setOnClickListener(showForm);
        brakes.setOnClickListener(showForm);
        electronics.setOnClickListener(showForm);
        lights.setOnClickListener(showForm);
    }

    public static String getImageName(){

        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmSS").format(new Date());
        String imageName = "PM_"+"_"+timeStamp;

        return imageName;
    }

    public static void addPicToGallery(String currentImagePath){

        Intent scanMediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File imageFile = new File(currentImagePath);
        Uri imageUri = Uri.fromFile(imageFile);
        scanMediaIntent.setData(imageUri);
        activity.sendBroadcast(scanMediaIntent);
    }

    private View.OnClickListener showForm = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent toFormIntent = new Intent(activity, RequestForm.class);
            toFormIntent.putExtra("issue", view.getTag().toString());
            startActivity(toFormIntent);
        }
    };

    private void checkPermissions(){

        ArrayList<String> permissions=new ArrayList<>();
        permissionUtils=new PermissionUtils(activity,this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions, "We need your GPS for delivery", REQUEST_CHECK_SETTINGS);
    }

    public static void setUserDrawerInfo(NavigationView navigationView){
        try {
            View headerView = navigationView.getHeaderView(0);
            TextView fullName = headerView.findViewById(R.id.menu_full_name);
            TextView viewProfile = headerView.findViewById(R.id.view_profile);
            ImageView menuDp = headerView.findViewById(R.id.menu_dp);

            fullName.setText(prefs.getString("lname", "") + " " + prefs.getString("fname", ""));
            Picasso.with(activity).load(prefs.getString("imagePath", "")).into(menuDp);

            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toProfileIntent = new Intent(activity, UserProfile.class);
                    activity.startActivity(toProfileIntent);
                }
            });
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static PendingIntent getDeleteIntent(Context context){

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setGApiClient(){
        try {
            gApiClient = new GoogleApiClient
                    .Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            setLocation();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Toast.makeText(MainActivity.this, "Connection suspended", Toast.LENGTH_LONG).show();
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
    }

    void setLocation(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                userLocation = location;
            }
        });
    }

    protected void createLocationRequest(){
        LocationRequest locationReq = new LocationRequest();
        locationReq.setInterval(10000);
        locationReq.setFastestInterval(5000);
        locationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationReq);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try{
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    checkPermissions();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                // Ignore the error.
                            } catch (ClassCastException ex) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            break;
                    }
            }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
//            case R.id.nav_payments:
//                break;
            case R.id.nav_history:
                Intent showHistoryIntent = new Intent(MainActivity.this, HistoryAct.class);
                startActivity(showHistoryIntent);
                break;
            case R.id.nav_help:
                Intent showHelpIntent = new Intent(MainActivity.this, HelpAct.class);
                startActivity(showHelpIntent);
                break;
            case R.id.nav_change_location:
                setLocationManuallyFunc();
                break;
            case R.id.nav_about:
                Intent showAboutIntent = new Intent(MainActivity.this, AboutAct.class);
                startActivity(showAboutIntent);
                break;
            case R.id.nav_settings:
                Intent showSettingsIntent = new Intent(MainActivity.activity, SettingsAct.class);
                startActivity(showSettingsIntent);
                break;
            case R.id.nav_logout:
                editor.clear();
                editor.commit();

                Intent toLoginIntent = new Intent(activity, SignIn.class);
                activity.startActivity(toLoginIntent);
                activity.finish();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS){
            if (resultCode == RESULT_OK){
                checkPermissions();
            }else if (resultCode == RESULT_CANCELED){
                setLocationDialog = new Dialog(activity);
                setLocationDialog.setContentView(R.layout.manual_location_dialog);
                setLocationDialog.setTitle("Location");
                setLocationDialog.setCancelable(false);

                Button setLocationButton = setLocationDialog.findViewById(R.id.set_location_button);
                setLocationButton.setOnClickListener(setLocationManually);

                setLocationDialog.show();
            }
        }else if (requestCode == SET_LOCATION_MANUALLY){
            if (resultCode == RESULT_OK){
                try {
                    Place currentPlace = PlacePicker.getPlace(activity, data);
                    LatLng latLng = currentPlace.getLatLng();
                    editor.putFloat("dropOffLat", (float)latLng.latitude);
                    editor.putFloat("dropOffLng", (float)latLng.longitude);
                    editor.commit();
                    dropOffLoc = latLng;
                    isManual = true;
                    userLocation = new Location("");
                    userLocation.setLatitude(dropOffLoc.latitude);
                    userLocation.setLongitude(dropOffLoc.longitude);
                    if (setLocationDialog != null && setLocationDialog.isShowing()) {
                        setLocationDialog.dismiss();
                    }
                    else if (menuDialog != null && menuDialog.isShowing()){
                        menuDialog.dismiss();
                    }
                }catch (Exception e){
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openFullMap() {
        try{
            fullMapDialog = new Dialog(activity);
            fullMapDialog.setContentView(R.layout.full_map_dialog);
            fullMapDialog.setCancelable(false);

            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dialog_map);
            changeLocButt = fullMapDialog.findViewById(R.id.dialog_change_loc);
            keepLocButt = fullMapDialog.findViewById(R.id.dialog_keep_loc);
            fullMapLoading = fullMapDialog.findViewById(R.id.full_map_loading);
            mapFrag.getMapAsync(onMapReadyCallback1());

            changeLocButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLocationManuallyFunc();
                    fullMapDialog.dismiss();
                }
            });
            keepLocButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dropOffLoc = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                    editor.putFloat("dropOffLat", (float)dropOffLoc.latitude);
                    editor.putFloat("dropOffLng", (float)dropOffLoc.longitude);
                    editor.commit();
                    fullMapDialog.dismiss();
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(fullMapDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            fullMapDialog.show();
            fullMapDialog.getWindow().setAttributes(lp);

        }catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserMarker(final GoogleMap googleMap) {

        long delayTime = 5000;
        if (MainActivity.userLocation != null) {

            keepLocButt.setVisibility(View.VISIBLE);
            changeLocButt.setVisibility(View.VISIBLE);
            fullMapLoading.setVisibility(View.GONE);
            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().title("Selected location").position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            setLocation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUserMarker(googleMap);
                }
            }, delayTime);
        }
    }

    public OnMapReadyCallback onMapReadyCallback1(){
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.clear();
                setUserMarker(mMap);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        };
    }

    private void setLocationManuallyFunc(){
        try {
            Intent manualIntent = new PlacePicker.IntentBuilder().build(activity);
            startActivityForResult(manualIntent, SET_LOCATION_MANUALLY);
        }catch (GooglePlayServicesRepairableException e){
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }catch (GooglePlayServicesNotAvailableException e){
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener setLocationManually = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setLocationManuallyFunc();
            fullMapDialog.dismiss();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(activity, "Map ready", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void PermissionGranted(int request_code) {
        if (userLocation == null){
            setLocation();
        }
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
