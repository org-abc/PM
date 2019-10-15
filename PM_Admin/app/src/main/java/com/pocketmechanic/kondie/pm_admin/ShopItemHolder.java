package com.pocketmechanic.kondie.pm_admin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kondie on 2018/02/17.
 */

public class ShopItemHolder extends RecyclerView.ViewHolder {

    TextView shopName, hiddenShopId;
    Button shopStatusButton;
    public static Button clickedShopButton;
    ImageView shopDp;

    public ShopItemHolder(View likeViewItem){
        super(likeViewItem);

        shopName = (TextView) likeViewItem.findViewById(R.id.shop_name);
        hiddenShopId = (TextView) likeViewItem.findViewById(R.id.hidden_shop_id);
        shopStatusButton = (Button) likeViewItem.findViewById(R.id.shop_status);
        shopDp = (ImageView) likeViewItem.findViewById(R.id.shop_dp);

        likeViewItem.setOnClickListener(showUserProfile);
        shopStatusButton.setOnClickListener(changeShopStatus);
    }

    private View.OnClickListener changeShopStatus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickedShopButton = shopStatusButton;
            new ChangeShopStatus().execute((shopStatusButton.getText().toString().equalsIgnoreCase("active")) ? "deactivate" : "activate", hiddenShopId.getText().toString());
        }
    };

    private View.OnClickListener showUserProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*Intent gotoUserProfile = new Intent(MainActivity.activity, UserProfile.class);
            gotoUserProfile.putExtra("username", username.getText().toString());
            gotoUserProfile.putExtra("stringDP", getStringDP());
            MainActivity.activity.startActivity(gotoUserProfile);*/
        }
    };

    /*private String getStringDP(){
        userDp.buildDrawingCache();
        Bitmap userDpBitmap = userDp.getDrawingCache();

        ByteArrayOutputStream BAOutStream = new ByteArrayOutputStream();
        userDpBitmap.compress(Bitmap.CompressFormat.JPEG, 100, BAOutStream);
        byte[] byteArray = BAOutStream.toByteArray();
        String stringDP = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return (stringDP);
    }

    private View.OnClickListener goto_chat = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent gotoChatIntent = new Intent(UserListAdapter.activity, InsideDm.class);
            gotoChatIntent.putExtra("username", username.getText().toString());
            gotoChatIntent.putExtra("stringDP", getStringDP());
            UserListAdapter.activity.startActivity(gotoChatIntent);
        }
    };*/
}

