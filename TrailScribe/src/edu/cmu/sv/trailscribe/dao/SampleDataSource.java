package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.cmu.sv.trailscribe.model.Sample;

public class SampleDataSource {
	private static final String MSG_TAG = "SampleDataSource";
	
	// Database fields
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = { DBHelper.PRIVATE_KEY, DBHelper.USER_ID,
			DBHelper.X, DBHelper.Y, DBHelper.Z, DBHelper.NAME, DBHelper.TIMESTAMP,
			DBHelper.DESCRIPTION, DBHelper.MISC };
	
	public SampleDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean createSample(Sample sample) {
	    ContentValues values = new ContentValues();
	    values.put(DBHelper.PRIVATE_KEY, sample.getPrivateKey());
	    values.put(DBHelper.USER_ID, sample.getUserId());
	    values.put(DBHelper.X, sample.getX());
	    values.put(DBHelper.Y, sample.getY());
	    values.put(DBHelper.Z, sample.getZ());
	    values.put(DBHelper.NAME, sample.getName());
	    values.put(DBHelper.TIMESTAMP, sample.getTimeStamp());
	    values.put(DBHelper.DESCRIPTION, sample.getDescription());
	    values.put(DBHelper.MISC, sample.getMisc());
	    
	    return !(database.insert(DBHelper.TABLE_SAMPLE, null, values) == -1);
	}

	public void deleteSample(Sample sample) {
	    long privateKey = sample.getPrivateKey();
	    
	    Log.d(MSG_TAG, "Comment deleted with id: " + privateKey);
	    database.delete(DBHelper.TABLE_SAMPLE, DBHelper.PRIVATE_KEY + " = " + privateKey, null);
	}

	public List<Sample> getAllSamples() {
	    List<Sample> samples = new ArrayList<Sample>();
	
	    Cursor cursor = database.query(DBHelper.TABLE_SAMPLE,
	        allColumns, null, null, null, null, null);
	    
	    if (cursor != null) {
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Sample sample = cursorToSample(cursor);
		    	samples.add(sample);
		    	cursor.moveToNext();
		    }
		    cursor.close();
	    }
	    
	    return samples;
	}

	private Sample cursorToSample(Cursor cursor) {
	    return new Sample(
	    		cursor.getLong(0),
	    		cursor.getLong(1), 
	    		cursor.getDouble(2), 
	    		cursor.getDouble(3), 
	    		cursor.getDouble(4), 
	    		cursor.getString(5), 
	    		cursor.getString(6), 
	    		cursor.getString(7), 
	    		cursor.getString(8));
	}
	
}
