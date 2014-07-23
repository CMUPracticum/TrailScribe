package edu.cmu.sv.trailscribe.controller;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.cmu.sv.trailscribe.dao.KmlDataSource;
import edu.cmu.sv.trailscribe.dao.MapDataSource;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.Kml;
import edu.cmu.sv.trailscribe.model.Map;
import edu.cmu.sv.trailscribe.model.SyncItem;
import edu.cmu.sv.trailscribe.model.SyncItems;
import edu.cmu.sv.trailscribe.utils.BackendFacade;
import edu.cmu.sv.trailscribe.utils.SyncItemSerializer;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;

public class SynchronizationCenterController 
	extends AsyncTask<String, Void, Void> implements AsyncTaskCompleteListener<String>{
	
	private final String endpoint = "http://trail-scribe.mlep.net/sync/";
	private AsyncTaskCompleteListener<ArrayList<SyncItem>> mTaskCompletedCallback;
	MapDataSource mMapsDataSource = new MapDataSource(TrailScribeApplication.mDBHelper);
	KmlDataSource mKmlsDataSource = new KmlDataSource(TrailScribeApplication.mDBHelper);
	
	public SynchronizationCenterController(AsyncTaskCompleteListener<ArrayList<SyncItem>> callback){
		this.mTaskCompletedCallback = callback;
	}
	
	// This method is invoked by the BackendFacade once the response from the backend is available
	@Override
	public void onTaskCompleted(String syncResult) {
		ArrayList<SyncItem> itemsToSync = null;

		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(syncResult);		

		// Unless there is an error in the communication, jsonElement should not be null
		if (!jsonElement.isJsonNull()) {
			JsonArray syncResultJson = (JsonArray)jsonParser.parse(syncResult);
			itemsToSync = new ArrayList<SyncItem>();
			for (JsonElement item:syncResultJson) {
				String model = item.getAsJsonObject().get("model").getAsString();
				JsonObject syncItemJsonArray = item.getAsJsonObject().get("fields").getAsJsonObject();
				
				// Parsing maps and kmls differently given their attributes are not quite the same
				if(model.equals("sync_center.map")){
					itemsToSync.add(parseMapInformation(item, syncItemJsonArray));
				}
				else if(model.equals("sync_center.kml")){
					itemsToSync.add(parseKmlInformation(item, syncItemJsonArray));
				}
			}
		}
		mTaskCompletedCallback.onTaskCompleted(itemsToSync);
	}

	private Kml parseKmlInformation(JsonElement item, JsonObject syncItemJsonArray) {
		Kml kml;
		kml = new Kml();
		kml.setName(syncItemJsonArray.get("name").getAsString());
		kml.setId(item.getAsJsonObject().get("pk").getAsLong());
		kml.setFilename(syncItemJsonArray.get("filename").getAsString());
		kml.setLastModified(syncItemJsonArray.get("last_modified").getAsString());
		return kml;
	}

	private Map parseMapInformation(JsonElement item, JsonObject syncItemJsonArray) {
		Map map;
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
		return map;
	}

	@Override
	protected Void doInBackground(String... params) {
		List<Map> maps = mMapsDataSource.getAll();
		List<Kml> kmls = mKmlsDataSource.getAll();
	
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(SyncItems.class, new SyncItemSerializer());
        String json = gson.create().toJson(new SyncItems((ArrayList<Map>)maps, (ArrayList<Kml>)kmls));
        
		BackendFacade backend = new BackendFacade(endpoint, this, json);
		backend.execute();
		return null;
	}
}
