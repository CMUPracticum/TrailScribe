package edu.cmu.sv.trailscribe.dao;

import java.util.List;

import edu.cmu.sv.trailscribe.model.Map;
import edu.cmu.sv.trailscribe.model.Sample;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class DataSource {
	private static final String MSG_TAG = "DataSource";
	
	// Database fields
	protected static SQLiteDatabase database;
	protected static DBHelper dbHelper;
	
	public DataSource(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public DataSource(DBHelper dbHelper) {
		DataSource.dbHelper = dbHelper;
	}

	protected void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	protected void close() {
		dbHelper.close();
	}

	public abstract boolean add(Object data);
	protected boolean addHelper(String table, ContentValues values) {
		open();
		boolean success = (database.insert(table, null, values) != -1);
		close();
		
		return success;
	}

	public abstract boolean delete(Object data);
	protected void deleteHelper(String table, long id) {
	    database.delete(table, DBHelper.KEY_ID + " = " + id, null);
	}

	@SuppressWarnings("rawtypes")
	public abstract List getAll();
	@SuppressWarnings("rawtypes")
	protected Object cursorToData(Class classType, Cursor cursor) {
		if (classType == Sample.class) {
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
		} else if (classType == Map.class) {
			
		} else {
			Log.e(MSG_TAG, "Invalid data type");
			return null;
		}
		
		return null;
	}
}