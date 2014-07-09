package edu.cmu.sv.trailscribe.model;


public class Map {

	private long mId;
	private String mName;
	private String mDescription;
	private String mProjection;
	private int mMinZoomLevel;
	private int mMaxZoomLevel;
	private double mMinX;
	private double mMaxY; 
	private double mMaxX;
	private double mMinY;
	private String mFilename; 
	private String mLastModified; 
			
	public Map(
			long id, String name, String description, String projection, 
			int minZoomLevel, int maxZoomLevel, 
			double minX, double minY, double maxX, double maxY, 
			String filename, String lastModified) {
		mId = id;
		mName = name;
		mDescription = description;
		mProjection = projection;
		mMinZoomLevel = minZoomLevel;
		mMaxZoomLevel = maxZoomLevel;
		mMinX = minX;
		mMinY = minY;
		mMaxX = maxX;
		mMinY = minY;
		mFilename = filename;
		mLastModified = lastModified;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
	
	public long getId() {
		return mId;
	}

	public void setId(long mId) {
		this.mId = mId;
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
		return mFilename;
	}

	public void setFilename(String mFilename) {
		this.mFilename = mFilename;
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
}
