package com.kondie.pocketmechanic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by kondie on 2018/02/12.
 */

public class SettingsAct extends AppCompatActivity {

    CheckBox normalBox, hybridBox, satelliteBox, terrainBox;
    Toolbar settingsToolbar;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String mapType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_act);
        prefs = getSharedPreferences("PM", MODE_PRIVATE);
        editor = prefs.edit();

        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapType = prefs.getString("mapType", "");
        normalBox = findViewById(R.id.normal_map_type);
        hybridBox = findViewById(R.id.hybrid_map_type);
        satelliteBox = findViewById(R.id.satellite_map_type);
        terrainBox = findViewById(R.id.terrain_map_type);

        setMapType();
        normalBox.setOnClickListener(chooseNormal);
        hybridBox.setOnClickListener(chooseHybrid);
        satelliteBox.setOnClickListener(chooseSatellite);
        terrainBox.setOnClickListener(chooseTerrain);
    }

    private View.OnClickListener chooseNormal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor.putString("mapType", "normal");
            editor.commit();
            normalBox.setChecked(true);
            hybridBox.setChecked(false);
            satelliteBox.setChecked(false);
            terrainBox.setChecked(false);
        }
    };

    private View.OnClickListener chooseHybrid = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor.putString("mapType", "hybrid");
            editor.commit();
            normalBox.setChecked(false);
            hybridBox.setChecked(true);
            satelliteBox.setChecked(false);
            terrainBox.setChecked(false);
        }
    };

    private View.OnClickListener chooseSatellite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor.putString("mapType", "satellite");
            editor.commit();
            normalBox.setChecked(false);
            hybridBox.setChecked(false);
            satelliteBox.setChecked(true);
            terrainBox.setChecked(false);
        }
    };

    private View.OnClickListener chooseTerrain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor.putString("mapType", "terrain");
            editor.commit();
            normalBox.setChecked(false);
            hybridBox.setChecked(false);
            satelliteBox.setChecked(false);
            terrainBox.setChecked(true);
        }
    };

    private void setMapType(){

        switch (mapType){

            case "normal":
                normalBox.setChecked(true);
                break;
            case "hybrid":
                hybridBox.setChecked(true);
                break;
            case "satellite":
                satelliteBox.setChecked(true);
                break;
            case "terrain":
                terrainBox.setChecked(true);
                break;
            default:
                normalBox.setChecked(true);
        }
    }
}
