package edu.cmu.sv.trailscribe;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import edu.cmu.sv.trailscribe.controller.MapsController;
import android.webkit.WebView;
import android.test.UiThreadTest;
import android.webkit.ValueCallback;
import java.util.concurrent.CountDownLatch;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import android.widget.Button;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import java.util.concurrent.TimeUnit;
import android.webkit.WebViewClient;
import android.location.Location;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import android.os.Bundle;
import android.os.SystemClock;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private static final String WEBVIEW_URL = "file:///android_asset/map.html";
    private static final boolean JAVASCRIPT_ENABLED = true;
    private static final String LOG_TAG = "MapsActivityTest";
    private static final long PAGE_LOAD_TIMEOUT = 50L;
    private static final String SAMPLE_POINT_LOCATIONS = "\"[{\\\"x\\\":-13585932.705423407,\\\"y\\\":4497531.859545275},{\\\"x\\\":-13589272.29014674,\\\"y\\\":4497531.859545275},{\\\"x\\\":-13592611.874870075,\\\"y\\\":4494729.00634739}]\"";
    private static final String CURRENT_LOCATION_JSON = "\"[{\\\"x\\\":-13590658.885723868,\\\"y\\\":4491810.069464362}]\"";
    private static final String POSITION_HISTORY = "\"[{\\\"x\\\":-13586526.149628745,\\\"y\\\":4495410.731525376},{\\\"x\\\":-13586683.889347177,\\\"y\\\":4495850.197979055},{\\\"x\\\":-13586979.999192646,\\\"y\\\":4496576.422973805},{\\\"x\\\":-13586963.301269028,\\\"y\\\":4496872.561169761},{\\\"x\\\":-13586976.103010466,\\\"y\\\":4497270.041372993}]\"";
    private static final String LOC_PROVIDER = "flp";
    private static final double LOC_LAT = 37.377166;
    private static final double LOC_LNG = -122.086966;
    private static final float LOC_ACCURACY = 3.0f;

    private LocationClient tLocationClient;

    private MapsActivity tMapsActivity;
    private WebView tWebView;
    private MapsController tMapsController;

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
        tWebView = (WebView) tMapsActivity
            .findViewById(R.id.maps_webview);
        tSamplesButton = (Button) tMapsActivity
            .findViewById(R.id.maps_samples);
        tCurrentLocationButton = (Button) tMapsActivity
            .findViewById(R.id.maps_current_location);
        tPositionHistoryButton = (Button) tMapsActivity
            .findViewById(R.id.maps_position_history);

        // set a web chrome client with more debug info
        tMapsActivity.runOnUiThread(new Runnable() {
                public void run() {
                    tWebView.setWebChromeClient(new WebChromeClient() {
                            public boolean onConsoleMessage(ConsoleMessage cm) {
                                Log.d("MapsActivity", cm.message() + " -- From line "
                                      + cm.lineNumber() + " of "
                                      + cm.sourceId() );
                                return true;
                            }
                        });
                }
            });

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
        locationDone.await();
        tLocationClient.setMockMode(true);
        tLocationClient.unregisterConnectionCallbacks(listener);
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

    private boolean waitForWebView() {
        // wait for the webview to finish loading
        final CountDownLatch webviewDone = new CountDownLatch(1);
        tMapsActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if (tWebView.getProgress() == 100) {
                        webviewDone.countDown();
                    } else {
                        tWebView.setWebViewClient(new WebViewClient() {
                                public void onPageFinished(WebView view, String url) {
                                    webviewDone.countDown();
                                }
                            });
                    }
                }
            });
        try {
            final boolean result = webviewDone.await(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
            return result;
        } catch (InterruptedException e) {
            return false;
        }
    }

    private String readResource(final String fname) throws UnsupportedEncodingException, IOException {
        final StringBuilder buf = new StringBuilder();
        final InputStream file = getInstrumentation().getContext().getAssets().open(fname);
        final BufferedReader in = new BufferedReader(new InputStreamReader(file, "UTF-8"));
        String str;
        while ((str = in.readLine()) != null) {
            buf.append(str + "\n");
        }
        in.close();

        return buf.toString();
    }

    private void runJsOnUiThread(final ResultContainer container, final String js) throws Throwable {
        final CountDownLatch done = new CountDownLatch(1);
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tWebView.evaluateJavascript(js, new ValueCallback<String>() {
                            public void onReceiveValue(String value) {
                                Log.d(LOG_TAG, "Value received: " + value);
                                container.setResult(value);
                                done.countDown();
                            }
                        });
                    Log.d(LOG_TAG, "JS started");
                }
            });
        Log.d(LOG_TAG, "Waiting for result");
        done.await();
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
        final String expected = WEBVIEW_URL;
        final String actual = tWebView.getUrl();
        assertEquals("Webview at wrong URL", expected, actual);
    }

    @UiThreadTest
    public void testWebView_javascript() {
        final boolean expected = JAVASCRIPT_ENABLED;
        final boolean actual = tWebView.getSettings().getJavaScriptEnabled();
        assertEquals("Javascript not enabled", expected, actual);
    }

    public void testWebView_samplesButton() throws Exception, Throwable {
        // make sure the page has finished loading
        Log.d(LOG_TAG, "Waiting for page to load");
        assertTrue("Page timed out while loading", waitForWebView());
        Log.d(LOG_TAG, "Reading js test from assets");
        final String js = readResource("js/samplesButtonTest.js");
        final String expected = SAMPLE_POINT_LOCATIONS;
        final ResultContainer<String> container = new ResultContainer<String>();

        Log.d(LOG_TAG, "Performing button click");
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tSamplesButton.performClick();
                }
            });

        Log.d(LOG_TAG, "Running JS");
        runJsOnUiThread(container, js);
        final String actual = container.getResult();
        assertEquals("Clicking samples button did not display the correct layer",
                     expected, actual);
    }

    public void testWebView_currentLocation() throws Exception, Throwable {
        // create and set mock location
        final Location testLocation = createLocation(LOC_LAT, LOC_LNG, LOC_ACCURACY);
        Log.d(LOG_TAG, "Setting mock location");
        tLocationClient.setMockLocation(testLocation);
        // make sure the page has finished loading
        Log.d(LOG_TAG, "Waiting for page to load");
        assertTrue("Page timed out while loading", waitForWebView());
        Log.d(LOG_TAG, "Reading js test from assets");
        final String js = readResource("js/currentLocationTest.js");
        final String expected = CURRENT_LOCATION_JSON;
        final ResultContainer<String> container = new ResultContainer<String>();

        Log.d(LOG_TAG, "Performing button click");
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tCurrentLocationButton.performClick();
                }
            });

        Log.d(LOG_TAG, "Running JS");
        runJsOnUiThread(container, js);
        final String actual = container.getResult();
        assertEquals("Clicking current location button did not display the correct location",
                     expected, actual);
    }

    public void testWebView_positionHistory() throws Exception, Throwable {
        // make sure the page has finished loading
        Log.d(LOG_TAG, "Waiting for page to load");
        assertTrue("Page timed out while loading", waitForWebView());
        Log.d(LOG_TAG, "Reading js test from assets");
        final String js = readResource("js/positionHistoryTest.js");
        final String expected = POSITION_HISTORY;
        final ResultContainer<String> container = new ResultContainer<String>();

        Log.d(LOG_TAG, "Performing button click");
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tPositionHistoryButton.performClick();
                }
            });

        Log.d(LOG_TAG, "Running JS");
        runJsOnUiThread(container, js);
        final String actual = container.getResult();
        assertEquals("Clicking position history button did not display the correct layer",
                     expected, actual);
    }

}

class ResultContainer<T> {
    private T result;

    public ResultContainer() {
        result = null;
    }

    public ResultContainer(T initial) {
        result = initial;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T value) {
        result = value;
    }
}
