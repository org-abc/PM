package com.kondie.pocketmechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendFeedback extends AsyncTask<String, Void, String> {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    String orderId, driverRating, driverReview, restaurantRating, restaurantReview;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            pDialog = new ProgressDialog(NavMap.activity);
        }catch (Exception e){

        }
        if (pDialog != null) {
            pDialog.setMessage("Sending feedback...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = NavMap.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            orderId = params[0];
            driverRating = params[1];
            restaurantRating = params[2];
            driverReview = params[3];
            restaurantReview = params[4];

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/sendFeedback.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("orderId", params[0])
                    .appendQueryParameter("driverRating", params[1])
                    .appendQueryParameter("restaurantRating", params[2])
                    .appendQueryParameter("driverReview", params[3])
                    .appendQueryParameter("restaurantReview", params[4]);
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
        }catch (Exception e){}

        try {
            if (s.equals("congrats")){
                NavMap.activity.finish();
            }else{
                new AlertDialog.Builder(NavMap.activity).setCancelable(false).setMessage("Failed to send the feedback. Retry?").setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SendFeedback().execute(orderId, driverRating, restaurantRating, driverReview, restaurantReview);
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavMap.activity.finish();
                            }
                        }).show();
            }
        } catch (Exception e) {
//            Toast.makeText(NavMap.activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
