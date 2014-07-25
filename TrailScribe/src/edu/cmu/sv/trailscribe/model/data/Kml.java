package edu.cmu.sv.trailscribe.model.data;

public class Kml extends SyncItem {
	private String mLastModified; 
	
	public Kml(){}
	
	public Kml(
			long id, String name, String filename, String lastModified) {
		super.setId(id);
		super.setName(name);
		super.setFilename(filename);
		setLastModified(lastModified);
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
	
	public String getFilename() {
		return super.getFilename();
	}

	public void setFilename(String mFilename) {
		super.setFilename(mFilename);
	}
	
	public String getLastModified() {
		return mLastModified;
	}

	public void setLastModified(String mLastModified) {
		this.mLastModified = mLastModified;
	}

    @Override
    public String toJson() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("'id':'").append(this.getId()).append("', ");
        buffer.append("'name':'").append(this.getName()).append("', ");
        buffer.append("'fileName':'").append(this.getFilename()).append("', ");
        buffer.append("'lastModified':'").append(this.getLastModified()).append("'");
    
        return buffer.toString();
    }
}
