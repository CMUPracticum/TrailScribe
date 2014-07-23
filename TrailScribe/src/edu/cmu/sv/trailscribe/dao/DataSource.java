package edu.cmu.sv.trailscribe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DataSource<T> {
    protected static final String MSG_TAG = "DataSource";
	
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

	public abstract boolean add(T data);
	protected boolean addHelper(String table, ContentValues values) {
		open();
		boolean success = (database.insert(table, null, values) != -1);
		close();
		
		return success;
	}

	public abstract boolean delete(T data);
	protected void deleteHelper(String table, long id) {
	    open();
	    database.delete(table, DBHelper.KEY_ID + " = " + id, null);
	    close();
	}
	
	public abstract boolean deleteAll();
    protected void deleteAllHelper(String table) {
        open();
        database.delete(table, null, null);
        close();
    }

	public abstract T get(long id);
    protected T getHelper(String table, String[] allColumns, long id) {
        open();
        
        Cursor cursor = database.query(table, allColumns, 
                DBHelper.KEY_ID + "=?", new String[] { Long.toString(id) }, 
                null, null, null, null);
        T data = null;
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                data = cursorToData(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }
        
        close();
        return data;
    }
	
	public abstract List<T> getAll();
    protected List<T> getAllHelper(String table, String[] allColumns) {
        open();
        
        Cursor cursor = database.query(table, allColumns, null, null, null, null, null);
        List<T> list = new ArrayList<T>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursorToData(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        
        close();
        return list;
	}
	
	
	protected abstract T cursorToData(Cursor cursor);
}