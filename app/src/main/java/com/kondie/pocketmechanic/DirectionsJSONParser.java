package com.kondie.pocketmechanic;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kondie on 2018/02/06.
 */

public class DirectionsJSONParser {

    public List<List<HashMap<String, String>>> parser(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try{
            jRoutes = jObject.getJSONArray("routes");
            for(int c=0; c<jRoutes.length(); c++){
                jLegs = ((JSONObject) jRoutes.get(c)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();

                for(int i=0; i<jLegs.length(); i++){
                    jSteps = ((JSONObject) jLegs.get(i)).getJSONArray("steps");

                    for (int j=0; j<jSteps.length(); j++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject) jSteps.get(j)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        for (int k=0; k<list.size(); k++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(((LatLng) list.get(k)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(k)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        }catch (Exception e){
//            Toast.makeText(NavMap.activity, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return routes;
    }

    private List<LatLng> decodePoly(String encoded){

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len){
            int b, shift = 0, result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while(b >= 0x20);

            int dlat = ((result & 1) != 0) ? -(result >> 1) : (result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while(b >= 0x20);
            int dlng = ((result & 1) != 0 ? -(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }
}
