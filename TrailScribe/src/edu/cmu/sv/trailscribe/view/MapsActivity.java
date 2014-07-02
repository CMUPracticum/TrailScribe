package edu.cmu.sv.trailscribe.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.LocationManager;
import edu.cmu.sv.trailscribe.controller.MapsController;


public class MapsActivity extends Activity implements 
	OnClickListener, 
	LocationListener,
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	public static final ActivityTheme ACTIVITY_THEME = 
			new ActivityTheme("MapActivity", "Display map and layers", R.color.green);
	private static final String MSG_TAG = "MapsActivity";

//	Controllers
	private MapsController mController;
	
//	FIXME: Location should be independent from Activity
//	Location
	private LocationClient mLocationClient;
	private Location mLocation;
	
//	Views
	private WebView mWebView;
	private Button mSamplesButton;
	private Button mCurrentLocationButton;
	private Button mPositionHistoryButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView();
		
		mLocationClient = new LocationClient(this, this, this);
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
	
	private void setView() {
		setContentView(R.layout.activity_maps);
		
		setMap();
		setTitleBar();
		setListener();
	}
	
//	FIXME: Bad design, the code should be reusable
	private void setTitleBar() {
		View titleBar = (View) findViewById(R.id.mapsview_titlebar);
		titleBar.setBackgroundColor(getResources().getColor(ACTIVITY_THEME.getActivityColor()));
	}
	
	private void setListener() {
		mSamplesButton = (Button) findViewById(R.id.mapsview_samples);
		mCurrentLocationButton = (Button) findViewById(R.id.mapsview_current_location);
		mPositionHistoryButton = (Button) findViewById(R.id.mapsview_position_history);
		
		mSamplesButton.setOnClickListener(this);
		mCurrentLocationButton.setOnClickListener(this);
		mPositionHistoryButton.setOnClickListener(this);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setMap() {
		mWebView = (WebView) findViewById(R.id.mapsview_webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, "android");
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setUseWideViewPort(false);
		mWebView.setWebViewClient(new WebViewClient());

		mController = new MapsController();
		mWebView.loadUrl(mController.getURL());
	}
	
	private void setLayers(MessageToWebview message) {
		mWebView.loadUrl("javascript:setLayers(\"" + message.getMessage() + "\")");
	}
	
	private void setLocationClient() {
		try {
			LocationManager manager = new LocationManager(this);
			if (!manager.isPlayServicesAvailable()) {
				Log.e(MSG_TAG, "Google Play service is not available");
				return;
			}
			
			mLocationClient.connect();
		} catch (Exception e) {
			Log.e(MSG_TAG, e.getMessage());
		}
	}
	
	@JavascriptInterface
	public String getSamples() {
//		TODO Import actual samples in the future
		JSONObject mapPoints = null;
		try {
			mapPoints = new JSONObject("{'points':[{'x':'-122.04451', 'y':'37.41800'},{'x':'-122.07451', 'y':'37.41800'}, {'x':'-122.10451', 'y':'37.39800'}]}'");
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return mapPoints.toString();
	}
	
	@JavascriptInterface
	public String getCurrentLocation() {
		JSONObject mapPoints = null;
		
		try {
			double la = mLocation.getLatitude();
			double lng = mLocation.getLongitude();
			mapPoints = new JSONObject("{'points':[{'x':'" + lng + "', 'y':'" + la + "'}]}'");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mapPoints.toString();
	}
	
	@JavascriptInterface
	public String getPositionHistory() {
	// TODO: Import actual position history
		JSONObject mapPoints = null;
		try {
			// TODO: Change the points
			mapPoints = new JSONObject("{'points':[{'x':'-122.049841', 'y':'37.402865'},"
					+ "{'x':'-122.051258', 'y':'37.406001'}, "
					+ "{'x':'-122.053918', 'y':'37.411183'},"
					+ "{'x':'-122.053768', 'y':'37.413296'},"
					+ "{'x':'-122.053883', 'y':'37.416132'}]}'");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mapPoints.toString();
	}
	
	@Override
	public void onClick(View v) {
		String state;
		boolean willDisplay;
		MessageToWebview message = MessageToWebview.Default;
		
		switch (v.getId()) {
		case R.id.mapsview_samples:
			state = mSamplesButton.getText().toString();
			willDisplay = (state.equals(getResources().getString(R.string.map_display_samples)));
			
			Toast.makeText(getApplicationContext(), 
					"Sample locations are hard-coded currently", Toast.LENGTH_SHORT).show();
			if (willDisplay) {
				mSamplesButton.setText(R.string.map_hide_samples);
				message = MessageToWebview.DisplaySamples;
			} else {
				mSamplesButton.setText(R.string.map_display_samples);
				message = MessageToWebview.HideSamples;
			}
			break;
		case R.id.mapsview_current_location:
			state = mCurrentLocationButton.getText().toString();
			willDisplay = (state.equals(getResources().getString(R.string.map_display_current_location)));
			
			if (willDisplay) {
				if (!mLocationClient.isConnected() || mLocation == null) {
					Toast.makeText(getApplicationContext(), 
							"Current location is not available", Toast.LENGTH_SHORT).show();
					return;
				}
				
				mCurrentLocationButton.setText(R.string.map_hide_current_location);
				message = MessageToWebview.DisplayCurrentLocation;
			} else {
				mCurrentLocationButton.setText(R.string.map_display_current_location);
				message = MessageToWebview.HideCurrentLocation;
			}
			break;
		case R.id.mapsview_position_history:
			state = mPositionHistoryButton.getText().toString();
			willDisplay = (state.equals(getResources().getString(R.string.map_display_position_history)));
			
			Toast.makeText(getApplicationContext(), 
					"Position history is hard-coded currently", Toast.LENGTH_SHORT).show();
			if (willDisplay) {
				mPositionHistoryButton.setText(R.string.map_hide_position_history);
				message = MessageToWebview.DisplayPositionHistory;				
			} else {
				mPositionHistoryButton.setText(R.string.map_display_position_history);
				message = MessageToWebview.HidePositionHistory;
			}
			break;
		default:
				Toast.makeText(getApplicationContext(), 
						"Sorry, the feature is not implemented yet!", Toast.LENGTH_SHORT).show();
				return;
		}
		
		setLayers(message);
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
		mLocation = location;
		
//		TODO: Verify if map layer changes whenever the location has changed
		Toast.makeText(getApplicationContext(), 
				"onLocationChanged: (" + mLocation.getLatitude() + "," + mLocation.getLongitude() + ")", Toast.LENGTH_SHORT).show();
		String state = mSamplesButton.getText().toString();
		boolean shouldisplay = (state.equals(getResources().getString(R.string.map_hide_current_location)));
		if (shouldisplay) {
			setLayers(MessageToWebview.DisplayCurrentLocation);
		}
	};
	
	private enum MessageToWebview {
		Default("default"),
		
		DisplaySamples("DisplaySamples"),
		HideSamples("HideSamples"),
		DisplayCurrentLocation("DisplayCurrentLocation"),
		HideCurrentLocation("HideCurrentLocation"),
		DisplayPositionHistory("DisplayPositionHistory"),
		HidePositionHistory("HidePositionHistory");
		
		private final String message;
		MessageToWebview(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
}