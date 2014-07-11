package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import android.webkit.WebView;
import android.test.UiThreadTest;
import java.util.concurrent.CountDownLatch;
import android.widget.Button;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import android.os.Bundle;
import android.os.SystemClock;
import edu.cmu.sv.trailscribe.R;
import android.test.RenamingDelegatingContext;
import edu.cmu.sv.trailscribe.dao.DBHelper;
import edu.cmu.sv.trailscribe.dao.LocationDataSource;
import edu.cmu.sv.trailscribe.dao.SampleDataSource;
import edu.cmu.sv.trailscribe.model.Sample;
import java.util.List;
import android.location.Location;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private static final String WEBVIEW_URL = "file:///android_asset/map.html";
    private static final boolean JAVASCRIPT_ENABLED = true;
    private static final String LOG_TAG = "MapsActivityTest";
    private static final long GENERAL_AWAIT_TIMEOUT = 10L;
    private static final long IDLE_SYNC_TIMEOUT = 10L;
    private static final String SAMPLES_RESULT = "{\"points\":[{\"y\":\"37.418\",\"x\":\"-122.04451\"},{\"y\":\"1.0\",\"x\":\"1.0\"},{\"y\":\"2.0\",\"x\":\"2.0\"},{\"y\":\"37.418\",\"x\":\"-122.04451\"}]}";
    private static final String CURRENT_LOCATION_RESULT = "{\"points\":[{\"y\":\"1.0\",\"x\":\"1.0\"}]}";
    private static final String POSITION_HISTORY_RESULT = "{\"points\":[{\"y\":\"2\",\"x\":\"2\"},{\"y\":\"3\",\"x\":\"3\"}]}";
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

        // setup location
        tLocationClient = new LocationClient(tMapsActivity, tMapsActivity, tMapsActivity);
        tMapsActivity.setLocationClient(tLocationClient);
        final CountDownLatch locationDone = new CountDownLatch(1);
        final ConnectionCallbacks listener = new ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    locationDone.countDown();
                }

                @Override
                public void onDisconnected() {
                    // nothing
                }
            };
        tLocationClient.registerConnectionCallbacks(listener);
        assertTrue(locationDone.await(GENERAL_AWAIT_TIMEOUT, TimeUnit.SECONDS));
        tLocationClient.setMockMode(true);
        tLocationClient.unregisterConnectionCallbacks(listener);

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

    private void waitForIdleTimeout() throws InterruptedException {
        final CountDownLatch done = new CountDownLatch(1);
        getInstrumentation().waitForIdle(new Runnable() {
                public void run() {
                    done.countDown();
                }
            });
        assertTrue("Idle sync took too long", done.await(IDLE_SYNC_TIMEOUT, TimeUnit.SECONDS));
    }

    public void testPreconditions() throws InterruptedException {
        assertNotNull("Maps activity not found", tMapsActivity);
        assertNotNull("WebView not found", tWebView);
        assertNotNull("Samples button not found", tSamplesButton);
        assertNotNull("Current Location button not found", tCurrentLocationButton);
        assertNotNull("Position History button not found", tPositionHistoryButton);
        assertTrue("Location is not connected", tLocationClient.isConnected());
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
        // insert mock position history points by changing location
        tLocationClient.setMockLocation(createLocation(1.0, 1.0, 1.0f));
        Thread.sleep(1000);
        tLocationClient.setMockLocation(createLocation(2.0, 2.0, 1.0f));
        Thread.sleep(1000);
        tLocationClient.setMockLocation(createLocation(3.0, 3.0, 1.0f));

        final String actual = tMapsActivity.getPositionHistory();
        assertEquals("Position history didn't respond to location change correctly",
                     POSITION_HISTORY_RESULT, actual);

        final LocationDataSource dataSource = new LocationDataSource(tHelper);

        // empty location list
        final List<edu.cmu.sv.trailscribe.model.Location> positionHistory = dataSource.getAll();
        edu.cmu.sv.trailscribe.model.Location location;
        for (int i = 0; i < positionHistory.size(); i++) {
            location = positionHistory.get(i);
            dataSource.delete(location);
        }

    }

    public void testInterface_currentLocation() throws InterruptedException {
        // create and set mock location
        tLocationClient.setMockLocation(createLocation(1.0, 1.0, 1.0f));

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
