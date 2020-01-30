package com.kondie.pocketmechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * Created by kondie on 2018/07/05.
 */

public class RequestForAMechanic extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(RequestForm.activity);
        progressDialog.setTitle("Sending request...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = RequestForm.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/request.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("comment", params[0])
                    .appendQueryParameter("makeAndModel", params[1])
                    .appendQueryParameter("issue", params[2])
                    .appendQueryParameter("serviceFee", params[3])
                    .appendQueryParameter("payment", "cash")
                    .appendQueryParameter("userLat", String.valueOf(MainActivity.dropOffLoc.latitude))
                    .appendQueryParameter("userLng", String.valueOf(MainActivity.dropOffLoc.longitude))
                    .appendQueryParameter("email", prefs.getString("email", ""));
            String query = builder.build().getEncodedQuery();

            OutputStream outStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

            writer.write(query);
            writer.flush();
            writer.close();
            outStream.close();
            conn.connect();

            int resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK)
            {
                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
                inStream.close();
                return (result.toString());
            }
            else
            {
                return ("Connection Failed " + resCode);
            }
        }
        catch(Exception e)
        {
            return ("Conn, " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try{
            progressDialog.dismiss();
            if (s.equals("outOfRange")){
                Toast.makeText(RequestForm.activity, "You're out of range for our services. SORRY", Toast.LENGTH_LONG).show();
            } else if (s.split(":")[0].equals("congrats")) {
                editor = prefs.edit();
                editor.putString("status", "busy");
                editor.putString("requestId", s.split(":")[1]);
                editor.commit();
//                CastReceiver.setAlarm(MainActivity.activity, "response");
                new AlertDialog.Builder(RequestForm.activity).setCancelable(false).setMessage("Your mechanic will be in contact with you in a few minutes")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent toNavIntent = new Intent(MainActivity.activity, NavMap.class);
                                RequestForm.activity.finish();
                                MainActivity.activity.startActivity(toNavIntent);
                            }
                        }).show();
            }else{
                Toast.makeText(RequestForm.activity, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(RequestForm.activity, "Sorry, try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
