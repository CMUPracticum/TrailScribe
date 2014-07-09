package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.Sample;

public class SampleDataSource extends DataSource {
	private String[] allColumns = {
			DBHelper.KEY_ID, DBHelper.NAME, DBHelper.DESCRIPTION, DBHelper.TIME, 
			DBHelper.X, DBHelper.Y, DBHelper.Z,
			DBHelper.CUSTOM_FIELD, DBHelper.LAST_MODIFIED,
			DBHelper.USER_ID, DBHelper.MAP_ID, DBHelper.EXPEDITION_ID };
	
	public SampleDataSource(Context context) {
		super(context);
	}
	
	public SampleDataSource(DBHelper dbHelper) {
		super(dbHelper);
	}

	@Override
	public boolean add(Object data) {
		if (data.getClass() != Sample.class) return false;
		Sample sample = (Sample) data;
		
	    ContentValues values = new ContentValues();
	    values.put(DBHelper.KEY_ID, sample.getId());
	    values.put(DBHelper.NAME, sample.getName());
	    values.put(DBHelper.DESCRIPTION, sample.getDescription());
	    values.put(DBHelper.TIME, sample.getTime());
	    values.put(DBHelper.X, sample.getX());
	    values.put(DBHelper.Y, sample.getY());
	    values.put(DBHelper.Z, sample.getZ());
	    values.put(DBHelper.CUSTOM_FIELD, sample.getCustomField());
	    values.put(DBHelper.LAST_MODIFIED, sample.getLastModified());
	    values.put(DBHelper.USER_ID, sample.getUserId());
	    values.put(DBHelper.MAP_ID, sample.getMapId());
	    values.put(DBHelper.EXPEDITION_ID, sample.getExpeditionId());
	    
	    return addHelper(DBHelper.TABLE_SAMPLE, values);
	}

	@Override
	public boolean delete(Object data) {
		if (data.getClass() != Sample.class) return false;

		Sample sample = (Sample) data;
	    deleteHelper(DBHelper.TABLE_SAMPLE, sample.getId());
	    
		return true;
	}

	@Override
	public List<Sample> getAll() {
		open();
		
	    List<Sample> samples = new ArrayList<Sample>();
	
	    Cursor cursor = database.query(DBHelper.TABLE_SAMPLE,
	        allColumns, null, null, null, null, null);
	    
	    if (cursor != null) {
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Sample sample = (Sample) cursorToData(Sample.class, cursor);
		    	samples.add(sample);
		    	cursor.moveToNext();
		    }
		    cursor.close();
	    }
	    
	    close();
	    return samples;
	}
}
