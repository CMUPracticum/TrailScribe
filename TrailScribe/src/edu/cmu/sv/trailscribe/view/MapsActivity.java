package edu.cmu.sv.trailscribe.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.MapsController;
import edu.cmu.sv.trailscribe.dao.LocationDataSource;
import edu.cmu.sv.trailscribe.dao.SampleDataSource;
import edu.cmu.sv.trailscribe.utils.JsonHelper;
import edu.cmu.sv.trailscribe.utils.StorageSystemHelper;
import edu.cmu.sv.trailscribe.dao.MapDataSource;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.model.data.Sample;

public class MapsActivity extends BaseActivity 
    implements OnClickListener, SensorEventListener, OnNavigationListener {
	
	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme(
	        "Maps", "Display map and layers", R.color.green);
	public static String MSG_TAG = "MapsActivity";

//	Controllers
	private MapsController mController;
	
//	Views
	private WebView mWebView;
	private TextView mCoordinateTextView;
	private Button mSamplesButton;
	private Button mCurrentLocationButton;
	private Button mPositionHistoryButton;
	private Button mKmlButton;
	
//	States
	private boolean mIsDisplaySamples = false;
	private boolean mIsDisplayCurrentLocation = false;
	private boolean mIsDisplayPositionHistory = false;
	private boolean mIsDisplayKML = false;
	
//	Orientation
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	private float mAzimuth;
	
//  Overlays
	private ArrayList<String> mOverlays;
	private ArrayList<String> mSelectedOverlayNames;
	
//	Base Map
	private ArrayList<String> mBaseMaps;
	private SpinnerAdapter mSpinnerAdapter;
	private int mSelectedMapPosition = -1; // position of the map that was selected in the spinner

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView();
		setSensor();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }	
	
	private void setView() {
		setContentView(R.layout.activity_maps);

		setActionBar(getResources().getString(ACTIVITY_THEME.getActivityColor()));
		
		setMap();
		setButton();
		setTextView();
	}
	
    @SuppressWarnings("deprecation")
    private void setSensor() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);      
    }
    
	@Override
	protected void setActionBar(String color) {
	    super.setActionBar(color);
	    
	    mActionBar.setIcon(R.drawable.button_settings);
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.maps_layout);
	    
