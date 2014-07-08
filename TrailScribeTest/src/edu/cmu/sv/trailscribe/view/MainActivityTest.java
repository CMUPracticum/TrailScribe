package edu.cmu.sv.trailscribe;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MainActivity;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import android.widget.GridView;
import android.app.Instrumentation.ActivityMonitor;
import android.app.Instrumentation;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class edu.cmu.sv.trailscribe.view.MainActivityTest \
 * edu.cmu.sv.trailscribe.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private static final int GRIDVIEW_ELEMENTS = 4;
    private static final String LOG_TAG = "MainActivityTest";
    private static final long IDLE_SYNC_TIMEOUT = 10L;

    private MainActivity tMainActivity;
    private GridView tGridView;

    public MainActivityTest() {
        super("edu.cmu.sv.trailscribe", MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tMainActivity = getActivity();
        tGridView = (GridView) tMainActivity
            .findViewById(R.id.main_buttongrid);
    }

    public void testPreconditions() {
        assertNotNull("tMainActivity is null", tMainActivity);
        assertNotNull("tGridView is null", tGridView);
    }

    public void testGridView_viewCount() {
        final int expected = GRIDVIEW_ELEMENTS;
        final int actual = tGridView.getAdapter().getCount();
        assertEquals(expected, actual);
    }

    public void testButton_mapsActivity() throws InterruptedException, Throwable {
        final ActivityMonitor monitor = getInstrumentation().addMonitor(MapsActivity.class.getName(), null, false);
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tGridView.performItemClick(tGridView.getAdapter().getView(1, null, null),
                                               1, tGridView.getAdapter().getItemId(1));
                }
            });
        final MapsActivity activity = (MapsActivity) monitor.waitForActivityWithTimeout(1000L);
        assertNotNull("Maps activity not started", activity);
        activity.finish();
    }
}
