package com.pocketmechanic.kondie.pocketmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

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
 * Created by kondie on 2018/02/09.
 */

public class SendReq extends AsyncTask <String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            pDialog = new ProgressDialog(NavMap.activity);
            pDialog.setMessage("Sending request...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){}
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = NavMap.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/saveReq.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("requester", prefs.getString("username", ""))
                    .appendQueryParameter("requested", params[0])
                    .appendQueryParameter("problem", params[1]);
            String query = builder.build().getEncodedQuery();

            OutputStream outStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
            writer.write(query);
            writer.flush();
            writer.close();
            outStream.close();
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                inStream.close();

                return result.toString();
            } else {
                return "conn problems";
            }

        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            pDialog.dismiss();

            if (s.equalsIgnoreCase("congrats"))
            {
                Toast.makeText(NavMap.activity, "waiting on mechanic response", Toast.LENGTH_LONG).show();
            }
            else
            {
                NavMap.activity.finish();
                Toast.makeText(MapActivity.activity, "Something went wrong" + s, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            NavMap.activity.finish();
            Toast.makeText(MainActivity.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}