//	    Define the listen of the navigation drawer
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

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
//      Create spinner in action bar 
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        
        setSpinner();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	        return true;
	    }

	    return super.onOptionsItemSelected(item);
	}
	
	private void setButton() {
	    mSamplesButton = (Button) findViewById(R.id.maps_samples);
		mCurrentLocationButton = (Button) findViewById(R.id.maps_current_location);
		mPositionHistoryButton = (Button) findViewById(R.id.maps_position_history);
		mKmlButton = (Button) findViewById(R.id.maps_kml);
		
		mSamplesButton.setOnClickListener(this);
		mCurrentLocationButton.setOnClickListener(this);
		mPositionHistoryButton.setOnClickListener(this);
		mKmlButton.setOnClickListener(this);
	}
	
	private void setTextView() {
	    mCoordinateTextView = (TextView) findViewById(R.id.maps_coordinate);
	    updateCoordinateTextView();
	}
	
	private void setBaseMaps() {
        MapDataSource dataSource = new MapDataSource(mDBHelper);
        ArrayList<Map> mapsInDatabase = (ArrayList<Map>) dataSource.getAll();
        HashSet<String> mapsInStorage = StorageSystemHelper.getBaseMapsFromStorage();
        
//      Only populate maps that are in both device database and device storage
        mBaseMaps = new ArrayList<String>();
        for (int i = 0; i < mapsInDatabase.size(); i++) {
            if (mapsInStorage.contains(mapsInDatabase.get(i).getName())) {
                mBaseMaps.add(mapsInDatabase.get(i).getName());
            }
        }
        
//      Add the title of the spinner to the head of the list, will be ignored if it is selected
        mBaseMaps.add(0, getResources().getString(R.string.map_display_basemap));
	    
	}
	
	private void setSpinner() {
	    setBaseMaps();
        
        String[] basemaps = new String[mBaseMaps.size()];
        mBaseMaps.toArray(basemaps);

//      Create spinner adapter, then add the adapter and the listener to the action bar
        mSpinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, mBaseMaps);        
        mActionBar.setListNavigationCallbacks(mSpinnerAdapter, this);        
	}
	
	private void updateCoordinateTextView() {
	    if (mLocation == null) {
	        mCoordinateTextView.setText(R.string.map_coordinate);
	        return;
	    }
	    
	    mCoordinateTextView.setText("Speed: " + mLocation.getSpeed() + " m/s, Lat: " + 
	    							mLocation.getLatitude() + ", Lng: " + 
	    							mLocation.getLongitude());	    
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setMap() {
		mWebView = (WebView) findViewById(R.id.maps_webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, "android");
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setUseWideViewPort(false);
		mWebView.setWebViewClient(new WebViewClient());
		
//		Setting to give OpenLayers access to local KML files
//		Sets whether JavaScript running in the context of a file scheme URL should be allowed to 
//		access content from any origin.
		mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);

		mController = new MapsController();
		mWebView.loadUrl(mController.getURL());
	}
	
	private void setLayers(MessageToWebview message) {
		mWebView.loadUrl("javascript:setLayers(\"" + message.getMessage() + "\")");
	}
	
	@JavascriptInterface
	public String getCurrentMap() throws Exception {		
//	    First request for base map
		if (mSelectedMapPosition == -1) {
			mSelectedMapPosition = 1; // By default, show the first map as base map
		}
		
		setBaseMaps();
//		Show error message if there is no map on the device, the only item in mBaseMaps is the title
		if (mBaseMaps.size() == 1) {
            String errorMessage = "There are no map on the device. Download maps in Sync Center";
            Log.e(MSG_TAG, errorMessage);
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            throw new Exception(errorMessage);		    
		}
		
		MapDataSource dataSource = new MapDataSource(mDBHelper);
		
//		Find selected map from database based on selected map name
		String selectedMapName = mBaseMaps.get(mSelectedMapPosition);
		Map currentMap = null;
		currentMap = dataSource.get(selectedMapName);
		
		if (currentMap == null) {
		    String errorMessage = "Selected map is not found in database";
		    Log.e(MSG_TAG, errorMessage);
		    throw new Exception(errorMessage);
		}
		
		String type = getMapTileType(currentMap);
		currentMap.setType(type);
		
		return JsonHelper.getCurrentMapJson(currentMap);		
	}
	
	/**
	 * TODO: Implement
	 * Given a map, return the file extension
	 * @param map
	 * @return
	 */
	private String getMapTileType(Map map) {
		return "jpg";
	}
	
	@JavascriptInterface
	public String getOrientation() {
	    return JsonHelper.getOrientationJson(mAzimuth);
	}
	
	@JavascriptInterface()
	public String getSample(String id) {
	    SampleDataSource dataSource = new SampleDataSource(mDBHelper);
	    
        ArrayList<Sample> samples = new ArrayList<Sample>();
        Sample sample = dataSource.get(Long.parseLong(id));
        
        if (sample == null) return new String();
        samples.add(sample);
	    
        return JsonHelper.getSamplesJson(samples);
	}
	
	@JavascriptInterface
	public String getSamples() {
		SampleDataSource dataSource = new SampleDataSource(mDBHelper);
		
		List<Sample> samples = dataSource.getAll();
		return JsonHelper.getSamplesJson(samples);
	}
	
	@JavascriptInterface
	public String getCurrentLocation() throws Exception {
	    if (mLocation == null) {
            Toast.makeText(getApplicationContext(), 
                    "Current location is not available", Toast.LENGTH_SHORT).show();
	        throw new Exception("Current location is not available");
	    }
	    
	    return JsonHelper.getCurrentLocationJson(mLocation);
	}
	
	@JavascriptInterface
	public String getPositionHistory() {
		LocationDataSource dataSource = new LocationDataSource(mDBHelper);
		
		List<edu.cmu.sv.trailscribe.model.data.Location> locations = dataSource.getAll();
		return JsonHelper.getPositionHistoryJson(locations);
	}
	
    @JavascriptInterface
    public String getKMLs() {
        return JsonHelper.getSelectedKmlJson(mSelectedOverlayNames);
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
		        mCurrentLocationButton.setBackgroundResource(
		                R.drawable.button_current_location_toggle);
		        
		        setLayers(MessageToWebview.PanToCurrentLocation);
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
                mPositionHistoryButton.setBackgroundResource(
                        R.drawable.button_position_history_toggle);
            }
            
            mIsDisplayPositionHistory = !mIsDisplayPositionHistory;
			break;
		case R.id.maps_kml:
