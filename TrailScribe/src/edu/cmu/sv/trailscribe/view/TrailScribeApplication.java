package edu.cmu.sv.trailscribe.view;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.LocationDataSource;

public class TrailScribeApplication extends Application 
    implements 
    LocationListener,
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener {
    
	private static final String MSG_TAG = "TrailScribeApplication";
	
	private static Context mContext;
	
//	Database
	private static DBHelper mDBHelper;

//	Location
    protected static Location mLocation;
    protected static LocationClient mLocationClient;
    
//  Time
    private static Time mTime;
    
	public TrailScribeApplication() {
		
	}
	
	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mDBHelper = new DBHelper(mContext);
		mTime = new Time();
		
		isPlayServicesAvailable();
		setLocationClient();
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
	
	public LocationClient getLocationClient() {
	    return mLocationClient;
	}
	
	public Location getLocation() {
	    return mLocation;
	}
	
    private void setLocationClient() {
        mLocationClient = new LocationClient(this, this, this);
        
        try {
            if (!TrailScribeApplication.isPlayServicesAvailable()) {
                Log.e(MSG_TAG, "Google Play service is not available");
                return;
            }
            
            mLocationClient.connect();
        } catch (Exception e) {
            Log.e(MSG_TAG, e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(MSG_TAG, "Application has failed to connect to location service");
        Toast.makeText(getApplicationContext(), 
                "Application has failed to connect to location service", Toast.LENGTH_SHORT).show();

//      Google Play service can resolve some connection error. 
//      However, it requires to start the Google Play services activity.
//      For the simplicity of the application, this feature is not implemented for now. 
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(MSG_TAG, "Application is connected to Google Play services");
        
        try {
            mLocation = mLocationClient.getLastLocation();
            if (mLocation == null) {
                Log.e(MSG_TAG, "Null last location");
                return;
            }
            
            saveLocationToDatabase();
        } catch (Exception e) {
            Log.e(MSG_TAG, e.getMessage());
        }        
    }

    @Override
    public void onDisconnected() {
        Log.e(MSG_TAG, "Application is disconnected from location service");
        Toast.makeText(getApplicationContext(), 
                "Application is disconnected from location service", Toast.LENGTH_SHORT).show();        
    }

    @Override
    public void onLocationChanged(Location location) {
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
	
}
