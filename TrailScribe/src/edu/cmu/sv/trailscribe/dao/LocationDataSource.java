package edu.cmu.sv.trailscribe.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.data.Location;

public class LocationDataSource extends DataSource<Location> {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.TIME, DBHelper.X, DBHelper.Y, DBHelper.Z,
			DBHelper.USER_ID, DBHelper.MAP_ID, DBHelper.EXPEDITION_ID
	};
	
	public LocationDataSource(Context context) {
		super(context);
	}
	
	public LocationDataSource(DBHelper dbHelper) {
		super (dbHelper);
	}
	
	@Override
	public boolean add(Location location) {
		ContentValues values = new ContentValues();
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
	public boolean delete(Location location) {
		deleteHelper(DBHelper.TABLE_LOCATION, location.getId());
		return true;
	}
	
    @Override
    public boolean deleteAll() {
        deleteAllHelper(DBHelper.TABLE_LOCATION);
        return true;
    }	
	
    @Override
    public Location get(long id) {
        return getHelper(DBHelper.TABLE_LOCATION, allColumns, id);
    }
    
    @Override
    public Location get(String name) {
//      Location does not have a name, so this query will always return null
        return getHelper(DBHelper.TABLE_LOCATION, allColumns, name);
    }    
    
	@Override
	public List<Location> getAll() {
        return getAllHelper(DBHelper.TABLE_LOCATION, allColumns);
	}
	
    @Override
    protected Location cursorToData(Cursor cursor) {
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
