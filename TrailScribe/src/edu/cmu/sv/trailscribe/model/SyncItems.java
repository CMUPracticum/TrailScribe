package edu.cmu.sv.trailscribe.model;

import java.util.Hashtable;

public class SyncItems {
	private Hashtable<Long, Map> maps;
	private Hashtable<Long, Kml> kmls;
	
	public SyncItems(Hashtable<Long, Map> mapsToSync, Hashtable<Long, Kml> kMLsToSync){
		this.setMaps(mapsToSync);
		this.setKMLs(kMLsToSync);
	}
	
	public Hashtable<Long, Map> getMaps() {
		return maps;
	}
	public void setMaps(Hashtable<Long, Map> maps) {
		this.maps = maps;
	}
	public Hashtable<Long, Kml> getKMLs() {
		return kmls;
	}
	public void setKMLs(Hashtable<Long, Kml> mKMLs) {
		this.kmls = mKMLs;
	}
}
