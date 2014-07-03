package edu.cmu.sv.trailscribe.view;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class TrailScribeApplication extends Application {
	private static final String MSG_TAG = "TrailScribeApplication";
	
	private Context mContext;

	public TrailScribeApplication() {
	}
	
	@Override
	public void onCreate() {
		this.mContext = getApplicationContext();
		isPlayServicesAvailable();
	}
	
	public boolean isPlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        
        if (resultCode == ConnectionResult.SUCCESS) {
            Log.d(MSG_TAG, "Google Play services is available");
            return true;
        } 
        
        Log.e(MSG_TAG, GooglePlayServicesUtil.getErrorString(resultCode));
        return false;
	}
	
}
