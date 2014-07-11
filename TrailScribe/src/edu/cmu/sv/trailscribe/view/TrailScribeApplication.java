package edu.cmu.sv.trailscribe.view;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.cmu.sv.trailscribe.dao.DBHelper;

public class TrailScribeApplication extends Application {
	private static final String MSG_TAG = "TrailScribeApplication";
	
	private static Context mContext;
	private static DBHelper mDBHelper;

	public TrailScribeApplication() {
		
	}
	
	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mDBHelper = new DBHelper(mContext);
		
		isPlayServicesAvailable();
	}
	
	public static boolean isPlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        
        if (resultCode == ConnectionResult.SUCCESS) {
            Log.d(MSG_TAG, "Google Play services is available");
            return true;
        } 
        
        Log.e(MSG_TAG, GooglePlayServicesUtil.getErrorString(resultCode));
        return false;
	}
	
	public DBHelper getDBHelper() {
		return mDBHelper;
	}
	
}
