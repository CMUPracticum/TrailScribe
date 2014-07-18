package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.Kml;

public class KmlDataSource extends DataSource {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.NAME,  
			DBHelper.FILENAME, DBHelper.LAST_MODIFIED };
	
	public KmlDataSource(Context context) {
		super(context);
	}
	
	public KmlDataSource(DBHelper dbHelper) {
		super(dbHelper);
	}
	
	@Override
	public boolean add(Object data) {
		if (data.getClass() != Kml.class) return false;
		Kml kml = (Kml) data;
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_ID, kml.getId());
		values.put(DBHelper.NAME, kml.getName());
		values.put(DBHelper.FILENAME, kml.getFilename());
		values.put(DBHelper.LAST_MODIFIED, kml.getLastModified());
		
		return addHelper(DBHelper.TABLE_MAP, values);
	}

	@Override
	public boolean delete(Object data) {
		if (data.getClass() != Kml.class) return false;

		Kml kml = (Kml) data;
	    deleteHelper(DBHelper.TABLE_MAP, kml.getId());
	    
		return true;
	}

	@Override
	public List<Kml> getAll() {
		open();
		
	    List<Kml> kmls = new ArrayList<Kml>();
	
	    Cursor cursor = database.query(DBHelper.TABLE_KML,
	        allColumns, null, null, null, null, null);
	    
	    if (cursor != null) {
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Kml kml = (Kml) cursorToData(cursor);
		    	kmls.add(kml);
		    	cursor.moveToNext();
		    }
		    cursor.close();
	    }
	    
	    close();
	    return kmls;
	}
	
    @Override
    protected Object cursorToData(Cursor cursor) {
        return new Kml(
                cursor.getLong(0),
                cursor.getString(1), 
                cursor.getString(2), 
                cursor.getString(3)); 
                }
}
