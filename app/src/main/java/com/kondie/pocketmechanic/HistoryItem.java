package com.kondie.pocketmechanic;

public class HistoryItem {

    String driverName, orderDetails, orderAmount, deliveryFee, dateCreated, id, shopName, status, orderName;
    double lat, lng, shopLat, shopLng;

    public HistoryItem(){}

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShopLat(double shopLat) {
        this.shopLat = shopLat;
    }

    public void setShopLng(double shopLng) {
        this.shopLng = shopLng;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }


    public String getDeliveryFee() {
        return deliveryFee;
    }

    public double getShopLat() {
        return shopLat;
    }

    public String getShopName() {
        return shopName;
    }

    public String getId() {
        return id;
    }

    public double getShopLng() {
        return shopLng;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getDriverName() {
        return driverName;
    }

}
