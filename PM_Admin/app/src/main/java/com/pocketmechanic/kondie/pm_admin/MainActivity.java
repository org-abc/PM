package com.pocketmechanic.kondie.pm_admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Activity activity;
    Toolbar mainToolbar;
    public static RecyclerView shopList;
    public static List<ShopItem> shopItems;
    public static List<ShopItem> markedItems;
    public static ShopListAdapter shopListAdapter;
    private LinearLayoutManager linearLayMan;
    public static ProgressBar loadingShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        try {
            loadingShops = (ProgressBar) findViewById(R.id.loading_shops);
            mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
            setSupportActionBar(mainToolbar);
            shopList = (RecyclerView) findViewById(R.id.shop_list);
            shopItems = new ArrayList<>();
            linearLayMan = new LinearLayoutManager(activity);
            shopList.setLayoutManager(linearLayMan);
            shopListAdapter = new ShopListAdapter(activity, shopItems, shopList);
            shopList.setAdapter(shopListAdapter);

            new GetShops().execute("5050-00-00 00:00:00");
        }catch (Exception e)
        {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_shop_button:
                Intent gotoCreateShopIntent = new Intent(activity, CreateNewShopAct.class);
                startActivity(gotoCreateShopIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
