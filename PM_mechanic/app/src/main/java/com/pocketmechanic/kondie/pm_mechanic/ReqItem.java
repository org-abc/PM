package com.pocketmechanic.kondie.pm_mechanic;

/**
 * Created by kondie on 2019/01/18.
 */

public class ReqItem {

    String name, problem, distance, phone, id;
    double lat, lng;

    public ReqItem(){}

    public  ReqItem(String name, String problem, String distance, String phone, double lat, double lng, String id)
    {
        this.name = name;
        this.problem = problem;
        this.distance = distance;
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public String getProblem() {
        return problem;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}
