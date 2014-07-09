package edu.cmu.sv.trailscribe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "TrailScribeDB";
    
	// Tables Names
    public static final String TABLE_MAP = "map";
    public static final String TABLE_SAMPLE = "sample";
    
    // Common column names
    public static final String KEY_ID = "id";	
    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String USER_ID = "userId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String TIMESTAMP = "timestamp";
    public static final String DESCRIPTION = "description";
    public static final String MISC = "misc";
    
    // Create Statements
    // Map table 
    private static final String CREATE_TABLE_MAP = "CREATE TABLE " + TABLE_MAP 
    		+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT," + VERSION + " INTEGER"+ ")";
    
    private static final String CREATE_TABLE_SAMPLE = "CREATE TABLE " + TABLE_SAMPLE 
    		+ "(" + PRIVATE_KEY + " INTEGER PRIMARY KEY," + USER_ID + " INTEGER," 
    		+ X + " DOUBLE, " + Y + " DOUBLE, " + Z + " DOUBLE, " 
    		+ NAME + " TEXT, " + TIMESTAMP + " TEXT, " + DESCRIPTION + " TEXT, " 
    		+ MISC + " TEXT" + ")";
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
        db.execSQL(CREATE_TABLE_MAP);
        db.execSQL(CREATE_TABLE_SAMPLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAMPLE);
        
        // create new tables
        onCreate(db);
	}
}
