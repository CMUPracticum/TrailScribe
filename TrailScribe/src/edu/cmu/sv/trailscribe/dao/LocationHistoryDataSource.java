package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.LocationHistory;

public class LocationHistoryDataSource extends DataSource {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.TIME, DBHelper.X, DBHelper.Y, DBHelper.Z,
			DBHelper.USER_ID, DBHelper.MAP_ID, DBHelper.EXPEDITION_ID
	};
	
	public LocationHistoryDataSource(Context context) {
		super(context);
	}
	
	public LocationHistoryDataSource(DBHelper dbHelper) {
		super (dbHelper);
		
		// TODO: Store current location in database
		// Seed data
		LocationHistory locationHistory1 = new LocationHistory(
				100, "default time", -122.051258, 37.406001, 0, 0, 0, 0);
		add(locationHistory1);
		
		LocationHistory locationHistory2 = new LocationHistory(
				101, "default time", -122.053918, 37.411183, 0, 0, 0, 0);
		add(locationHistory2);
		
		LocationHistory locationHistory3 = new LocationHistory(
				102, "default time", -122.053768, 37.413296, 0, 0, 0, 0);
		add(locationHistory3);
		
		LocationHistory locationHistory4 = new LocationHistory(
				103, "default time", -122.053883, 37.416132, 0, 0, 0, 0);
		add(locationHistory4);
	}
	
	@Override
	public boolean add(Object data) {
		if (data.getClass() != LocationHistory.class) return false;
		LocationHistory locationHistory = (LocationHistory) data;
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_ID, locationHistory.getId());
		values.put(DBHelper.TIME, locationHistory.getTime());
		values.put(DBHelper.X, locationHistory.getX());
		values.put(DBHelper.Y, locationHistory.getY());
		values.put(DBHelper.Z, locationHistory.getZ());
		values.put(DBHelper.USER_ID, locationHistory.getUserId());
		values.put(DBHelper.MAP_ID, locationHistory.getMapId());
		values.put(DBHelper.EXPEDITION_ID, locationHistory.getExpeditionId());
		
		return addHelper(DBHelper.TABLE_LOCATION_HISTORY, values);
	}
	
	@Override
	public boolean delete(Object data) {
		if (data.getClass() != LocationHistory.class) return false;
		
		LocationHistory locationHistory = (LocationHistory) data;
		deleteHelper(DBHelper.TABLE_LOCATION_HISTORY, locationHistory.getId());
		
		return true;
	}
	
	@Override
	public List<LocationHistory> getAll() {
		open();
		
		List<LocationHistory> locationHistories = new ArrayList<LocationHistory>();
		
		Cursor cursor = database.query(DBHelper.TABLE_LOCATION_HISTORY, 
				allColumns, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				LocationHistory locationHistory = 
						(LocationHistory) cursorToData(LocationHistory.class, cursor);
				locationHistories.add(locationHistory);
				cursor.moveToNext();
			}
			cursor.close();
		}
		close();
		return locationHistories;
	}
	
}
