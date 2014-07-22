package edu.cmu.sv.trailscribe.model;

import java.util.ArrayList;

public class SyncItems {
//	private Hashtable<Long, Map> maps;
//	private Hashtable<Long, Kml> kmls;
	
	private ArrayList<Map> maps;
	private ArrayList<Kml> kmls;
	
//	public SyncItems(Hashtable<Long, Map> mapsToSync, Hashtable<Long, Kml> kMLsToSync){
//		this.setMaps(mapsToSync);
//		this.setKMLs(kMLsToSync);
//	}
	
	public SyncItems(ArrayList<Map> mapsToSync, ArrayList<Kml> kMLsToSync){
		this.setMaps(mapsToSync);
		this.setKMLs(kMLsToSync);
	}
	
//	public Hashtable<Long, Map> getMaps() {
//		return maps;
//	}
//	public void setMaps(Hashtable<Long, Map> maps) {
//		this.maps = maps;
//	}
//	public Hashtable<Long, Kml> getKMLs() {
//		return kmls;
//	}
//	public void setKMLs(Hashtable<Long, Kml> mKMLs) {
//		this.kmls = mKMLs;
//	}
	
	public void setKMLs(ArrayList<Kml> kMLsToSync) {
		this.kmls = kMLsToSync;
		
	}

	public void setMaps(ArrayList<Map> mapsToSync) {
		this.maps = mapsToSync;
		
	}
	
	public ArrayList<Kml> getKmls(){
		return this.kmls;
	}
	
	public ArrayList<Map> getMaps(){
		return this.maps;
	}
}
