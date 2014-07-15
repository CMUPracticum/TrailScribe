package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.MainActivity;
import edu.cmu.sv.trailscribe.view.MapsActivity;
import edu.cmu.sv.trailscribe.view.SynchronizationCenterActivity;
import android.widget.GridView;
import android.app.Instrumentation.ActivityMonitor;
import android.app.Instrumentation;
import edu.cmu.sv.trailscribe.R;

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
    private static final long IDLE_SYNC_TIMEOUT = 10L; // seconds
    private static final long ACTIVITY_START_TIMEOUT = 1000L; // milliseconds
    private static final int MAPS_GRID_BUTTON = 1;
    private static final int SYNC_GRID_BUTTON = 2;

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

    public void testButton_mapsActivity() throws Throwable {
        final ActivityMonitor monitor = getInstrumentation().addMonitor(MapsActivity.class.getName(), null, false);
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tGridView.performItemClick(tGridView.getAdapter().getView(MAPS_GRID_BUTTON, null, null),
                                               MAPS_GRID_BUTTON, tGridView.getAdapter().getItemId(MAPS_GRID_BUTTON));
                }
            });
        final MapsActivity activity = (MapsActivity) monitor.waitForActivityWithTimeout(ACTIVITY_START_TIMEOUT);
        assertNotNull("Maps activity not started", activity);
        activity.finish();
    }

    public void testButton_syncActivity() throws Throwable {
        final ActivityMonitor monitor = getInstrumentation().addMonitor(SynchronizationCenterActivity.class.getName(), null, false);
        runTestOnUiThread(new Runnable() {
                public void run() {
                    tGridView.performItemClick(tGridView.getAdapter().getView(SYNC_GRID_BUTTON, null, null),
                                               SYNC_GRID_BUTTON, tGridView.getAdapter().getItemId(SYNC_GRID_BUTTON));
                }
            });
        final SynchronizationCenterActivity activity = (SynchronizationCenterActivity) monitor.waitForActivityWithTimeout(ACTIVITY_START_TIMEOUT);
        assertNotNull("Synchronization center activity not started", activity);
        activity.finish();
    }
}
