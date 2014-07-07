package edu.cmu.sv.trailscribe;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import edu.cmu.sv.trailscribe.controller.MapsController;
import android.webkit.WebSettings;
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

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private final String WEBVIEW_URL = "file:///android_asset/map.html";
    private final boolean JAVASCRIPT_ENABLED = true;
    private final String LOG_TAG = "MapsActivityTest";
    private final long PAGE_LOAD_TIMEOUT = 50L;
    private final String SAMPLE_POINT_LOCATIONS = "\"[{\\\"x\\\":-13585932.705423407,\\\"y\\\":4497531.859545275},{\\\"x\\\":-13589272.29014674,\\\"y\\\":4497531.859545275},{\\\"x\\\":-13592611.874870075,\\\"y\\\":4494729.00634739}]\"";

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
        tMapsActivity = getActivity();
        tWebView = (WebView) tMapsActivity
            .findViewById(R.id.maps_webview);
        tSamplesButton = (Button) tMapsActivity
            .findViewById(R.id.maps_samples);
        tCurrentLocationButton = (Button) tMapsActivity
            .findViewById(R.id.maps_current_location);
        tPositionHistoryButton = (Button) tMapsActivity
            .findViewById(R.id.maps_position_history);
    }


    public void testPreconditions() throws InterruptedException {
        assertNotNull("Maps activity not found", tMapsActivity);
        assertNotNull("WebView not found", tWebView);
        assertNotNull("Samples button not found", tSamplesButton);
        assertNotNull("Current Location button not found", tCurrentLocationButton);
        assertNotNull("Position History button not found", tPositionHistoryButton);

        // set a web chrome client with more debug info
        // This should technically be in setUp, but without the above assertions
        // the error messages generated aren't as clear
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
        final CountDownLatch webviewDone = new CountDownLatch(1);
        tMapsActivity.runOnUiThread(new Runnable() {
                public void run() {
                    tWebView.setWebViewClient(new WebViewClient() {
                            public void onPageFinished(WebView view, String url) {
                                webviewDone.countDown();
                            }
                        });
                }
            });
        assertTrue("Page timed out while loading", webviewDone.await(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS));
        Log.d(LOG_TAG, "Page done loading");
        Log.d(LOG_TAG, "Reading js test from assets");
        StringBuilder buf = new StringBuilder();
        InputStream file = getInstrumentation().getContext().getAssets().open("js/samplesButtonTest.js");
        BufferedReader in =
            new BufferedReader(new InputStreamReader(file, "UTF-8"));
        String str;
        while ((str = in.readLine()) != null) {
            buf.append(str + "\n");
        }
        in.close();

        final String js = buf.toString();
        final CountDownLatch done = new CountDownLatch(1);
        final String expected = SAMPLE_POINT_LOCATIONS;
        final ResultContainer<String> container = new ResultContainer<String>();

        Log.d(LOG_TAG, "Performing button click");
        this.runTestOnUiThread(new Runnable() {
                public void run() {
                    tSamplesButton.performClick();
                }
            });

        Log.d(LOG_TAG, "Running JS");
        this.runTestOnUiThread(new Runnable() {
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
        final String actual = container.getResult();
        assertEquals("Clicking samples button did not display the correct layer",
                     expected, actual);
    }
}

class ResultContainer<T> {
    private T result;

    public ResultContainer() {
        this.result = null;
    }

    public ResultContainer(T initial) {
        this.result = initial;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T value) {
        this.result = value;
    }
}
