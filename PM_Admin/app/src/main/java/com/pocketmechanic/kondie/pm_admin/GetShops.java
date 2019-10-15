package com.pocketmechanic.kondie.pm_admin;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
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

public class GetShops extends AsyncTask <String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.loadingShops.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(Constants.SPECIALS_WEBSITE + "/getMechanics.php");
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

        MainActivity.loadingShops.setVisibility(View.GONE);
        try{
            JSONArray shopsArr = new JSONArray(s);
            for (int c=0; c < shopsArr.length(); c++){
                JSONObject shopOb = shopsArr.getJSONObject(c);
                ShopItem item = new ShopItem();
                item.setShopDpPath(shopOb.getString("p_pic"));
                item.setShopName(shopOb.getString("username"));
                item.setShopId(shopOb.getString("id"));
                item.setShopStatus(shopOb.getString("status"));
                item.setLastUpdated(shopOb.getString("time_stamp"));

                int existRV = exist(item.getShopId());
                if (existRV == -1) {
                    MainActivity.shopItems.add(item);
                }
            }
            MainActivity.shopListAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Toast.makeText(MainActivity.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }

    private int exist(String id){

        for (int c=0; c<MainActivity.shopItems.size(); c++){
            ShopItem item = MainActivity.shopItems.get(c);
            if (item.getShopId().equals(id)){
                return (c);
            }
        }
        return (-1);
    }
}
