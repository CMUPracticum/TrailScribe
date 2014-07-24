package edu.cmu.sv.trailscribe.model.data;

public class Sample implements DataModel {
	private long mId;
	private long mUserId;
	private long mMapId;
	private long mExpeditionId;
	private double x;
	private double y;
	private double z;
	private String mName;
	private String mDescription;
	private String mTime;
	private String mCustomField;
	private String mLastModified;

	public Sample(
			long id, String name, String description, String time, 
			double x, double y, double z,
			String customField, String lastModified, 
			long userId, long mapId, long expeditionId) {
		this.mId = id;
		this.mName = name;
		this.mDescription = description;
		this.mTime = time;
		this.x = x;
		this.y = y;
		this.z = z;
		this.mCustomField = customField;
		this.mLastModified = lastModified;
		this.mUserId = userId;
		this.mMapId = mapId;
		this.mExpeditionId = expeditionId;
	}
	
	public long getId() {
		return this.mId;
	}
	
	public void setId(long id) {
		this.mId = id;
	}
	
	public String getName() {
		return this.mName;
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getDescription() {
		return this.mDescription;
	}
	
	public void setDescription(String description) {
		this.mDescription = description;
	}
	
	public String getTime() {
		return this.mTime;
	}
	
	public void setTime(String time) {
		this.mTime = time;
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
		return this.mCustomField;
	}
	
	public void setCustomField(String customField) {
		this.mCustomField = customField;
	}
	
	public String getLastModified() {
		return this.mLastModified;
	}
	
	public void setLastModified(String lastModified) {
		this.mLastModified = lastModified;
	}
	
	public long getUserId() {
		return this.mUserId;
	}
	
	public void setUserId(long userId) {
		this.mUserId = userId;
	}
	
	public long getMapId() {
		return this.mMapId;
	}
	
	public void setMapId(long mapId) {
		this.mMapId = mapId;
	}
	
	public long getExpeditionId() {
		return this.mExpeditionId;
	}
	
	public void setExpeditionId(long expeditionId) {
		this.mExpeditionId = expeditionId;
	}


    @Override
    public String toString() {
        return this.getName();
    }
	
	@Override
	public String toJson() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("'id':'").append(this.getId()).append("', ");
        buffer.append("'userId':'").append(this.getUserId()).append("', ");
        buffer.append("'mapId':'").append(this.getMapId()).append("', ");
        buffer.append("'expeditionId':'").append(this.getExpeditionId()).append("', ");
        buffer.append("'x':'").append(this.getX()).append("', ");
        buffer.append("'y':'").append(this.getY()).append("', ");
        buffer.append("'z':'").append(this.getZ()).append("', ");
        buffer.append("'name':'").append(this.getName()).append("', ");
        buffer.append("'description':'").append(this.getDescription()).append("', ");
        buffer.append("'time':'").append(this.getTime()).append("', ");
        buffer.append("'customField':'").append(this.getCustomField()).append("', ");
        buffer.append("'lastModified':'").append(this.getLastModified()).append("'");
        
        return buffer.toString();
	}
}
