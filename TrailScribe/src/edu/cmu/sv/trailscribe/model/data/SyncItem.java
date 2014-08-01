/* 
 * Copyright (c) 2014, TrailScribe Team.
 * This content is released under the MIT License. See the file named LICENSE for details.
 */
package edu.cmu.sv.trailscribe.model.data;

public abstract class SyncItem implements DataModel {
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

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public String toJson() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("'id':'").append(this.getId()).append("', ");
		buffer.append("'name':'").append(this.getName()).append("', ");
		buffer.append("'fileName':'").append(this.getFilename()).append("'");

		return buffer.toString();
	}
}
