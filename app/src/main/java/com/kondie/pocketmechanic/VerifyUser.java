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

/**
 * Created by kondie on 2018/02/09.
 */

public class VerifyUser extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String email, pass;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(VerifAct.activity);
        pDialog.setMessage("Saving details...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = VerifAct.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            email = params[1];
            pass = params[2];

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/verifyUser.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("code", params[0])
                    .appendQueryParameter("email", params[1]);
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
                Toast.makeText(VerifAct.activity, "Done", Toast.LENGTH_SHORT).show();
                editor.putString("email", email);
                editor.putString("password", pass);
                editor.putString("signUpState", "done");
                editor.commit();
                Intent gotoMain = new Intent(VerifAct.activity, MainActivity.class);
                if (s.equals("CONGRATS")){
                    gotoMain.putExtra("status", "busy");
                }else{
                    gotoMain.putExtra("status", "free");
                }
                VerifAct.activity.startActivity(gotoMain);
                VerifAct.activity.finish();
            }
            else
            {
                Toast.makeText(VerifAct.activity, "Something went wrong."+s+"__", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(VerifAct.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}
