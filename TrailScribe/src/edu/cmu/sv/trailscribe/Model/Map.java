package edu.cmu.sv.trailscribe.Model;

public class Map {
	private int mId;
	private String mName;
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
}
