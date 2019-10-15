package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
 * Created by kondie on 2018/07/05.
 */

public class GetMechanics extends AsyncTask <String, Void, String> {

    ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(MapActivity.activity);
        pDialog.setMessage("Getting help...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getMechanics.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("lastUpdated", params[0]);
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
        try{
            JSONArray shopsArr = new JSONArray(s);
            for (int c=0; c < shopsArr.length(); c++){
                JSONObject shopOb = shopsArr.getJSONObject(c);
                MechanicItem item = new MechanicItem();
                item.setMechanicImagePath(shopOb.getString("p_pic"));
                item.setMechanicName(shopOb.getString("username"));
                item.setMechanicId(shopOb.getInt("id"));
                item.setLat(shopOb.getDouble("lat"));
                item.setLng(shopOb.getDouble("lng"));
                item.setMinFee(shopOb.getDouble("min_fee"));
                item.setRating(shopOb.getDouble("rating"));
                item.setPhone(shopOb.getInt("phone"));

                MapActivity.mechanicItems.add(item);
            }
            MapActivity.mechanicListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(MapActivity.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}
