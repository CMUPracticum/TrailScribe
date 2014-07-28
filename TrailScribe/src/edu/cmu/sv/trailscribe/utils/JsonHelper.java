package edu.cmu.sv.trailscribe.utils;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.model.data.Sample;

public class JsonHelper {

    /**
     * Given the azimuth, return it in Json format
     * 
     * @param azimuth
     * @return azimuth in Json format 
     */
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
    
    /**
     * Given the currently selected map, return it in Json format
     * 
     * @param map
     * @return map in Json format 
     */
    public static String getCurrentMapJson(Map map) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'map':{");      
        buffer.append(map.toJson());
        buffer.append("}}");
        
        JSONObject mapInJson = null;
        try {
            mapInJson = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return mapInJson.toString();
    }
    
    /**
     * @return the every sample in database in Json format
     */
    public static String getSamplesJson(List<Sample> samples) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'points':[");
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            
            buffer.append("{");
            buffer.append(sample.toJson());
            buffer.append("}");
            
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
    
    /**
     * @return the current location in database in Json format
     */
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
    
    /**
     * @return the every past location in database in Json format
     */
    public static String getPositionHistoryJson(
            List<edu.cmu.sv.trailscribe.model.data.Location> locations) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{'points':[");
        for (int i = 0; i < locations.size(); i++) {
            edu.cmu.sv.trailscribe.model.data.Location location = locations.get(i);
            
            buffer.append("{");
            buffer.append(location.toJson());
            buffer.append("}");
            
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
    
    /**
     * Given a list of names of selected overlays, return the names in Json format.
     * 
     * @param selectedOverlayNames
     * @return selectedOverlayNames in Json format 
     */
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