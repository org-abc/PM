package com.kondie.pocketmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUserProfile extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String fname, lname, phone, imageData, imageName;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(UpdateProfile.activity);
        pDialog.setMessage("Saving details...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = UpdateProfile.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            fname = params[0];
            lname = params[1];
            phone = params[2];
            imageData = params[3];
            imageName = params[4];

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/updateProfile.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("fname", fname)
                    .appendQueryParameter("lname", lname)
                    .appendQueryParameter("phone", phone)
                    .appendQueryParameter("email", prefs.getString("email", ""));
            if (!imageName.equals("")){
                builder.appendQueryParameter("imageData", imageData)
                        .appendQueryParameter("imageName", imageName);
            }
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

            JSONObject userOb = new JSONObject(s);
            editor.putString("fname", userOb.getString("fname"));
            editor.putString("lname", userOb.getString("lname"));
            editor.putString("imagePath", userOb.getString("image_path"));
            editor.putString("phone", userOb.getString("phone"));
            editor.putString("email", userOb.getString("email"));
            editor.commit();

            Toast.makeText(UpdateProfile.activity, "Changes saved", Toast.LENGTH_SHORT).show();
            UpdateProfile.activity.finish();
        } catch (Exception e) {
            Toast.makeText(UpdateProfile.activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
