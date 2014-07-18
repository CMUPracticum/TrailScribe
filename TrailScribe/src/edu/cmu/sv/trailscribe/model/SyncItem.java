package edu.cmu.sv.trailscribe.model;

import java.util.ArrayList;

public abstract class SyncItem {
	private long mId;
	private String mName;
	private String mFileName;
	
	public long getId() {
		return mId;
	}
	public void setId(long mId) {
		this.mId = mId;
	}
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getFilename() {
		return mFileName;
	}
	
	public void setFilename(String fileName){
		this.mFileName = fileName;
	}

}
