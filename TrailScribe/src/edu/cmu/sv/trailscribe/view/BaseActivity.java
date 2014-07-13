package edu.cmu.sv.trailscribe.view;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.dao.DBHelper;

public class BaseActivity extends Activity implements 
	LocationListener {
	
	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme("Default", "default", R.color.blue);
	public static String MSG_TAG = "BaseActivity";
	
//	Application
	protected static TrailScribeApplication mApplication;
	
//	Database
	protected static DBHelper mDBHelper;
	
//	Location
	protected static Location mLocation;
	protected static LocationClient mLocationClient;
	
//	View
    protected DrawerLayout mDrawerLayout;
    protected ActionBar mActionBar;
    protected ActionBarDrawerToggle mDrawerToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (TrailScribeApplication) getApplication();
		mDBHelper = mApplication.getDBHelper();
		mLocationClient = mApplication.getLocationClient();
		mLocation = mApplication.getLocation();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient = mApplication.getLocationClient();
		mLocation = mApplication.getLocation();
    }
	
	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
    }

	@Override
	public void onLocationChanged(Location location) {
	    mLocation = location;
	}
	
	protected void setActionBar(String color) {
	    mActionBar = getActionBar();
        mActionBar.setTitle("");
        mActionBar.setIcon(R.drawable.icon_trailscribe);
        
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
	}
}
