package edu.cmu.sv.trailscribe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String MSG_TAG = "DBHelper";
	
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
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String TIME = "time";
    public static final String DESCRIPTION = "description";
    public static final String CUSTOM_FIELD = "custom_field";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String USER_ID = "user_id";
    public static final String MAP_ID = "map_id";
    public static final String EXPEDITION_ID = "expedition_id";
    
    // Create Statements
    private static final String CREATE_TABLE_MAP = "CREATE TABLE " + TABLE_MAP 
    		+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT," + VERSION + " INTEGER"+ ")";
 
    private static final String CREATE_TABLE_SAMPLE = "CREATE TABLE " + TABLE_SAMPLE 
    		+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT, "	+ DESCRIPTION + " TEXT, "
    		+ TIME + " TEXT, "
    		+ X + " DOUBLE, " + Y + " DOUBLE, " + Z + " DOUBLE, " 
    		+ CUSTOM_FIELD + " TEXT," + LAST_MODIFIED + " TEXT," 
    		+ USER_ID + " INTEGER," + MAP_ID + " INTEGER," + EXPEDITION_ID + " INTEGER" + ")";
    
    
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(MSG_TAG, "onCreate");
		
        db.execSQL(CREATE_TABLE_MAP);
        db.execSQL(CREATE_TABLE_SAMPLE);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.d(MSG_TAG, "onOpen");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(MSG_TAG, "onUpgrade");
		
		// on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAMPLE);
        
        // create new tables
        onCreate(db);
	}
}
