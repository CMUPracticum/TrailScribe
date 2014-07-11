package edu.cmu.sv.trailscribe.view;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.MapsController;
import edu.cmu.sv.trailscribe.dao.LocationDataSource;
import edu.cmu.sv.trailscribe.dao.SampleDataSource;
import edu.cmu.sv.trailscribe.model.Sample;


@SuppressLint("NewApi")
public class MapsActivity extends BaseActivity implements OnClickListener {
	
	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme("MapActivity", "Display map and layers", R.color.green);
	public static String MSG_TAG = "MapsActivity";

//	Controllers
	private MapsController mController;
	
//	Views
	private WebView mWebView;
	private Button mSamplesButton;
	private Button mCurrentLocationButton;
	private Button mPositionHistoryButton;
	private Button mKmlButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_maps);

		setMap();
		setListener();
	}
	
	private void setListener() {
	    mSamplesButton = (Button) findViewById(R.id.maps_samples);
		mCurrentLocationButton = (Button) findViewById(R.id.maps_current_location);
		mPositionHistoryButton = (Button) findViewById(R.id.maps_position_history);
		mKmlButton = (Button) findViewById(R.id.maps_kml);
		
		mSamplesButton.setOnClickListener(this);
		mCurrentLocationButton.setOnClickListener(this);
		mPositionHistoryButton.setOnClickListener(this);
		mKmlButton.setOnClickListener(this);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setMap() {
		mWebView = (WebView) findViewById(R.id.maps_webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, "android");
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setUseWideViewPort(false);
		mWebView.setWebViewClient(new WebViewClient());
		
		// Setting to give OpenLayers access to local KML files
		// Sets whether JavaScript running in the context of a file scheme URL should be allowed to 
		// access content from any origin.
		mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);

		mController = new MapsController();
		mWebView.loadUrl(mController.getURL());
	}
	
	private void setLayers(MessageToWebview message) {
		mWebView.loadUrl("javascript:setLayers(\"" + message.getMessage() + "\")");
	}
	
	@JavascriptInterface
	public String getSamples() {
		SampleDataSource dataSource = new SampleDataSource(mDBHelper);
		
		List<Sample> samples = dataSource.getAll();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{'points':[");
		for (int i = 0; i < samples.size(); i++) {
			Sample sample = samples.get(i);
			
			buffer.append("{'x':'").append(sample.getX()).append("', ");
			buffer.append("'y':'").append(sample.getY()).append("'}");
			
			if (i != samples.size() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append("]}'");
		
		JSONObject mapPoints = null;
		try {
			mapPoints = new JSONObject(buffer.toString());
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
		LocationDataSource dataSource = new LocationDataSource(mDBHelper);
		
		List<edu.cmu.sv.trailscribe.model.Location> locations = dataSource.getAll();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{'points':[");
		for (int i = 0; i < locations.size(); i++) {
		    edu.cmu.sv.trailscribe.model.Location locationHistory = locations.get(i);
			
			buffer.append("{'x':'").append(locationHistory.getX()).append("',");
			buffer.append("'y':'").append(locationHistory.getY()).append("'}");
			
			if (i != locations.size() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append("]}'");
		
		JSONObject mapPoints = null;
		try {
			mapPoints = new JSONObject(buffer.toString());
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
		case R.id.maps_samples:
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
		case R.id.maps_current_location:
			state = mCurrentLocationButton.getText().toString();
			willDisplay = (state.equals(getResources().getString(R.string.map_display_current_location)));
			
			if (willDisplay) {
				if (mLocation == null) {
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
		case R.id.maps_position_history:
			state = mPositionHistoryButton.getText().toString();
			willDisplay = (state.equals(getResources().getString(R.string.map_display_position_history)));
			
			if (willDisplay) {
				mPositionHistoryButton.setText(R.string.map_hide_position_history);
				message = MessageToWebview.DisplayPositionHistory;				
			} else {
				mPositionHistoryButton.setText(R.string.map_display_position_history);
				message = MessageToWebview.HidePositionHistory;
			}
			break;
		case R.id.maps_kml:
			state = mKmlButton.getText().toString();
			willDisplay = (state.equals(getResources().getString(R.string.map_display_kml)));
			
			if (willDisplay) {
				mKmlButton.setText(R.string.map_hide_kml);
				message = MessageToWebview.DisplayKML;
			} else {
				mKmlButton.setText(R.string.map_display_kml);
				message = MessageToWebview.HideKML;
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
		
		super.onLocationChanged(location);
	}
	
	private enum MessageToWebview {
		Default("default"),
		
		DisplaySamples("DisplaySamples"),
		HideSamples("HideSamples"),
		DisplayCurrentLocation("DisplayCurrentLocation"),
		HideCurrentLocation("HideCurrentLocation"),
		DisplayPositionHistory("DisplayPositionHistory"),
		HidePositionHistory("HidePositionHistory"),
		DisplayKML("DisplayKML"),
		HideKML("HideKML");
		
		private final String message;
		MessageToWebview(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
}