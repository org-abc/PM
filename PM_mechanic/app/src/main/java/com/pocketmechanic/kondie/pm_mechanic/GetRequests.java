package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pocketmechanic.kondie.pm_mechanic.Constants;
import com.pocketmechanic.kondie.pm_mechanic.MainActivity;

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

public class GetRequests extends AsyncTask <String, Void, String> {

    ProgressDialog pDialog;
    SharedPreferences prefs;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(MainActivity.activity);
        pDialog.setMessage("Getting requests...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PMM", Context.MODE_PRIVATE);
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getRequests.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("mechanic", prefs.getString("username", ""));
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
            JSONObject allData = new JSONObject(s);
            String reqsStr = allData.getString("reqs");
            String infosStr = allData.getString("infos");
            JSONArray reqArr = new JSONArray(reqsStr);
            JSONArray infosArr = new JSONArray(infosStr);
            for (int c=0; c < reqArr.length(); c++){
                JSONObject reqOb = reqArr.getJSONObject(c);
                JSONObject infoOb = infosArr.getJSONObject(c);
                ReqItem item = new ReqItem();
                item.setProblem(reqOb.getString("problem"));
                item.setName(reqOb.getString("requester"));
                item.setLat(infoOb.getDouble("lat"));
                item.setLng(infoOb.getDouble("lng"));
                item.setPhone(infoOb.getString("phone"));
                item.setId(String.valueOf(reqOb.getInt("id")));
                if (MainActivity.userLocation != null)
                {
                    item.setDistance(String.valueOf(MainActivity.getDistanceInMeters(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude(), item.getLat(), item.getLng())));
                }

                MainActivity.reqItems.add(item);
            }
            MainActivity.reqListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(MainActivity.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}
