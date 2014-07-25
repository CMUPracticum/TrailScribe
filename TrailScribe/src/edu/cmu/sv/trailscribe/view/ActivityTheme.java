package edu.cmu.sv.trailscribe.view;

public class ActivityTheme {
	private String mActivityName;
	private String mActivityDescription;

//	Color of the action bar in the activity.
//	It should be defined in /res/values/colors.xml and loaded from getResources()
	private int mActivityColor;
	
	ActivityTheme(String activityName, String activityDescription, int activityColor) {
		this.mActivityName = activityName;
		this.mActivityDescription = activityDescription;
		this.mActivityColor = activityColor;
	}
	
	public String getActivityName() {
		return this.mActivityName;
	}
	
	public String getActivityDescription() {
		return this.mActivityDescription;
	}
	
	public int getActivityColor() {
		return this.mActivityColor;
	}
	
	public void updateActivityDescription() {
//	    TODO
	}

}
