package edu.cmu.sv.trailscribe.model.data;

public class Map extends SyncItem {
	private String mDescription;
	private String mProjection;
	private int mMinZoomLevel;
	private int mMaxZoomLevel;
	private double mMinX;
	private double mMaxY; 
	private double mMaxX;
	private double mMinY;
	private String mLastModified;
	private String mType;
	
	public Map(){}
	public Map(
			long id, String name, String description, String projection, 
			int minZoomLevel, int maxZoomLevel, 
			double minX, double minY, double maxX, double maxY, 
			String filename, String lastModified, String type) {
		super.setId(id);
		super.setName(name);
		super.setFilename(filename);
		mDescription = description;
		mProjection = projection;
		mMinZoomLevel = minZoomLevel;
		mMaxZoomLevel = maxZoomLevel;
		mMinX = minX;
		mMinY = minY;
		mMaxX = maxX;
		mMaxY = maxY;
		mLastModified = lastModified;
		mType = type;
	}

	public int getMaxZoomLevel() {
		return mMaxZoomLevel;
	}

	public void setMaxZoomLevel(int mMaxZoomLevel) {
		this.mMaxZoomLevel = mMaxZoomLevel;
	}

	public int getMinZoomLevel() {
		return mMinZoomLevel;
	}

	public void setMinZoomLevel(int mMinZoomLevel) {
		this.mMinZoomLevel = mMinZoomLevel;
	}

	public double getMinX() {
		return mMinX;
	}

	public void setMinX(double mMinX) {
		this.mMinX = mMinX;
	}

	public double getMaxY() {
		return mMaxY;
	}

	public void setMaxY(double mMaxY) {
		this.mMaxY = mMaxY;
	}

	public double getMaxX() {
		return mMaxX;
	}

	public void setMaxX(double mMaxX) {
		this.mMaxX = mMaxX;
	}

	public double getMinY() {
		return mMinY;
	}

	public void setMinY(double mMinY) {
		this.mMinY = mMinY;
	}

	public String getFilename() {
		return super.getFilename();
	}

	public void setFilename(String mFilename) {
		super.setFilename(mFilename);
	}

	public String getProjection() {
		return mProjection;
	}

	public void setProjection(String mProjection) {
		this.mProjection = mProjection;
	}

	public String getLastModified() {
		return mLastModified;
	}

	public void setLastModified(String mLastModified) {
		this.mLastModified = mLastModified;
	}
	
	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	
	public long getId() {
		return super.getId();
	}
	
	public void setId(long id){
		super.setId(id);
	}

	public void setName(String name) {
		super.setName(name);
	}
	
	public String getName(){
		return super.getName();
	}
	
	public String getType() {
		return mType;
	}
	
	public void setType(String mType) {
		this.mType = mType;
	}
	
    @Override
    public String toString(){
        return this.getName();
    }
    
    @Override
    public String toJson() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("'projection':'").append(this.getProjection()).append("', ");
        buffer.append("'name':'").append(this.getName()).append("', ");
        buffer.append("'minZoomLevel':'").append(this.getMinZoomLevel()).append("', ");
        buffer.append("'maxZoomLevel':'").append(this.getMaxZoomLevel()).append("', ");
        buffer.append("'minX':'").append(this.getMinX()).append("', ");
        buffer.append("'maxX':'").append(this.getMaxX()).append("', ");
        buffer.append("'minY':'").append(this.getMinY()).append("', ");
        buffer.append("'maxY':'").append(this.getMaxY()).append("'");
        buffer.append("'type':'").append(this.getType()).append("'");
    
        return buffer.toString();
    }
}
