package com.kondie.pocketmechanic;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import androidx.annotation.Nullable;

public class CheckResponseService extends IntentService {

    public static final String ACTION_START = "ACTION_START";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public CheckResponseService(){
        super(CheckResponseService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            String action = intent.getAction();
            if (action.equals(ACTION_START)) {
                startBackgroundCheck();
            }
        }
        catch (Exception e){

        }
    }

    private void startBackgroundCheck(){
        try {
            prefs = MainActivity.activity.getSharedPreferences("PM", Context.MODE_PRIVATE);
            editor = prefs.edit();
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/checkResponse.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("orderId", prefs.getString("orderId", ""));
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

                String s = result.toString();
//                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

                String response = s.split(":")[0];
                if (response.equalsIgnoreCase("decline")){
                    new AlertDialog.Builder(this).setCancelable(false).setMessage("Your request was denied")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else if (s.split(":").length >= 2 && (response.equalsIgnoreCase("accept") || response.equalsIgnoreCase("delivered"))){

                    if (prefs.getString("driverEmail", "").equals("")) {
                        String jsonStr = s.substring(s.indexOf(":") + 1);
                        JSONArray jsonArr = new JSONArray(jsonStr);
                        for (int c = 0; c < jsonArr.length(); c++) {
                            JSONObject jsonOb = jsonArr.getJSONObject(c);

                            editor.putString("driverEmail", jsonOb.getString("email"));
                            editor.commit();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Conn Problems", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent createCheckIntent(Context context){

        Intent intent = new Intent(context, CheckResponseService.class);
        intent.setAction(ACTION_START);
        return intent;
    }
}
