package edu.cmu.sv.trailscribe.view;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.LocationDataSource;

public class TrailScribeApplication extends Application 
    implements 
    LocationListener {
    
	private static final String MSG_TAG = "TrailScribeApplication";
	
	private static Context mContext;
	
//	Database
	private static DBHelper mDBHelper;

//	Location
    protected static Location mLocation;
    private LocationManager mLocationManager;
    
//  Time
    private static Time mTime;
    
	public TrailScribeApplication() {
		
	}
	
	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mDBHelper = new DBHelper(mContext);
		mTime = new Time();
		
		setLocationManager();
	}
	
	public DBHelper getDBHelper() {
		return mDBHelper;
	}
	
	public LocationManager getLocationManager() {
	    return mLocationManager;
	}
	
	public Location getLocation() {
	    return mLocation;
	}
	
    private void setLocationManager() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) this);
        } catch (Exception e) {
            Log.e(MSG_TAG, e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLocation != null && Math.abs(location.distanceTo(mLocation)) <= 10) {
//          Ignore minor changes
            return;
        }
        
        mLocation = location;
        saveLocationToDatabase();
    }
    
    private void saveLocationToDatabase() {
        LocationDataSource dataSource = new LocationDataSource(mDBHelper);

        mTime.setToNow();
        Log.d(MSG_TAG, "Current time:" + mTime.format2445());
        Log.d(MSG_TAG, "Current location: (" + mLocation.getLatitude() + "," + mLocation.getLongitude() + "), altitude=" + mLocation.getAltitude());
        
        edu.cmu.sv.trailscribe.model.Location loc = 
                new edu.cmu.sv.trailscribe.model.Location(
                        (int) (Math.random() * Integer.MAX_VALUE), mTime.format2445(), 
                        mLocation.getLongitude(), mLocation.getLatitude(), mLocation.getAltitude(), 
                        0, 0, 0);
        dataSource.add(loc);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(MSG_TAG, "Location provider is disabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(MSG_TAG, "Location provider is enabled: " + provider);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        saveLocationToDatabase();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String statusMessage = new String();
        switch(status) {
        case LocationProvider.AVAILABLE:
            statusMessage = "AVAILABLE";
            break;
        case LocationProvider.OUT_OF_SERVICE:
            statusMessage = "OUT_OF_SERVICE";
            break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
            statusMessage = "TEMPORARILY_UNAVAILABLE";
            break;
            default:
                return;
        }
        
        Log.d(MSG_TAG, "Provider status has changed:" + provider + ", " + statusMessage);
    }    
}
