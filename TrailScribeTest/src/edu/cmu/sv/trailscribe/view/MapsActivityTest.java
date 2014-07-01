package edu.cmu.sv.trailscribe;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import edu.cmu.sv.trailscribe.controller.MapsController;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private static final String WEBVIEW_URL = "file:///android_asset/map.html";

    /*private MapsActivity tMapsActivity;
    private WebView tWebView;
    private WebSettings tWebSettings;
    private MapsController tMapsController;*/

    public MapsActivityTest() {
        super("edu.cmu.sv.trailscribe", MapsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // We'd run tests here, but WebView is not thread safe
        // => not testable
        /*
        tMapsActivity = getActivity();
        tWebView = (WebView) tMapsActivity
            .findViewById(R.id.mapsview_webview);
        tWebSettings = tWebView.getSettings();
        */
    }

    /*
    public void testPreconditions() {
        assertNotNull("tMapsActivity is null", tMapsActivity);
        assertNotNull("tWebView is null", tWebView);
        assertNotNull("tWebSettings is null", tWebSettings);
    }

    public void testWebView_url() {
        final String expected = WEBVIEW_URL;
        final String actual = tWebView.getUrl();
        assertEquals(expected, actual);
    }
    */
}
