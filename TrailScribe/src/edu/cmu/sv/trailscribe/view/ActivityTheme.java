package edu.cmu.sv.trailscribe.view;

public class ActivityTheme {
	private String activityName;
	private String activityDescription;
	private int activityColor;
	
	ActivityTheme(String activityName, String activityDescription, int activityColor) {
		this.activityName = activityName;
		this.activityDescription = activityDescription;
		this.activityColor = activityColor;
	}
	
	public String getActivityName() {
		return this.activityName;
	}
	
	public String getActivityDescription() {
		return this.activityDescription;
	}
	
	public int getActivityColor() {
		return this.activityColor;
	}
	
	public void updateActivityDescription() {
//			TODO
	}

}
