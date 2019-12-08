package com.kondie.pocketmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class ChangePassword extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String pass;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(ForgotPassword.activity);
        pDialog.setMessage("Changing password...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = ForgotPassword.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            pass = params[1];

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/changePassword.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", prefs.getString("email", ""))
                    .appendQueryParameter("password", params[1])
                    .appendQueryParameter("code", params[0]);
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
                editor.putString("password", pass);
                editor.commit();
                Intent gotoMain = new Intent(ForgotPassword.activity, MainActivity.class);
                gotoMain.putExtra("status", "free");
                ForgotPassword.activity.startActivity(gotoMain);
                ForgotPassword.activity.finish();
                SignIn.activity.finish();
            }
            else if (s.equals("sorry")){
                Toast.makeText(ForgotPassword.activity, "Make sure the code is correct", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ForgotPassword.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}
