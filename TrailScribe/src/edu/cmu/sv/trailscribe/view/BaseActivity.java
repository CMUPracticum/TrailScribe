package edu.cmu.sv.trailscribe.view;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.dao.DBHelper;

//  Location-related actions such as saving to database is handled by TrailScribeApplication.
//  Activities implement LocationListener just to make sure activities are aware of location changes.  
public class BaseActivity extends Activity implements LocationListener {
	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme("Default", "default", R.color.blue);
	public static String MSG_TAG = "BaseActivity";
	
//	Application
	protected static TrailScribeApplication mApplication;
	
//	Database
	protected static DBHelper mDBHelper;
	
//	Storage
	public static String STORAGE_PATH ;
	
//	Location
	protected static Location mLocation;
	protected static LocationManager mLocationManager;
	
//	View
    protected DrawerLayout mDrawerLayout;
    protected ActionBar mActionBar;
    protected ActionBarDrawerToggle mDrawerToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (TrailScribeApplication) getApplication();
		mDBHelper = TrailScribeApplication.getDBHelper();
		STORAGE_PATH = TrailScribeApplication.STORAGE_PATH;
		
		setLocation();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocation = mApplication.getLocation();
    }
	
	@Override
	protected void onStop() {
		super.onStop();
    }
	
	private void setLocation() {
        mLocationManager = mApplication.getLocationManager();
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) this);
        
        mLocation = mApplication.getLocation();
	}
	
	protected void setActionBar(String color) {
	    mActionBar = getActionBar();
        mActionBar.setTitle("");
        mActionBar.setIcon(R.drawable.icon_trailscribe);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
	}

    @Override
    public void onProviderDisabled(String provider) {
//        Handled by TrailScribeApplication
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Handled by TrailScribeApplication
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Handled by TrailScribeApplication
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = mApplication.getLocation();
    }
}
