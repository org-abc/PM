package com.kondie.pocketmechanic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

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
    private String userEmail, orderId;

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            userEmail = prefs.getString("email", "");
            orderId = prefs.getString("orderId", "");
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/checkResponse.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("orderId", prefs.getString("orderId", ""));
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
            NavMap.startCheckingForDriverResponse(s.split(":")[0]);
            if (s.split(":").length >= 2){
                if (s.split(":")[0].equals("delivered") || s.split(":")[0].equals("cancel")){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("driverEmail", "");
                    editor.putString("orderId", "");
                    editor.commit();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavMap.activity).setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    showReviewDialog();
                                }
                            });
                    if (s.split(":")[0].equals("delivered")){
                        CastReceiver.stopAlarm(MainActivity.activity, "response");
                        alertBuilder.setTitle("Delivered").setMessage("This order was delivered. Enjoy your food :)").show();
                    }
                    else if (s.split(":")[0].equals("cancel")){
                        alertBuilder.setTitle("Canceled").setMessage("This order was canceled :(").show();
                    }
                }
                else {
                    String jsonStr = s.substring(s.indexOf(":") + 1);
                    JSONArray jsonArr = new JSONArray(jsonStr);
                    for (int c = 0; c < jsonArr.length(); c++) {
                        JSONObject jsonOb = jsonArr.getJSONObject(c);

                        editor.putString("driverEmail", jsonOb.getString("email"));
                        editor.putFloat("driverLat", (float) jsonOb.getDouble("lat"));
                        editor.putFloat("driverLng", (float) jsonOb.getDouble("lng"));
                        editor.putString("driverImagePath", jsonOb.getString("image_path"));
                        editor.putString("driverPhone", jsonOb.getString("phone"));
                        editor.commit();
                        NavMap.driverLat = (float) jsonOb.getDouble("lat");
                        NavMap.driverLng = (float) jsonOb.getDouble("lng");
                        NavMap.updateLoc();
                    }
                }
            }
        }catch (Exception e){
//            Toast.makeText(NavMap.activity, "++++=" + e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }


    private void showReviewDialog(){
        final Dialog ratingDialog;
        ratingDialog = new Dialog(NavMap.activity);
        if (ratingDialog != null) {
            ratingDialog.setContentView(R.layout.rating_dialog);

            TextView submitRatingButt = ratingDialog.findViewById(R.id.submit_rating);
            final EditText driverComment = ratingDialog.findViewById(R.id.driver_review);
            final EditText restaurantComment = ratingDialog.findViewById(R.id.restaurant_review);
            final RatingBar driverRating = ratingDialog.findViewById(R.id.driver_rating);
            final RatingBar restaurantRating = ratingDialog.findViewById(R.id.restaurant_rating);

            submitRatingButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SendFeedback().execute(orderId, String.valueOf(driverRating.getRating()), String.valueOf(restaurantRating.getRating()), driverComment.getText().toString(), restaurantComment.getText().toString());
                    ratingDialog.dismiss();
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(ratingDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            ratingDialog.show();
            ratingDialog.getWindow().setAttributes(lp);
        }
    }
}