package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.Location;

public class LocationDataSource extends DataSource {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.TIME, DBHelper.X, DBHelper.Y, DBHelper.Z,
			DBHelper.USER_ID, DBHelper.MAP_ID, DBHelper.EXPEDITION_ID
	};
	
	public LocationDataSource(Context context) {
		super(context);
	}
	
	public LocationDataSource(DBHelper dbHelper) {
		super (dbHelper);
		
		// TODO: Store current location in database
		// Seed data
		Location location1 = new Location(
				100, "default time", -122.051258, 37.406001, 0, 0, 0, 0);
		add(location1);
		
		Location location2 = new Location(
				101, "default time", -122.053918, 37.411183, 0, 0, 0, 0);
		add(location2);
		
		Location location3 = new Location(
				102, "default time", -122.053768, 37.413296, 0, 0, 0, 0);
		add(location3);
		
		Location location4 = new Location(
				103, "default time", -122.053883, 37.416132, 0, 0, 0, 0);
		add(location4);
	}
	
	@Override
	public boolean add(Object data) {
		if (data.getClass() != Location.class) return false;
		Location location = (Location) data;
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_ID, location.getId());
		values.put(DBHelper.TIME, location.getTime());
		values.put(DBHelper.X, location.getX());
		values.put(DBHelper.Y, location.getY());
		values.put(DBHelper.Z, location.getZ());
		values.put(DBHelper.USER_ID, location.getUserId());
		values.put(DBHelper.MAP_ID, location.getMapId());
		values.put(DBHelper.EXPEDITION_ID, location.getExpeditionId());
		
		return addHelper(DBHelper.TABLE_LOCATION, values);
	}
	
	@Override
	public boolean delete(Object data) {
		if (data.getClass() != Location.class) return false;
		
		Location location = (Location) data;
		deleteHelper(DBHelper.TABLE_LOCATION, location.getId());
		
		return true;
	}
	
	@Override
	public List<Location> getAll() {
		open();
		
		List<Location> locations = new ArrayList<Location>();
		
		Cursor cursor = database.query(DBHelper.TABLE_LOCATION, 
				allColumns, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Location location = (Location) cursorToData(cursor);
				locations.add(location);
				cursor.moveToNext();
			}
			cursor.close();
		}
		close();
		return locations;
	}
	
    @Override
    protected Object cursorToData(Cursor cursor) {
        return new Location(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getDouble(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getLong(5),
                cursor.getLong(6),
                cursor.getLong(7));
    }
	
}
