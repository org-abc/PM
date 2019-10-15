package com.pocketmechanic.kondie.pm_mechanic;

/**
 * Created by kondie on 2019/01/19.
 */

public class ClientItem {

    String name, username, problem, imgPath;
    double lat, lng, distance;

    public ClientItem(){}

    public ClientItem(String name, String username, String problem, String imgPath, double distance, double lat, double lng)
    {
        this.name = name;
        this.username = username;
        this.problem = problem;
        this.imgPath = imgPath;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProblem() {
        return problem;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getUsername() {
        return username;
    }
}
