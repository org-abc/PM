package com.kondie.pocketmechanic;

public class HistoryItem {

    String mechanicName, issue, orderAmount, serviceFee, dateCreated, id, status, orderName;
    double lat, lng, shopLng;

    public HistoryItem(){}

    public void setDeliveryFee(String serviceFee) {
        this.serviceFee = serviceFee;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getIssue() {
        return issue;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public String getServiceFee() {
        return serviceFee;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public void setServiceFee(String serviceFee) {
        this.serviceFee = serviceFee;
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

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
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

    public void setDriverName(String mechanicName) {
        this.mechanicName = mechanicName;
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
        return mechanicName;
    }

}
