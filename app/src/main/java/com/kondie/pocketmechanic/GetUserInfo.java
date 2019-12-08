package com.kondie.pocketmechanic;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserInfo extends AsyncTask<String, Void, String> {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String email, pass;

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            email = prefs.getString("email", "");
            pass = prefs.getString("password", "");

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getUserInfo.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", email)
                    .appendQueryParameter("password", pass);
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
            JSONObject userOb = new JSONObject(s);
            editor.putString("fname", userOb.getString("fname"));
            editor.putString("lname", userOb.getString("lname"));
            editor.putString("imagePath", userOb.getString("image_path"));
            editor.putString("phone", userOb.getString("phone"));
            editor.putString("email", userOb.getString("email"));
            editor.commit();
            MainActivity.setUserDrawerInfo((NavigationView) MainActivity.activity.findViewById(R.id.nav_view));
        } catch (Exception e) {
        }
    }
}
