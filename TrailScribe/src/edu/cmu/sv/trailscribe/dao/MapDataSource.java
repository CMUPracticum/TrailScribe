package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.Map;

public class MapDataSource extends DataSource {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.NAME, DBHelper.DESCRIPTION, DBHelper.PROJECTION, 
			DBHelper.MIN_ZOOM_LEVEL, DBHelper.MAX_ZOOM_LEVEL, 
			DBHelper.MIN_X, DBHelper.MIN_Y, DBHelper.MAX_X, DBHelper.MAX_Y, 
			DBHelper.FILENAME, DBHelper.LAST_MODIFIED };
	
	public MapDataSource(Context context) {
		super(context);
	}
	
	public MapDataSource(DBHelper dbHelper) {
		super(dbHelper);
	}
	
	@Override
	public boolean add(Object data) {
		if (data.getClass() != Map.class) return false;
		Map map = (Map) data;
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_ID, map.getId());
		values.put(DBHelper.NAME, map.getName());
		values.put(DBHelper.DESCRIPTION, map.getDescription());
		values.put(DBHelper.PROJECTION, map.getProjection());
		values.put(DBHelper.MIN_ZOOM_LEVEL, map.getMinZoomLevel());
		values.put(DBHelper.MAX_ZOOM_LEVEL, map.getMaxZoomLevel());
		values.put(DBHelper.MIN_X, map.getMinX());
		values.put(DBHelper.MIN_Y, map.getMinY());
		values.put(DBHelper.MAX_X, map.getMaxX());
		values.put(DBHelper.MAX_Y, map.getMaxY());
		values.put(DBHelper.FILENAME, map.getFilename());
		values.put(DBHelper.LAST_MODIFIED, map.getLastModified());
		
		return addHelper(DBHelper.TABLE_MAP, values);
	}

	@Override
	public boolean delete(Object data) {
		if (data.getClass() != Map.class) return false;

		Map map = (Map) data;
	    deleteHelper(DBHelper.TABLE_MAP, map.getId());
	    
		return true;
	}

	@Override
	public List<Map> getAll() {
		open();
		
	    List<Map> maps = new ArrayList<Map>();
	
	    Cursor cursor = database.query(DBHelper.TABLE_MAP,
	        allColumns, null, null, null, null, null);
	    
	    if (cursor != null) {
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Map map = (Map) cursorToData(cursor);
		    	maps.add(map);
		    	cursor.moveToNext();
		    }
		    cursor.close();
	    }
	    
	    close();
	    return maps;
	}
	
    @Override
    protected Object cursorToData(Cursor cursor) {
        return new Map(
                cursor.getLong(0),
                cursor.getString(1), 
                cursor.getString(2), 
                cursor.getString(3), 
                cursor.getInt(4), 
                cursor.getInt(5), 
                cursor.getDouble(6),
                cursor.getDouble(7),
                cursor.getDouble(8),
                cursor.getDouble(9),
                cursor.getString(10), 
                cursor.getString(11));
    }
}
