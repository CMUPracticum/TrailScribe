package edu.cmu.sv.trailscribe.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.data.Kml;

public class KmlDataSource extends DataSource<Kml> {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.NAME,  
			DBHelper.FILENAME, DBHelper.LAST_MODIFIED
	};
	
	public KmlDataSource(Context context) {
		super(context);
	}
	
	public KmlDataSource(DBHelper dbHelper) {
		super(dbHelper);
	}
	
	@Override
	public boolean add(Kml kml) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_ID, kml.getId());
		values.put(DBHelper.NAME, kml.getName());
		values.put(DBHelper.FILENAME, kml.getFilename());
		values.put(DBHelper.LAST_MODIFIED, kml.getLastModified());
		
		return addHelper(DBHelper.TABLE_KML, values);
	}

	@Override
	public boolean delete(Kml kml) {
	    deleteHelper(DBHelper.TABLE_KML, kml.getId());
		return true;
	}
	
    @Override
    public boolean deleteAll() {
        deleteAllHelper(DBHelper.TABLE_KML);
        return true;
    }   	

    @Override
    public Kml get(long id) {
        return getHelper(DBHelper.TABLE_KML, allColumns, id);
    }
    
    @Override
    public Kml get(String name) {
        return getHelper(DBHelper.TABLE_KML, allColumns, name);
    }    
    
	@Override
	public List<Kml> getAll() {
        return getAllHelper(DBHelper.TABLE_KML, allColumns);
	}
	
    @Override
    protected Kml cursorToData(Cursor cursor) {
        return new Kml(
                cursor.getLong(0),
                cursor.getString(1), 
                cursor.getString(2), 
                cursor.getString(3)); 
    }
}