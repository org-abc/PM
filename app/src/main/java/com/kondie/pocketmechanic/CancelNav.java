package com.kondie.pocketmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kondie on 2018/02/06.
 */

public class CancelNav extends AsyncTask<Void, Void, String> {

    SharedPreferences prefs;
    private String orderId;
    ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(NavMap.activity);
        pDialog.setMessage("Canceling request...");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            orderId = prefs.getString("orderId", "");
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/cancelReq.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("orderId", orderId);
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

        pDialog.dismiss();
        if (s.equals("congrats")){
            Toast.makeText(NavMap.activity, "Order canceled", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("driverEmail", "");
            editor.putString("orderId", "");
            editor.commit();
            NavMap.activity.finish();
        }else if (s.equalsIgnoreCase("sorry")){
            Toast.makeText(NavMap.activity, "You can't cancel a request after it has been accepted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(NavMap.activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