//          Hide KML if it is currently displayed
			if (mIsDisplayKML) {
                message = MessageToWebview.HideKML;
                mKmlButton.setBackgroundResource(R.drawable.button_kml);
                mIsDisplayKML = false;
            } else {
//              Message to webview will be sent after positive button in the selector is clicked 
                createKMLSelector();
                return;
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
	    super.onLocationChanged(location);

//	    Ignore if location has not changed (more than 3 meters)
	    if (!mApplication.isLocationChanged()) {
	        return;
	    }

	    updateCoordinateTextView();
		if (mIsDisplayCurrentLocation) {
		    setLayers(MessageToWebview.HideCurrentLocation);
		    setLayers(MessageToWebview.DisplayCurrentLocation);
		}
		
        if (mIsDisplayPositionHistory) {
            setLayers(MessageToWebview.HidePositionHistory);
            setLayers(MessageToWebview.DisplayPositionHistory);
        }		
	}
	
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        
//      Ignore minor rotations
        if (Math.abs(values[0] - mAzimuth) <= 5) {
            return;
        }
        
        mAzimuth = (int) values[0];
        if (mIsDisplayCurrentLocation) {
            setLayers(MessageToWebview.HideCurrentLocation);
            setLayers(MessageToWebview.DisplayCurrentLocation);
        }
    }
    
    private void displayOverlays(List<String> overlayNames) {
        mSelectedOverlayNames = (ArrayList<String>) overlayNames;
        
        MessageToWebview message = MessageToWebview.DisplayKML;
        setLayers(message);
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//      Log.d(MSG_TAG, "Sensor accuracy has changed: " + sensor.getName() + ", " + accuracy);
    }
    
//  Create the selector dialog for displaying KML layers
    private void createKMLSelector() {
        mOverlays = StorageSystemHelper.getOverlaysFromStorage();
        
        String[] overlayNames = new String[mOverlays.size()];
        mOverlays.toArray(overlayNames);
        
        KMLSelectorBuilder builder = new KMLSelectorBuilder(this, overlayNames);
        builder.show();
    }
    

    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        if (position == 0) {
//          Ignore when title of the spinner is selected
            return true;
        }
        
        mSelectedMapPosition = position;
        
        MessageToWebview message = MessageToWebview.ChangeBaseMap;
        setLayers(message);
        
        return true;        
    }
        
    private class KMLSelectorBuilder extends AlertDialog.Builder {
        private HashSet<Integer> mSelectedItems;
        private String[] mOverlayNames;
        
        public KMLSelectorBuilder(Context context, String[] overlayNames) {
            super(context);
        
            mSelectedItems = new HashSet<Integer>();
            mOverlayNames = Arrays.copyOf(overlayNames, overlayNames.length);
            
            this.setTitle(R.string.map_select_overlays);
            this.setMultiChoiceItems(mOverlayNames, null, mOverlaySelector);
            this.setPositiveButton(R.string.map_display_overlays, mPositiveListener);
            this.setNegativeButton(R.string.map_display_overlays_cancel, mNegativeListener);
            
        }
        
        private DialogInterface.OnMultiChoiceClickListener mOverlaySelector = 
                new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    mSelectedItems.add(which);
                } else if (mSelectedItems.contains(mOverlayNames[which])) {
                    mSelectedItems.remove(which);
                }
            }
        };
        
        private DialogInterface.OnClickListener mPositiveListener = 
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//              Do nothing when nothing is selected
                if (mSelectedItems.size() == 0) {
                    mNegativeListener.onClick(dialog, which);
                    return;
                }
                
                List<String> selectedItemNames = new ArrayList<String>();
                for (Integer item : mSelectedItems) {
                    selectedItemNames.add(mOverlayNames[item.intValue()]);
                }

//              Set states of the button
                mIsDisplayKML = true;
                mKmlButton.setBackgroundResource(R.drawable.button_kml_toggle);
                
                displayOverlays(selectedItemNames);
            }
        };
        
        private DialogInterface.OnClickListener mNegativeListener = 
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//              Set states of the button
                mIsDisplayKML = false;
                mKmlButton.setBackgroundResource(R.drawable.button_kml);
            }
        };
        
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
		HideKML("HideKML"),
		PanToCurrentLocation("PanToCurrentLocation"), 
		ChangeBaseMap("ChangeBaseMap");
		
		private final String message;
		MessageToWebview(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
}