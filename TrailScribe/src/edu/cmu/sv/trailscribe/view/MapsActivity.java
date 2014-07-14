package edu.cmu.sv.trailscribe.view;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
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
	
//	States
	private boolean mIsDisplaySamples = false;
	private boolean mIsDisplayCurrentLocation = false;
	private boolean mIsDisplayPositionHistory = false;
	private boolean mIsDisplayKML = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_maps);

		setMap();
		setActionBar(getResources().getString(ACTIVITY_THEME.getActivityColor()));
		setListener();
	}
	
	@Override
	protected void setActionBar(String color) {
	    super.setActionBar(color);
	    
	    mActionBar.setIcon(R.drawable.button_settings);
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.maps_layout);
	    
	    mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, 
                R.drawable.icon_trailscribe, R.string.map_display_tools, R.string.map_hide_tools) {

            public void onDrawerClosed(View view) {
                mActionBar.setIcon(R.drawable.button_settings);
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                mActionBar.setIcon(R.drawable.button_settings_toggle);
                super.onDrawerOpened(drawerView);
            }
        };

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	        return true;
	    }

	    return super.onOptionsItemSelected(item);
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
        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
	public String getCurrentLocation() throws Exception {
	    if (mLocation == null) {
            Toast.makeText(getApplicationContext(), 
                    "Current location is not available", Toast.LENGTH_SHORT).show();
	        throw new Exception("Current location is not available");
	    }
	    
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
		MessageToWebview message = MessageToWebview.Default;
		
		switch (v.getId()) {
		case R.id.maps_samples:
//		    Hide samples if they are currently displayed 
		    if (mIsDisplaySamples) {
		        message = MessageToWebview.HideSamples;
		        mSamplesButton.setBackgroundResource(R.drawable.button_samples);
		    } else {
		        message = MessageToWebview.DisplaySamples;
		        mSamplesButton.setBackgroundResource(R.drawable.button_samples_toggle);
		    }
		    
		    mIsDisplaySamples = !mIsDisplaySamples;
			break;
		case R.id.maps_current_location:
//          Hide current location if it is currently displayed 
		    if (mIsDisplayCurrentLocation) {
		        message = MessageToWebview.HideCurrentLocation;
		        mCurrentLocationButton.setBackgroundResource(R.drawable.button_current_location);
		    } else {
		        message = MessageToWebview.DisplayCurrentLocation;
		        mCurrentLocationButton.setBackgroundResource(R.drawable.button_current_location_toggle);
		    }
          
		    mIsDisplayCurrentLocation = !mIsDisplayCurrentLocation;
			break;
		case R.id.maps_position_history:
//          Hide location history if it is currently displayed
            if (mIsDisplayPositionHistory) {
                message = MessageToWebview.HidePositionHistory;
                mPositionHistoryButton.setBackgroundResource(R.drawable.button_position_history);
            } else {
                message = MessageToWebview.DisplayPositionHistory;
                mPositionHistoryButton.setBackgroundResource(R.drawable.button_position_history_toggle);
            }
          
            mIsDisplayPositionHistory = !mIsDisplayPositionHistory;
			break;
		case R.id.maps_kml:
			if (mIsDisplayKML) {
                message = MessageToWebview.HideKML;
                mKmlButton.setBackgroundResource(R.drawable.button_kml);
            } else {
                message = MessageToWebview.DisplayKML;
                mKmlButton.setBackgroundResource(R.drawable.button_kml_toggle);
            }
          
			mIsDisplayKML = !mIsDisplayKML;
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