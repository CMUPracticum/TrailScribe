package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.SynchronizationCenterActivity;
import edu.cmu.sv.trailscribe.R;
import android.widget.ListView;
import java.util.concurrent.TimeUnit;
import android.database.DataSetObserver;
import java.util.concurrent.CountDownLatch;
import android.widget.ArrayAdapter;
import edu.cmu.sv.trailscribe.model.Map;
import android.util.Log;

public class SynchronizationCenterActivityTest extends ActivityInstrumentationTestCase2<SynchronizationCenterActivity> {
    private static final String LOG_TAG = "SynchronizationCenterActivityTest";
    private static final long MAP_LIST_FETCH_TIMEOUT = 50L;
    private SynchronizationCenterActivity syncActivity;

    public SynchronizationCenterActivityTest() {
        super("edu.cmu.sv.trailscribe", SynchronizationCenterActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        syncActivity = getActivity();
    }

    public void testPreconditions() {
        assertNotNull("Sync activity not found", syncActivity);
    }

    public void testMapList_fetchMaps() {
        if (syncActivity.areMapsFetched()) {
            Log.d(LOG_TAG, "Map data has already been fetched");
            return;
        }
        final CountDownLatch done = new CountDownLatch(1);
        syncActivity.setMapsFetchedCallback(new Runnable() {
                @Override
                public void run() {
                    done.countDown();
                }
            });
        try {
            final boolean result = done.await(MAP_LIST_FETCH_TIMEOUT, TimeUnit.SECONDS);
            assertTrue("Fetching map list took loo long", result);
        } catch (InterruptedException e) {
            fail("Map fetching interrupted");
        }
    }
}
