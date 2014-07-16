package edu.cmu.sv.trailscribe.controller;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.MapDataSource;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.BackendFacade;
import edu.cmu.sv.trailscribe.model.Map;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;

public class SynchronizationCenterController 
	extends AsyncTask<String, Void, Void> implements AsyncTaskCompleteListener<String>{
	
	private final String endpoint = "http://trail-scribe.mlep.net/maps";
	private AsyncTaskCompleteListener<ArrayList<Map>> mTaskCompletedCallback;
	private Context mContext;
	
	public SynchronizationCenterController(AsyncTaskCompleteListener<ArrayList<Map>> callback, Context context){
		this.mTaskCompletedCallback = callback;
		mContext = context;
	}
	
	@Override
	public void onTaskCompleted(String syncResult) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(syncResult);
		
		if (jsonElement.isJsonNull()) {
//			TODO Show notification when returned result is null
			return;
		}
		
		JsonArray syncResultJson = (JsonArray)jsonParser.parse(syncResult);
		ArrayList<Map> maps = new ArrayList<Map>();
		Map map;
		for (JsonElement item:syncResultJson) {
			String model = item.getAsJsonObject().get("model").getAsString();
			if(model.equals("sync_center.map")){
				map = new Map(0, model, model, model, 0, 0, 0, 0, 0, 0, model, model);
				JsonObject mapsJsonArray = item.getAsJsonObject().get("fields").getAsJsonObject();
				map.setMinX(mapsJsonArray.get("min_x").getAsDouble());
				map.setMaxZoomLevel(mapsJsonArray.get("max_zoom_level").getAsInt());
				map.setMaxY(mapsJsonArray.get("max_y").getAsDouble());
				map.setName(mapsJsonArray.get("name").getAsString());
				map.setFilename(mapsJsonArray.get("filename").getAsString());
				map.setProjection(mapsJsonArray.get("projection").getAsString());
				map.setLastModified(mapsJsonArray.get("last_modified").getAsString());
				map.setMinZoomLevel(mapsJsonArray.get("min_zoom_level").getAsInt());
				map.setMaxX(mapsJsonArray.get("max_x").getAsDouble());
				map.setMinY(mapsJsonArray.get("min_y").getAsDouble());
				
				maps.add(map);
				//Persist
				MapDataSource mapsDataSource = new MapDataSource(TrailScribeApplication.mDBHelper);
				mapsDataSource.add(map);
				
			}
		}
		mTaskCompletedCallback.onTaskCompleted(maps);
	}

	@Override
	protected Void doInBackground(String... params) {
		BackendFacade backend = new BackendFacade(endpoint, this);
		backend.execute();
		return null;
	}
}
