package edu.cmu.sv.trailscribe.controller;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationManager {
	private static final String MSG_TAG = "LocationManager";
	
	private Context mContext;
	
	public LocationManager(Context context) {
		this.mContext = context;
	}
	
//	FIXME: Check only when the application is started
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
