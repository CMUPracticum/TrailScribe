package edu.cmu.sv.trailscribe.model.data;

public class Location implements DataModel {
	private long mId;
	private String mTime;	
	private double x;
	private double y;
	private double z;
	private long mUserId;
	private long mMapId;
	private long mExpeditionId;
	
	public Location(
			long id, String time, double x, double y, double z,
			long userId, long mapId, long expeditionId) {
		this.mId = id;
		this.mTime = time;
		this.x = x;
		this.y = y;
		this.z = z;
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
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getX()).append(",").append(this.getY());
    
        return buffer.toString();
    }

    @Override
    public String toJson() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("'id':'").append(this.getId()).append("', ");
        buffer.append("'time':'").append(this.getTime()).append("', ");
        buffer.append("'x':'").append(this.getX()).append("', ");
        buffer.append("'y':'").append(this.getY()).append("', ");
        buffer.append("'z':'").append(this.getZ()).append("', ");
        buffer.append("'userId':'").append(this.getUserId()).append("', ");
        buffer.append("'mapId':'").append(this.getMapId()).append("', ");
        buffer.append("'expeditionId':'").append(this.getExpeditionId()).append("', ");
    
        return buffer.toString();
    }
}