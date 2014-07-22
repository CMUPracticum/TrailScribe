package edu.cmu.sv.trailscribe.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DataSource {
	@SuppressWarnings("unused")
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
	    open();
	    database.delete(table, DBHelper.KEY_ID + " = " + id, null);
	    close();
	}

	@SuppressWarnings("rawtypes")
	public abstract List getAll();
	protected abstract Object cursorToData(Cursor cursor);
}
