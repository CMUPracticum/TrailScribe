package edu.cmu.sv.trailscribe.controller;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.BackendFacade;
import edu.cmu.sv.trailscribe.model.Map;

public class SynchronizationCenterController implements AsyncTaskCompleteListener<String>{
	//private String endpoint = "http://trail-scribe.mlep.net/maps/";
	private String endpoint = "http://192.168.0.3:8080/TestServer/latestReadingFromDevicesByGeofence/maps";
	private ArrayList<Map> maps = new ArrayList<Map>();
	
	public SynchronizationCenterController(){
		BackendFacade backend = new BackendFacade(endpoint, this);
		backend.execute();
	}

	public ArrayList<Map> syncMaps(){
		return maps;
	}
	
	@Override
	public void onTaskCompleted(String syncResult) {
		JsonParser jsonParser = new JsonParser();
		JsonArray syncResultJson = (JsonArray)jsonParser.parse(syncResult);
		Map map = new Map();
		for (JsonElement item:syncResultJson){
			String model = item.getAsJsonObject().get("model").getAsString();
			if(model.equals("map_manager.map")){
				JsonElement mapsJsonArray = item.getAsJsonObject().get("fields").getAsJsonObject();
				map.setMinX(mapsJsonArray.getAsJsonObject().get("min_x").getAsDouble());
				map.setMaxZoomLevel(mapsJsonArray.getAsJsonObject().get("max_zoom_level").getAsInt());
				map.setMaxY(mapsJsonArray.getAsJsonObject().get("max_y").getAsDouble());
				map.setName(mapsJsonArray.getAsJsonObject().get("name").getAsString());
				map.setFilename(mapsJsonArray.getAsJsonObject().get("filename").getAsString());
				map.setProjection(mapsJsonArray.getAsJsonObject().get("projection").getAsString());
				map.setLastModified(mapsJsonArray.getAsJsonObject().get("last_modified").getAsString());
				map.setMinZoomLevel(mapsJsonArray.getAsJsonObject().get("min_zoom_level").getAsInt());
				map.setMaxX(mapsJsonArray.getAsJsonObject().get("max_x").getAsDouble());
				map.setMinY(mapsJsonArray.getAsJsonObject().get("min_y").getAsDouble());
				
				maps.add(map);
			}
		}
	}
}
