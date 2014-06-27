package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.sv.trailscribe.model.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MapDataSource {
	
	// Database fields
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = { DBHelper.KEY_ID,
			DBHelper.NAME, DBHelper.VERSION};
	
	public MapDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean createMap(Map map) {
	    ContentValues values = new ContentValues();
	    values.put(DBHelper.NAME, map.getName());
	    values.put(DBHelper.VERSION, map.getVersion());
	    long insertId = database.insert(DBHelper.TABLE_MAP, null,
	        values);
	    if(insertId ==  -1){
	    	return false;
	    }
	    else{
	    	return true;
	    }
	}

	public void deleteMap(Map map) {
	    long id = map.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(DBHelper.TABLE_MAP, DBHelper.KEY_ID
	    + " = " + id, null);
	}

	public List<Map> getAllMaps() {
	    List<Map> maps = new ArrayList<Map>();
	
	    Cursor cursor = database.query(DBHelper.TABLE_MAP,
	        allColumns, null, null, null, null, null);
	    
	    if(cursor != null){
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Map map = cursorToMap(cursor);
		    	maps.add(map);
		    	cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
	    }
	    return maps;
	}

	private Map cursorToMap(Cursor cursor) {
	    Map map = new Map();
	    map.setId((int) cursor.getLong(0));
	    map.setName(cursor.getString(1));
	    map.setVersion(cursor.getInt(2));
	    return map;
	}
}
