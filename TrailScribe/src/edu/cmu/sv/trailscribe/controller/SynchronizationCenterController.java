package edu.cmu.sv.trailscribe.controller;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.cmu.sv.trailscribe.dao.MapDataSource;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.BackendFacade;
import edu.cmu.sv.trailscribe.model.Kml;
import edu.cmu.sv.trailscribe.model.Map;
import edu.cmu.sv.trailscribe.model.SyncItem;
import edu.cmu.sv.trailscribe.model.SyncItems;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;

public class SynchronizationCenterController 
	extends AsyncTask<String, Void, Void> implements AsyncTaskCompleteListener<String>{
	
	private final String endpoint = "http://trail-scribe.mlep.net/sync/";
	private AsyncTaskCompleteListener<ArrayList<SyncItem>> mTaskCompletedCallback;
	MapDataSource mMapsDataSource = new MapDataSource(TrailScribeApplication.mDBHelper);
	
	public SynchronizationCenterController(AsyncTaskCompleteListener<ArrayList<SyncItem>> callback){
		this.mTaskCompletedCallback = callback;
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
		ArrayList<SyncItem> itemsToSync = new ArrayList<SyncItem>();
		ArrayList<Kml> kmls = new ArrayList<Kml>();
		Map map;
		Kml kml;
		for (JsonElement item:syncResultJson) {
			String model = item.getAsJsonObject().get("model").getAsString();
			JsonObject syncItemJsonArray = item.getAsJsonObject().get("fields").getAsJsonObject();
			if(model.equals("sync_center.map")){
				map = new Map();
				map.setId(item.getAsJsonObject().get("pk").getAsLong());
				map.setMinX(syncItemJsonArray.get("min_x").getAsDouble());
				map.setMaxZoomLevel(syncItemJsonArray.get("max_zoom_level").getAsInt());
				map.setMaxY(syncItemJsonArray.get("max_y").getAsDouble());
				map.setName(syncItemJsonArray.get("name").getAsString());
				map.setFilename(syncItemJsonArray.get("filename").getAsString());
				map.setProjection(syncItemJsonArray.get("projection").getAsString());
				map.setLastModified(syncItemJsonArray.get("last_modified").getAsString());
				map.setMinZoomLevel(syncItemJsonArray.get("min_zoom_level").getAsInt());
				map.setMaxX(syncItemJsonArray.get("max_x").getAsDouble());
				map.setMinY(syncItemJsonArray.get("min_y").getAsDouble());
				
				itemsToSync.add(map);
				
				//Persist
				mMapsDataSource.add(map);
				
			}
			else if(model.equals("sync_center.kml")){
				kml = new Kml();
				kml.setName(syncItemJsonArray.get("name").getAsString());
				kml.setId(item.getAsJsonObject().get("pk").getAsLong());
				kml.setFilename(syncItemJsonArray.get("filename").getAsString());
				kmls.add(kml);
				itemsToSync.add(kml);
				//Persist
				//mMapsDataSource.add(map);				
			}
		}
		mTaskCompletedCallback.onTaskCompleted(itemsToSync);
	}

	@Override
	protected Void doInBackground(String... params) {
		List<Map> maps = mMapsDataSource.getAll();
		Hashtable <Long, Map> mapsToSync = new Hashtable<Long, Map>();
		for(Map map:maps){
			mapsToSync.put(map.getId(), map);
		}
		List<Kml> kmls = new ArrayList<Kml>();
		Hashtable <Long, Kml> KMLsToSync = new Hashtable<Long, Kml>();
		for(Kml kml:kmls){
			KMLsToSync.put(kml.getId(), kml);
		}
		SyncItems itemsToSync = new SyncItems(mapsToSync, KMLsToSync);
		BackendFacade backend = new BackendFacade(endpoint, this, new Gson().toJson(itemsToSync));
		backend.execute();
		return null;
	}
}
