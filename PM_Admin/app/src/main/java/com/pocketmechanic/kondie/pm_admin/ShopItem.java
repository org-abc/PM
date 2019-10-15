package com.pocketmechanic.kondie.pm_admin;

/**
 * Created by kondie on 2018/02/17.
 */

public class ShopItem {

    String shopId, shopName, shopDpPath, shopStatus, lastUpdated;

    public ShopItem(){}

    public ShopItem(String shopId, String shopName, String shopDpPath, String shopStatus, String lastUpdated){

        this.shopId = shopId;
        this.shopName = shopName;
        this.shopDpPath = shopDpPath;
        this.shopStatus = shopStatus;
        this.lastUpdated = lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopDpPath(String shopDpPath) {
        this.shopDpPath = shopDpPath;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopDpPath() {
        return shopDpPath;
    }

    public String getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }
}

