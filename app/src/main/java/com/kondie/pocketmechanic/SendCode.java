package com.kondie.pocketmechanic;

import android.app.Activity;
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

public class SendCode extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String email, origin;
    private Activity activity;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            pDialog = new ProgressDialog(ForgotPassword.activity);
        }catch (Exception e){
            try{
                pDialog = new ProgressDialog(VerifAct.activity);
            }catch (Exception ex){
                try{
                    pDialog = new ProgressDialog(SignIn.activity);
                }catch (Exception exp){}
            }
        }
        if (pDialog != null) {
            pDialog.setMessage("Sending verification code...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            if (params[1].equals("forgot")){
                activity = ForgotPassword.activity;
            }else if (params[1].equals("signin")){
                activity = SignIn.activity;
            }
            else{
                activity = VerifAct.activity;
            }
            prefs = activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            email = params[0];
            origin = params[1];

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/sendCode.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", email);
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

        try{
            pDialog.dismiss();
        }catch (Exception e){}

        try {
            if (s.equalsIgnoreCase("congrats"))
            {
                if (origin.equals("signin")) {
                    editor.putString("email", email);
                    editor.commit();
                    Intent gotoForgotPass = new Intent(activity, ForgotPassword.class);
                    activity.startActivity(gotoForgotPass);
                }
            }
            else if (s.equalsIgnoreCase("not found")){
                Toast.makeText(activity, "This user doesn't exist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//            Toast.makeText(activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}
