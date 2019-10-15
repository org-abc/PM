package com.pocketmechanic.kondie.pm_admin;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
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
 * Created by kondie on 2018/02/20.
 */

public class SaveNewShop extends AsyncTask<String, Void, String> {

    ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try{
            pDialog = new ProgressDialog(CreateNewShopAct.activity);
            pDialog.setMessage("Saving store...");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }catch (Exception e){
            Toast.makeText(CreateNewShopAct.activity, "No loading...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(Constants.SPECIALS_WEBSITE + "/saveNewMechanic.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("shopId", params[0])
                    .appendQueryParameter("pass", params[1].replace("'", "''"))
                    .appendQueryParameter("confirmPass", params[2].replace("'", "''"))
                    .appendQueryParameter("pin", params[3])
                    .appendQueryParameter("iconData", params[4])
                    .appendQueryParameter("iconName", params[5].replace("'", "''"))
                    .appendQueryParameter("name", params[6].replace("'", "''"))
                    .appendQueryParameter("lat", params[7])
                    .appendQueryParameter("lng", params[8])
                    .appendQueryParameter("website", params[9].replace("'", "''"))
                    .appendQueryParameter("username", params[10])
                    .appendQueryParameter("fname", params[11])
                    .appendQueryParameter("lname", params[12])
                    .appendQueryParameter("email", params[13])
                    .appendQueryParameter("phone", params[14])
                    .appendQueryParameter("bio", params[15])
                    .appendQueryParameter("minimum_fee", params[16]);
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
        if (s.equals("wrongPin")){
            CreateNewShopAct.wrongPin.setVisibility(View.VISIBLE);
        }else {
            if (s.equalsIgnoreCase("congrats")) {
                CreateNewShopAct.activity.finish();
                Toast.makeText(MainActivity.activity, s, Toast.LENGTH_SHORT).show();
                new GetShops().execute("5050-00-00 00:00:00");
            }
        }
    }
}
