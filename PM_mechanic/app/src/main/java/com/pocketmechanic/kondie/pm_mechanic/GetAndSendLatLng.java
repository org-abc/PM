package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pocketmechanic.kondie.pm_mechanic.Constants;
import com.pocketmechanic.kondie.pm_mechanic.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kondie on 2018/07/05.
 */

public class GetAndSendLatLng extends AsyncTask <String, Void, String> {

    SharedPreferences prefs;
    private String action;

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PMM", Context.MODE_PRIVATE);
            action = params[1];
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getAndSendLatLng.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("lat", String.valueOf(MainActivity.userLocation.getLatitude()))
                    .appendQueryParameter("lng", String.valueOf(MainActivity.userLocation.getLongitude()))
                    .appendQueryParameter("action", action)
                    .appendQueryParameter("client", params[0])
                    .appendQueryParameter("mechanic", prefs.getString("username", ""));
            String query = builder.build().getEncodedQuery();

            OutputStream outStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

            writer.write(query);
            writer.flush();
            writer.close();
            outStream.close();
            conn.connect();

            int resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK)
            {
                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
                inStream.close();
                return (result.toString());
            }
            else
            {
                return ("Connection Failed " + String.valueOf(resCode));
            }
        }
        catch(Exception e)
        {
            return ("Conn, " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (action.equalsIgnoreCase("both") || action.equalsIgnoreCase("get")) {
            try {
                JSONArray locArr = new JSONArray(s);
                JSONObject locOb = locArr.getJSONObject(0);
                NavMap.lat = locOb.getDouble("lat");
                NavMap.lng = locOb.getDouble("lng");
                Toast.makeText(NavMap.activity, "refreshed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(NavMap.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
            }
            NavMap.refreshLoc();
        }
    }
}
