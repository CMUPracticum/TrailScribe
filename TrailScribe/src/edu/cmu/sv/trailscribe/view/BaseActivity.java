package edu.cmu.sv.trailscribe.view;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.LocationDataSource;

public class BaseActivity extends Activity implements 
	LocationListener,
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme("Default", "default", R.color.blue);
	public static String MSG_TAG = "BaseActivity";
	
//	Application
	protected static TrailScribeApplication mApplication;
	
//	Database
	protected static DBHelper mDBHelper;
	
//	Location
	protected static Location mLocation;
	protected static LocationClient mLocationClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (TrailScribeApplication) getApplication();
		mDBHelper = mApplication.getDBHelper();
		
		setLocationClient();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		setLocationClient();
    }
	
	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e(MSG_TAG, "Application has failed to connect to location service");
		Toast.makeText(getApplicationContext(), 
				"Application has failed to connect to location service", Toast.LENGTH_SHORT).show();

//		Google Play service can resolve some connection error. 
//		However, it requires to start the Google Play services activity.
//		For the simplicity of the application, this feature is not implemented for now.		
	}

	@Override
	public void onConnected(Bundle bundle) {
            if (mLocationClient.isConnected()) {
                Log.d(MSG_TAG, "Application is connected to Google Play services");
                mLocation = mLocationClient.getLastLocation();
                if (mLocation == null) {
                    Log.e(MSG_TAG, "Null last location");
                } else {
                    saveLocationToDatabase();
                }
            } else {
                Log.e(MSG_TAG, "onConnect called but client is not connected");
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
	    saveLocationToDatabase();
//		Action when location has changed will be decided by the activity 
	}
	
	protected void setTitleBar(int viewId, int themeColor) {
		View titleBar = (View) findViewById(viewId);
		titleBar.setBackgroundColor(getResources().getColor(themeColor));
	}
	
	private void saveLocationToDatabase() {
	    LocationDataSource dataSource = new LocationDataSource(mDBHelper);
	    edu.cmu.sv.trailscribe.model.Location loc = 
	            new edu.cmu.sv.trailscribe.model.Location(
	                    (int) (Math.random() * Integer.MAX_VALUE), "default time", 
	                    mLocation.getLongitude(), mLocation.getLatitude(), mLocation.getAltitude(), 
	                    0, 0, 0);
        dataSource.add(loc);
	}
	
	private void setLocationClient() {
            setLocationClient(new LocationClient(this, this, this));
	}

    // code to allow overiding of location client, needed for testing
    public void setLocationClient(LocationClient client) {
        mLocationClient = client;
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
}
