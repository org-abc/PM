package com.kondie.pocketmechanic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

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

public class CheckResponse extends AsyncTask<String, Void,String> {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String userEmail, requestId;

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            userEmail = prefs.getString("email", "");
            requestId = prefs.getString("requestId", "");
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/checkResponse.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("requestId", requestId);
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
            if (s.split(":").length >= 2){
                if (s.split(":")[0].equals("done") || s.split(":")[0].equals("canceled")){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("mechanicEmail", "");
                    editor.putString("requestId", "");
                    editor.commit();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavMap.activity).setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    if (s.split(":")[0].equals("done")){
                        if (!prefs.getString("requestId", "").equals("")) {
                            MainActivity.sendFeedback(prefs.getString("requestId", ""));
                        }
                        editor.putString("driverEmail", "");
                        editor.putString("requestId", "");
                        editor.putString("status", "free");
                        editor.commit();
                        CastReceiver.stopAlarm(MainActivity.activity, "response");
                        alertBuilder.setTitle("Arrived").setMessage("Your mechanic arrived").show();
                    }
                    else if (s.split(":")[0].equals("canceled")){
                        alertBuilder.setTitle("Canceled").setMessage("This request was canceled :(").show();
                    }
                }
                else {
                    String jsonStr = s.substring(s.indexOf(":") + 1);
                    JSONArray jsonArr = new JSONArray(jsonStr);
                    for (int c = 0; c < jsonArr.length(); c++) {
                        JSONObject jsonOb = jsonArr.getJSONObject(c);

                        editor.putString("mechanicEmail", jsonOb.getString("email"));
                        editor.putFloat("mechanicLat", (float) jsonOb.getDouble("lat"));
                        editor.putFloat("mechanicLng", (float) jsonOb.getDouble("lng"));
                        editor.putString("mechanicImagePath", jsonOb.getString("image_path"));
                        editor.putString("mechanicFname", jsonOb.getString("fname"));
                        editor.putString("mechanicLname", jsonOb.getString("lname"));
                        editor.putFloat("mechanicRating", (float) jsonOb.getDouble("rating"));
                        editor.putString("mechanicPhone", jsonOb.getString("phone"));
                        editor.commit();
                        NavMap.mechanicLat = (float) jsonOb.getDouble("lat");
                        NavMap.mechanicLng = (float) jsonOb.getDouble("lng");
                        NavMap.updateLoc();
                    }
                }
            }
            if (NavMap.mechanicDp.getVisibility() == View.GONE) {
                NavMap.startCheckingForMechanicResponse(s.split(":")[0]);
            }
        }catch (Exception e){
//            Toast.makeText(NavMap.activity, "++++=" + e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}