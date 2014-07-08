package edu.cmu.sv.trailscribe.model;


public class Sample {
	private long privateKey;
	private long userId;
	private double x;
	private double y;
	private double z;
	private String name;
	private String timestamp;
	private String description;
	private String misc;

	public Sample(
			long privateKey, long userId, double x, double y, double z, 
			String name, String timeStamp, String description, String misc) {
		this.privateKey = privateKey;
		this.userId = userId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
		this.timestamp = timeStamp;
		this.description = description;
		this.misc = misc;
	}
	
	public long getPrivateKey() {
		return this.privateKey;
	}
	
	public void setPrivateKey(long privateKey) {
		this.privateKey = privateKey;
	}
	
	public long getUserId() {
		return this.userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getX() {
		return this.x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTimeStamp() {
		return this.timestamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timestamp = timeStamp;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMisc() {
		return this.misc;
	}
	
	public void setMisc(String misc) {
		this.misc = misc;
	}
	
}
