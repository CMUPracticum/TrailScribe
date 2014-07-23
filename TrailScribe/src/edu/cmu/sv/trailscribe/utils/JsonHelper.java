package edu.cmu.sv.trailscribe.utils;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import edu.cmu.sv.trailscribe.model.Sample;

public class JsonHelper {

    public static String getOrientationJson(float azimuth) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'orientation':[");
        buffer.append("{'azimuth':'").append(azimuth).append("'}");
        buffer.append("]}'");
        
        JSONObject orientation = null;
        try {
            orientation = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return orientation.toString();
    }
    
    public static String getSamplesJson(List<Sample> samples) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'points':[");
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            buffer.append(sample.toString());
            
            if (i != samples.size() - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("]}'");
        
        JSONObject mapPoints = null;
        try {
            mapPoints = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return mapPoints.toString();
    }
    
    public static String getCurrentLocationJson(Location location) throws Exception {
        JSONObject mapPoints = null;
        
        try {
            double la = location.getLatitude();
            double lng = location.getLongitude();
            mapPoints = new JSONObject("{'points':[{'x':'" + lng + "', 'y':'" + la + "'}]}'");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return mapPoints.toString();
    }
    
    public static String getPositionHistoryJson(List<edu.cmu.sv.trailscribe.model.Location> locations) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'points':[");
        for (int i = 0; i < locations.size(); i++) {
            edu.cmu.sv.trailscribe.model.Location location = locations.get(i);
            buffer.append(location.toString());
            
            if (i != locations.size() - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("]}'");
        
        JSONObject mapPoints = null;
        try {
            mapPoints = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return mapPoints.toString();
    }
    
    public static String getSelectedKmlJson(List<String> selectedOverlayNames) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'kmls':[");
        for (int i = 0; i < selectedOverlayNames.size(); i++) {
            buffer.append("{'path':'").append(selectedOverlayNames.get(i)).append("'}");
            
            if (i != selectedOverlayNames.size() - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("]}'");
        
        JSONObject overlays = null;
        try {
            overlays = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return overlays.toString();
    }
}
