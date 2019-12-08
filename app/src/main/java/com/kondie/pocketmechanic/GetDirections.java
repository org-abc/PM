package com.kondie.pocketmechanic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kondie on 2018/02/11.
 */

public class GetDirections extends AsyncTask <Float, Void, String> {

    float reqNum;
    SharedPreferences prefs;

    @Override
    protected String doInBackground(Float... params) {
        try{
            prefs = MainActivity.activity.getSharedPreferences("Willzo", Context.MODE_PRIVATE);
            reqNum = params[4];
            String start = "origin=" + params[0] + "," + params[1];
            String dest = "destination=" + params[2] + "," + params[3];
            String sensor = "sensor=false";
            String parameters = start + "&" + dest + "&" + sensor;
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=AIzaSyAlD9l56Dkx3J8KjDFjPfqpboXIoFfwsHY");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){

                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder result = new StringBuilder();
                String line;

                while((line=reader.readLine()) != null){

                    result.append(line);
                }
                inStream.close();
                return result.toString();
            }else{
                return "Response code Prob";
            }

        }catch (Exception e){
            return "Oooops "+e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try{
            List<LatLng> points;
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();
            JSONObject jsonOb = new JSONObject(s);
            String duration = ((JSONObject) ((JSONObject) jsonOb.getJSONArray("routes").get(0)).getJSONArray("legs").get(0)).getJSONObject("duration").getString("text");
//            NavMap.toolbar.setTitle(duration);

            List<List<HashMap<String, String>>> result = new DirectionsJSONParser().parser(jsonOb);

            for (int c=0; c<result.size(); c++){

                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(c);
                for (int i=0; i<path.size(); i++){
                    HashMap<String, String> point = path.get(i);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(8);
                if(reqNum == 0) {
                    lineOptions.color(Color.RED);
                    new GetDirections().execute((float) MainActivity.userLocation.getLatitude(), (float) MainActivity.userLocation.getLongitude(), prefs.getFloat("destLat", 0), prefs.getFloat("destLng", 0), (float) 1);
                }else{
                    lineOptions.color(Color.BLUE);
                }
            }
            NavMap.lineOptions = lineOptions;
//            NavMap.navGoogleMap.addPolyline(lineOptions);
            CastReceiver.setAlarm(NavMap.activity.getBaseContext(), "location");

        }catch (Exception e){
//            Toast.makeText(MainActivity.activity, "<><><><><>>>>" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
