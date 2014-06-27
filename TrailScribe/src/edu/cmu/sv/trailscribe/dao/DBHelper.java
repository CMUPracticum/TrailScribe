package edu.cmu.sv.trailscribe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "TrailScribeDB";
    
	// Tables Names
    public static final String TABLE_MAP = "map";
    
    // Common column names
    public static final String KEY_ID = "id";	
    public static final String NAME = "name";
    public static final String VERSION = "version";
    
    // Create Statements
    // Map table 
    private static final String CREATE_TABLE_MAP = "CREATE TABLE "
            + TABLE_MAP + "(" + KEY_ID + " INTEGER PRIMARY KEY," + NAME
            + " TEXT," + VERSION + " INTEGER"+ ")";
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
        db.execSQL(CREATE_TABLE_MAP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP);
        
        // create new tables
        onCreate(db);
	}

}
