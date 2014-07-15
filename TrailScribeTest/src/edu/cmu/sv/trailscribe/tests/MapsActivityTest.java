package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import android.webkit.WebView;
import android.test.UiThreadTest;
import android.widget.Button;
import android.util.Log;
import com.google.android.gms.location.LocationClient;
import android.os.SystemClock;
import edu.cmu.sv.trailscribe.R;
import android.test.RenamingDelegatingContext;
import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.SampleDataSource;
import edu.cmu.sv.trailscribe.model.Sample;
import android.location.Location;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private static final String WEBVIEW_URL = "file:///android_asset/map.html";
    private static final boolean JAVASCRIPT_ENABLED = true;
    private static final String LOG_TAG = "MapsActivityTest";
    private static final String SAMPLES_RESULT = "{\"points\":[{\"y\":\"37.418\",\"x\":\"-122.04451\"},{\"y\":\"1.0\",\"x\":\"1.0\"},{\"y\":\"2.0\",\"x\":\"2.0\"},{\"y\":\"37.418\",\"x\":\"-122.04451\"}]}";
    private static final String CURRENT_LOCATION_RESULT = "{\"points\":[{\"y\":\"3.0\",\"x\":\"3.0\"}]}";
    private static final String POSITION_HISTORY_RESULT = "{\"points\":[{\"y\":\"1.0\",\"x\":\"1.0\"},{\"y\":\"2.0\",\"x\":\"2.0\"}]}";
    private static final String LOC_PROVIDER = "flp";
    private static final String TEST_FILE_PREFIX = "test_";

    private LocationClient tLocationClient;

    private MapsActivity tMapsActivity;
    private WebView tWebView;

    private DBHelper tHelper;

    private Button tSamplesButton;
    private Button tCurrentLocationButton;
    private Button tPositionHistoryButton;

    public MapsActivityTest() {
        super("edu.cmu.sv.trailscribe", MapsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // find all UI elements
        tMapsActivity = getActivity();
        Log.d(LOG_TAG, "Found main activity");
        tWebView = (WebView) tMapsActivity
            .findViewById(R.id.maps_webview);
        tSamplesButton = (Button) tMapsActivity
            .findViewById(R.id.maps_samples);
        tCurrentLocationButton = (Button) tMapsActivity
            .findViewById(R.id.maps_current_location);
        tPositionHistoryButton = (Button) tMapsActivity
            .findViewById(R.id.maps_position_history);

        // setup database
        final RenamingDelegatingContext context = new RenamingDelegatingContext(getInstrumentation().getTargetContext().getApplicationContext(), TEST_FILE_PREFIX);
        tHelper = new DBHelper(context);
        tMapsActivity.setDBHelper(tHelper);

    }

    private Location createLocation(final double lat, final double lng, final float accuracy) {
        // Create new location
        final Location newLocation = new Location(LOC_PROVIDER);
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setAccuracy(accuracy);
        newLocation.setTime(System.currentTimeMillis());
        newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        return newLocation;
    }

    public void testPreconditions() throws InterruptedException {
        assertNotNull("Maps activity not found", tMapsActivity);
        assertNotNull("WebView not found", tWebView);
        assertNotNull("Samples button not found", tSamplesButton);
        assertNotNull("Current Location button not found", tCurrentLocationButton);
        assertNotNull("Position History button not found", tPositionHistoryButton);
    }

    @UiThreadTest
    public void testWebView_url() {
        assertEquals("Webview at wrong URL", WEBVIEW_URL, tWebView.getUrl());
    }

    @UiThreadTest
    public void testWebView_javascript() {
        assertEquals("Javascript not enabled", JAVASCRIPT_ENABLED,
                     tWebView.getSettings().getJavaScriptEnabled());
    }

    public void testInterface_positionHistory() throws InterruptedException {
        // hijack the onlocation changed event
        tMapsActivity.onLocationChanged(createLocation(1.0, 1.0, 3.0f));
        tMapsActivity.onLocationChanged(createLocation(2.0, 2.0, 3.0f));

        final String actual = tMapsActivity.getPositionHistory();
        assertEquals("Position history didn't respond to location change correctly",
                     POSITION_HISTORY_RESULT, actual);
    }

    public void testInterface_currentLocation() throws Exception {
        // hijack the onlocationchanged event
        tMapsActivity.onLocationChanged(createLocation(3.0, 3.0, 3.0f));

        final String actual = tMapsActivity.getCurrentLocation();
        assertEquals("Current location returned incorrect result",
                     CURRENT_LOCATION_RESULT, actual);
    }

    public void testInterface_samplePoints() {
        final SampleDataSource dataSource = new SampleDataSource(tHelper);

        // insert mock sample points
        final Sample sample1 = new Sample(1, "test_1", "Test sample 1", "5:43",
                                          1.0, 1.0, 0,
                                          "", "5:44",
                                          0, 0, 0);
        final Sample sample2 = new Sample(1, "test_2", "Test sample 2", "5:46",
                                          2.0, 2.0, 0,
                                          "", "5:47",
                                          0, 0, 0);
        dataSource.add(sample1);
        dataSource.add(sample2);

        final String actual = tMapsActivity.getSamples();
        assertEquals("Samples returned incorrect result",
                     SAMPLES_RESULT, actual);
    }

}
