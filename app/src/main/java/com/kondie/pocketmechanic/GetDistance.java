package com.kondie.pocketmechanic;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kondie on 2018/02/05.
 */

public class GetDistance extends AsyncTask<Float, Void, String> {

    float reqNum;

    @Override
    protected String doInBackground(Float... params) {
        try{
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
            JSONObject jsonOb = new JSONObject(s);
            int distance = Integer.valueOf(((JSONObject) ((JSONObject) jsonOb.getJSONArray("routes").get(0)).getJSONArray("legs").get(0)).getJSONObject("distance").getString("value"));
            if(reqNum == 0) {
            }
            else if (reqNum == 1){
            }

        }catch (Exception e){
            if (reqNum == 0) {
            }
            else if (reqNum == 1) {
                Toast.makeText(MainActivity.activity, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
