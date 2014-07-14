package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.cmu.sv.trailscribe.view.SynchronizationCenterActivity;
import edu.cmu.sv.trailscribe.R;
import android.widget.ListView;

public class SynchronizationCenterActivityTest extends ActivityInstrumentationTestCase2<SynchronizationCenterActivity> {
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

}
