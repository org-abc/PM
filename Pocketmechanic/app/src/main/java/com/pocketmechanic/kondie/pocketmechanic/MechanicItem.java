package com.pocketmechanic.kondie.pocketmechanic;

/**
 * Created by kondie on 2018/02/27.
 */

public class MechanicItem {

    String mechanicName, mechanicImagePath;
    double lat, lng, rating, minFee;
    int mechanicId, phone;

    public MechanicItem(){}

    public MechanicItem(String mechanicName, String mechanicImagePath, int mechanicId, int phone, double lat, double rating, double lng, double minFee){

        this.mechanicName = mechanicName;
        this.mechanicImagePath = mechanicImagePath;
        this.mechanicId = mechanicId;
        this.lat = lat;
        this.lng = lng;
        this.minFee = minFee;
        this.rating = rating;
        this.phone = phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getPhone() {
        return phone;
    }

    public void setMinFee(double minFee) {
        this.minFee = minFee;
    }

    public double getMinFee() {
        return minFee;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setMechanicId(int mechanicId) {
        this.mechanicId = mechanicId;
    }

    public void setMechanicImagePath(String mechanicImagePath) {
        this.mechanicImagePath = mechanicImagePath;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public double getRating() {
        return rating;
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public String getMechanicImagePath() {
        return mechanicImagePath;
    }

    public String getMechanicName() {
        return mechanicName;
    }
}
