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

public class BaseActivity extends Activity implements 
	LocationListener,
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme("Default", "default", R.color.blue);
	public static String MSG_TAG = "BaseActivity";
	
//	Application
	protected TrailScribeApplication mApplication;
	
//	Location
	protected Location mLocation;
	protected LocationClient mLocationClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (TrailScribeApplication) getApplication();
		
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
		Log.d(MSG_TAG, "Application is connected to Google Play services");

		try {
			mLocation = mLocationClient.getLastLocation();
			if (mLocation == null) {
				Log.e(MSG_TAG, "Null last location");
			}
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
//		Action when location has changed will be decided by the activity 
	}
	
	protected void setTitleBar(int viewId, int themeColor) {
		View titleBar = (View) findViewById(viewId);
		titleBar.setBackgroundColor(getResources().getColor(themeColor));
	}
	
	private void setLocationClient() {
		mLocationClient = new LocationClient(this, this, this);
		
		try {
			if (!mApplication.isPlayServicesAvailable()) {
				Log.e(MSG_TAG, "Google Play service is not available");
				return;
			}
			
			mLocationClient.connect();
		} catch (Exception e) {
			Log.e(MSG_TAG, e.getMessage());
		}
	}
}
