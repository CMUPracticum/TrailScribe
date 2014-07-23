package edu.cmu.sv.trailscribe.model;

public class Location {
	private long id;
	private String time;	
	private double x;
	private double y;
	private double z;
	private long userId;
	private long mapId;
	private long expeditionId;
	
	public Location(
			long id, String time, double x, double y, double z,
			long userId, long mapId, long expeditionId) {
		this.id = id;
		this.time = time;
		this.x = x;
		this.y = y;
		this.z = z;
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
	
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("'id':'").append(this.getId()).append("', ");
        buffer.append("'time':'").append(this.getTime()).append("', ");
        buffer.append("'x':'").append(this.getX()).append("', ");
        buffer.append("'y':'").append(this.getY()).append("', ");
        buffer.append("'z':'").append(this.getZ()).append("', ");
        buffer.append("'userId':'").append(this.getUserId()).append("', ");
        buffer.append("'mapId':'").append(this.getMapId()).append("', ");
        buffer.append("'expeditionId':'").append(this.getExpeditionId()).append("', ");
        buffer.append("}");
    
    return buffer.toString();
    }
}