package edu.cmu.sv.trailscribe.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.cmu.sv.trailscribe.model.data.Kml;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.model.data.SyncItems;

public class SyncItemSerializer implements JsonSerializer<SyncItems>{

	@Override
	public JsonElement serialize(SyncItems items, Type type,
			JsonSerializationContext context) {
		JsonObject syncItems = new JsonObject();
		JsonObject mapsJo = new JsonObject();
		for (Map map: items.getMaps()){
			JsonObject mapJo = new JsonObject();
			mapJo.addProperty("id", map.getId());
			mapJo.addProperty("last_modified", map.getLastModified());
			mapsJo.add(String.valueOf(map.getId()), mapJo);
		}
		syncItems.add("maps", mapsJo);
		JsonObject kmlsJo = new JsonObject();
		for (Kml kml: items.getKmls()){
			JsonObject kmlJo = new JsonObject();
			kmlJo.addProperty("id", kml.getId());
			kmlJo.addProperty("last_modified", kml.getLastModified());
			kmlsJo.add(String.valueOf(kml.getId()), kmlJo);
		}
		syncItems.add("kmls", kmlsJo);
		return syncItems;
	}

}
