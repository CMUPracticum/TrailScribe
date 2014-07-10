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
    public static final String TABLE_MAP = "MAP";
    public static final String TABLE_SAMPLE = "SAMPLE";
    public static final String TABLE_LOCATION = "LOCATION";
    
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
    public static final String PROJECTION = "projection";
    public static final String MIN_ZOOM_LEVEL = "min_zoom_level";
    public static final String MAX_ZOOM_LEVEL = "max_zoom_level";
    public static final String MIN_X = "min_x";
    public static final String MIN_Y = "min_y";
    public static final String MAX_X = "max_x";
    public static final String MAX_Y = "max_y";
    public static final String FILENAME = "filename";
    
    // Create Statements
    private static final String TABLE_MAP_COLUMN_DEFINITION = TABLE_MAP 
    		+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT," 
    		+ DESCRIPTION + " TEXT," + PROJECTION + " TEXT," 
    		+ MIN_ZOOM_LEVEL + " INTEGER," + MAX_ZOOM_LEVEL + " INTEGER,"
    		+ MIN_X + " INTEGER, " + MIN_Y + " INTEGER, "
    		+ MAX_X + " INTEGER, " + MAX_Y + " INTEGER, "
    		+ FILENAME + " TEXT," + LAST_MODIFIED + " TEXT" + ")";
 
    private static final String TABLE_SAMPLE_COLUMN_DEFINITION = TABLE_SAMPLE 
    		+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT, "	+ DESCRIPTION + " TEXT, "
    		+ TIME + " TEXT, "
    		+ X + " DOUBLE, " + Y + " DOUBLE, " + Z + " DOUBLE, " 
    		+ CUSTOM_FIELD + " TEXT," + LAST_MODIFIED + " TEXT," 
    		+ USER_ID + " INTEGER," + MAP_ID + " INTEGER," + EXPEDITION_ID + " INTEGER" + ")";
    
    private static final String TABLE_LOCATION_COLUMN_DEFINITION = TABLE_LOCATION
    		+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + TIME + " TEXT,"
    		+ X + " DOUBLE, " + Y + " DOUBLE, " + Z + " DOUBLE, "
    		+ USER_ID + " INTEGER," + MAP_ID + " INTEGER," + EXPEDITION_ID + " INTEGER" + ")";
    
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String CREATE_TABLE_MAP = CREATE_TABLE + TABLE_MAP_COLUMN_DEFINITION;
    private static final String CREATE_TABLE_SAMPLE = CREATE_TABLE + TABLE_SAMPLE_COLUMN_DEFINITION;
    private static final String CREATE_TABLE_LOCATION = CREATE_TABLE + TABLE_LOCATION_COLUMN_DEFINITION;
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(MSG_TAG, "onCreate");
		
		db.execSQL(CREATE_TABLE_MAP);
		db.execSQL(CREATE_TABLE_SAMPLE);
        db.execSQL(CREATE_TABLE_LOCATION);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.d(MSG_TAG, "onOpen");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MAP_COLUMN_DEFINITION);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SAMPLE_COLUMN_DEFINITION);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_COLUMN_DEFINITION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(MSG_TAG, "onUpgrade");
		
		// on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAMPLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        
        // create new tables
        onCreate(db);
	}
}
