package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import android.webkit.WebView;
import android.test.UiThreadTest;
import android.widget.Button;
import android.util.Log;
import android.os.SystemClock;
import edu.cmu.sv.trailscribe.R;
import android.test.RenamingDelegatingContext;
import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.SampleDataSource;
import edu.cmu.sv.trailscribe.model.data.Sample;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;
import android.location.Location;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private static final long GENERAL_AWAIT_TIMEOUT = 10L;
    private static final String WEBVIEW_URL = "file:///android_asset/map.html";
    private static final boolean JAVASCRIPT_ENABLED = true;
    private static final String LOG_TAG = "MapsActivityTest";
    private static final String LOC_PROVIDER = "flp";
    private static final String TEST_FILE_PREFIX = "test_";
    private static final int POSITION_HISTORY_MINIMUM_LENGTH = 13;

    private TrailScribeApplication tApplication;
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
        tApplication = (TrailScribeApplication) tMapsActivity.getApplication();
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
        tApplication.setDBHelper(tHelper);
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

    public void testPreconditions() {
        assertNotNull("Application not found", tApplication);
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

    public void testInterface_positionHistory() {
        // hijack the onlocation changed event
        tApplication.onLocationChanged(createLocation(1.0, 1.0, 3.0f));
        tApplication.onLocationChanged(createLocation(2.0, 2.0, 3.0f));

        final String result = tMapsActivity.getPositionHistory();
        final String regex = getInstrumentation().getContext()
            .getString(edu.cmu.sv.trailscribe.tests.R.string.position_history_regex);
        assertTrue("Position history returned incorrect result", result.matches(regex));
    }

    public void testInterface_currentLocation() {
        // hijack the onlocationchanged event
        tApplication.onLocationChanged(createLocation(3.0, 3.0, 3.0f));
        // the same needs to be called in mapsactivity to cause it to update
        final CountDownLatch done = new CountDownLatch(1);
        tMapsActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tMapsActivity.onLocationChanged(null);
                    done.countDown();
                }
            });

        try {
            assertTrue("Pushing current location change through MapsActivity took too long",
                       done.await(GENERAL_AWAIT_TIMEOUT, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail("Interrupted while waiting for MapsActivity to update position");
        }
        try {
            final String actual = tMapsActivity.getCurrentLocation();
            final String expected = getInstrumentation().getContext()
                .getString(edu.cmu.sv.trailscribe.tests.R.string.current_location_result);
            assertEquals("Current location returned incorrect result",
                         expected, actual);
        } catch (Exception e) {
            fail("Failed to get current location");
        }
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
        final String expected = getInstrumentation().getContext()
            .getString(edu.cmu.sv.trailscribe.tests.R.string.samples_result);
        assertEquals("Samples returned incorrect result",
                     expected, actual);
    }

}
