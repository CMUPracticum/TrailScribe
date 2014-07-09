package edu.cmu.sv.trailscribe.model;


public class Sample {
	private long id;
	private long userId;
	private long mapId;
	private long expeditionId;
	private double x;
	private double y;
	private double z;
	private String name;
	private String description;
	private String time;
	private String customField;
	private String lastModified;

	public Sample(
			long id, String name, String description, String time, 
			double x, double y, double z,
			String customField, String lastModified, 
			long userId, long mapId, long expeditionId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.time = time;
		this.x = x;
		this.y = y;
		this.z = z;
		this.customField = customField;
		this.lastModified = lastModified;
		this.userId = userId;
		this.mapId = mapId;
		this.expeditionId = expeditionId;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public void setTime(String time) {
		this.time = time;
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
	
	public String getCustomField() {
		return this.customField;
	}
	
	public void setCustomField(String customField) {
		this.customField = customField;
	}
	
	public String getLastModified() {
		return this.lastModified;
	}
	
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	
	public long getUserId() {
		return this.userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public long getMapId() {
		return this.mapId;
	}
	
	public void setMapId(long mapId) {
		this.mapId = mapId;
	}
	
	public long getExpeditionId() {
		return this.expeditionId;
	}
	
	public void setExpeditionId(long expeditionId) {
		this.expeditionId = expeditionId;
	}
}
