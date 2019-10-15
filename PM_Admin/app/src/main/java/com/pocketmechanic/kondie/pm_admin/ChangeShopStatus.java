package com.pocketmechanic.kondie.pm_admin;

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
 * Created by kondie on 2018/07/02.
 */

public class ChangeShopStatus extends AsyncTask <String, Void, String> {

    private String act;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.loadingShops.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String[] params) {

        try {
            act = params[0];
            URL url = new URL(Constants.SPECIALS_WEBSITE + "/changeMechanicStatus.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("activity", act)
                    .appendQueryParameter("shopId", params[1]);
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
                return ("Connection Failed");
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
        if (s.equalsIgnoreCase("congrats")){
            ShopItemHolder.clickedShopButton.setText((act.equalsIgnoreCase("activate") ? "active" : "not active"));
            if (act.equalsIgnoreCase("activate")){
                ShopItemHolder.clickedShopButton.setBackgroundResource(R.drawable.shop_button_backg);
            }else{
                ShopItemHolder.clickedShopButton.setBackgroundResource(R.drawable.not_active_backg);
            }
            Toast.makeText(MainActivity.activity, "Shop status changed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.activity, s, Toast.LENGTH_SHORT).show();
        }
    }
}
