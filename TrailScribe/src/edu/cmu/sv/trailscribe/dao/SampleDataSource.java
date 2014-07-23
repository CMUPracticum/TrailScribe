package edu.cmu.sv.trailscribe.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.cmu.sv.trailscribe.model.Sample;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;

public class SampleDataSource extends DataSource<Sample> {
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
		
//		TODO: Remove when feature to add samples is implemented
//		Inserting samples.
        List<Sample> samples = getAll();
        
        if (samples.size() == 4) {
            return;
        }
        
        for (Sample sample : samples) {
            delete(sample);
        }
        
        // Path of sample's images are stored in custom field
        // The images are located in:
        // file:///sdcard/trailscribe/samples/<sample.name>/
        // In the order of 1.jpg, 2.jpg, 3.jpg, etc.
		Sample s1 = new Sample(
	    		0, "Carnegie Mellon University - Silicon Valley Campus", 
	    		"sample #1", "default time",
	    		-122.059746, 37.410418, 0, 
	    		TrailScribeApplication.STORAGE_PATH + "samples/Carnegie Mellon University - Silicon Valley Campus/1.jpg", 
	    		"default last modified", 0, 0, 0);
        Sample s2 = new Sample(
                0, "Hangar 1", 
                "sample #2", "default time",
                -122.054195, 37.412675, 0, 
                TrailScribeApplication.STORAGE_PATH + "samples/Hangar 1/1.jpg", 
                "default last modified", 0, 0, 0);
        Sample s3 = new Sample(
                0, "Moffett Field Historical Society Museum", 
                "sample #3", "default time",
                -122.054230, 37.411352, 0, 
                TrailScribeApplication.STORAGE_PATH + "samples/Moffett Field Historical Society Museum/1.jpg", 
                "default last modified", 0, 0, 0);
        Sample s4 = new Sample(
                0, "Pool", 
                "sample #4", "default time",
                -122.056896, 37.409516, 0, 
                TrailScribeApplication.STORAGE_PATH + "samples/Pool/1.jpg", 
                "default last modified", 0, 0, 0);
		
		add(s1);
		add(s2);
		add(s3);
		add(s4);
	}

	@Override
	public boolean add(Sample sample) {
	    ContentValues values = new ContentValues();
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
	public boolean delete(Sample sample) {
		deleteHelper(DBHelper.TABLE_SAMPLE, sample.getId());
		return true;
	}
	   
    @Override
    public boolean deleteAll() {
        deleteAllHelper(DBHelper.TABLE_SAMPLE);
        return true;
    }

    @Override
    public Sample get(long id) {
        return getHelper(DBHelper.TABLE_SAMPLE, allColumns, id);
    }
    
	@Override
	public List<Sample> getAll() {
	    return getAllHelper(DBHelper.TABLE_SAMPLE, allColumns);
	}

    @Override
    protected Sample cursorToData(Cursor cursor) {
        return new Sample(
                cursor.getLong(0),
                cursor.getString(1), 
                cursor.getString(2), 
                cursor.getString(3), 
                cursor.getDouble(4), 
                cursor.getDouble(5), 
                cursor.getDouble(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getLong(9),
                cursor.getLong(10), 
                cursor.getLong(11));
    }
}
