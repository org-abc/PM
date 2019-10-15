package com.pocketmechanic.kondie.pocketmechanic;

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
 * Created by kondie on 2018/07/05.
 */

public class SendBackResponse extends AsyncTask <String, Void, String> {

    ProgressDialog pDialog;
    SharedPreferences prefs;
    String resp;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(NavMap.activity);
        pDialog.setMessage("Getting requests...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            resp = params[1];
            prefs = NavMap.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/sendBackResponse.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("client", prefs.getString("username", ""))
                    .appendQueryParameter("mechanic", params[0])
                    .appendQueryParameter("response", params[1])
                    .appendQueryParameter("id", params[2]);
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
                return ("Connection Failed " + String.valueOf(resCode));
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

        pDialog.dismiss();

        if (resp.equalsIgnoreCase("declined") && s.equalsIgnoreCase("congrats"))
        {
            Toast.makeText(NavMap.activity, "Request declined", Toast.LENGTH_SHORT).show();
            NavMap.activity.finish();
        }
        else if (resp.equalsIgnoreCase("accepted") && s.equalsIgnoreCase("congrats"))
        {
            Toast.makeText(NavMap.activity, "Request accepted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(NavMap.activity, "Something went wrong" + s, Toast.LENGTH_SHORT).show();
        }
    }
}
