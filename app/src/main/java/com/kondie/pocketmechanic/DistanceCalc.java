package com.kondie.pocketmechanic;

import android.location.Location;

public class DistanceCalc {

    public static double distance(double startLat, double startLng, double endLat, double endLng) {
        if ((startLat == endLat) && (startLng == endLng)) {
            return 0;
        }
        else {
            Location startLocation = new Location("");
            startLocation.setLatitude(startLat);
            startLocation.setLongitude(startLng);
            Location endLocation = new Location("");
            endLocation.setLatitude(endLat);
            endLocation.setLongitude(endLng);
            double distanceInMeters = startLocation.distanceTo(endLocation);

            return (distanceInMeters);
        }
    }
}
