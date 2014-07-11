package edu.cmu.sv.trailscribe.model;

import java.util.Date;

public class Map {
	private int mId;
	private String mName;
	private String mModel;
	private int mMaxZoomLevel;
	private int mMinZoomLevel;
	private double mMinX;
	private double mMaxY; 
	private double mMaxX;
	private double mMinY;
	private String mFilename; 
	private String mProjection;
	private String mLastModified; 

			
	//If last updated is used, then it is not needed.
	private int mVersion;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getVersion() {
		return mVersion;
	}

	public void setVersion(int mVersion) {
		this.mVersion = mVersion;
	}

	public String getModel() {
		return mModel;
	}

	public void setModel(String mModel) {
		this.mModel = mModel;
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
	
	@Override
	public String toString(){
		return this.getName();
	}
}
