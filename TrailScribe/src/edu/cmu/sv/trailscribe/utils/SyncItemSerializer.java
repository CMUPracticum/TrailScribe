package edu.cmu.sv.trailscribe.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.cmu.sv.trailscribe.model.data.Kml;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.model.data.SyncItem;

public class SyncItemSerializer implements JsonSerializer<ArrayList<SyncItem>>{

	@Override
	public JsonElement serialize(ArrayList<SyncItem> items, Type type,
			JsonSerializationContext context) {
		JsonObject syncItems = new JsonObject();
		JsonObject mapsJo = new JsonObject();
		JsonObject kmlsJo = new JsonObject();

		for(SyncItem item: items){
			if (item instanceof Map){
				Map map = (Map) item;
				JsonObject mapJo = new JsonObject();
				mapJo.addProperty("id", map.getId());
				mapJo.addProperty("last_modified", map.getLastModified());
				mapsJo.add(String.valueOf(map.getId()), mapJo);	
			}
			else if (item instanceof Kml){
				JsonObject kmlJo = new JsonObject();
				Kml kml = (Kml)item;
				kmlJo.addProperty("id", kml.getId());
				kmlJo.addProperty("last_modified", kml.getLastModified());
				kmlsJo.add(String.valueOf(kml.getId()), kmlJo);
			}
		}
		syncItems.add("maps", mapsJo);
		syncItems.add("kmls", kmlsJo);
		return syncItems;
	}

}
