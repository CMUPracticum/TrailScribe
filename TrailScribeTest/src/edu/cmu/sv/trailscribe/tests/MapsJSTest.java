package edu.cmu.sv.trailscribe.tests;

import android.test.ActivityUnitTestCase;

public class MapsJSTest extends ActivityUnitTestCase<MapsJSActivity> {
    private MapsJSActivity activity;

    public MapsJSTest(Class<MapsJSActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() {
        activity = (MapsJSActivity) getActivity();
    }

    public void testPreconditions() {
        assertNotNull("Test activity not found", activity);
    }

    public void testJS_activityStarted() {
        // bogus test for now
        assertTrue(true);
    }
}
