/* 
 * Copyright (c) 2014, TrailScribe Team.
 * This content is released under the MIT License. See the file named LICENSE for details.
 */
package edu.cmu.sv.trailscribe.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.cmu.sv.trailscribe.dao.KmlDataSource;
import edu.cmu.sv.trailscribe.dao.MapDataSource;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.data.Kml;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.model.data.SyncItem;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;
import android.os.AsyncTask;

public class Decompressor extends AsyncTask <Void, Void, Void>{
	private String mCompressedFileFullPath; 
	private String mDecompressingDirectory; 
	private String mBaseDirectory;
	private ArrayList<SyncItem> mSyncItems;
	private Integer mSuccessFlag = DECOMPRESSION_SUCCESS;
	private AsyncTaskCompleteListener<Integer> mTaskCompletedCallback;

	public static final int DECOMPRESSION_SUCCESS = 0;
	public static final int DECOMPRESSION_ERROR = -1;

	public Decompressor(ArrayList<SyncItem> syncItems, String baseDirectory, AsyncTaskCompleteListener<Integer> callback) {
		this.mSyncItems = syncItems;
		this.mBaseDirectory = baseDirectory;
		this.mTaskCompletedCallback = callback;
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (SyncItem item: mSyncItems){
			String mapFileName = item.getFilename().substring(item.getFilename().lastIndexOf("/") +1);
			String folderName = getFolderNameBasedOnItemType(item);
			mCompressedFileFullPath =  mBaseDirectory + folderName + "/"+ item.getName() + "/" + mapFileName;
			mDecompressingDirectory = mBaseDirectory + folderName + "/" + item.getName() + "/";
			decompress();
		}
		return null;
	}

	// Determine the folder name for decompressing based on the item type (maps/kmls)
	private String getFolderNameBasedOnItemType(SyncItem item) {
		String folderName = null;
		if(item instanceof Map){
			folderName = "maps";
		}
		else if(item instanceof Kml){
			folderName = "kmls";
		}
		return folderName;
	}
	
	//Check the directory actually exists before decompressing
	private void verifyDirectory(String directory) {
		if (!StorageSystemHelper.verifyDirectory(mDecompressingDirectory + directory)){
			StorageSystemHelper.createFolder(mDecompressingDirectory + directory);
		}
	}

	// Removing compressed files after compression
	private void removeZipFile() {
		String extension = "";
		int i = this.mCompressedFileFullPath.lastIndexOf('.');
		if (i >= 0) {
			extension = this.mCompressedFileFullPath.substring(i+1);
		}

		if(extension.equals("zip")){
			StorageSystemHelper.removeFile(this.mCompressedFileFullPath);
		}
	}
	
	// Decompress file
	private void decompress() {
		try{ 
			FileInputStream fileInputStream = new FileInputStream(mCompressedFileFullPath); 
			ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream)); 
			ZipEntry zipEntry = null; 
			while ((zipEntry = zipInputStream.getNextEntry()) != null) { 
				if(zipEntry.isDirectory()) { 
					verifyDirectory(zipEntry.getName()); 
				} else { 
					FileOutputStream fout = new FileOutputStream(mDecompressingDirectory + zipEntry.getName());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int count;		            
					while ((count = zipInputStream.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
					}
					baos.writeTo(fout);
					zipInputStream.closeEntry(); 
					fout.close(); 
				}
			}  
			zipInputStream.close(); 
			removeZipFile();
		} catch(Exception e) {
			e.printStackTrace();
			// Current version will return error if at least one file failed in decompressing
			mSuccessFlag = DECOMPRESSION_ERROR;
		}
	}

	@Override
	protected void onPostExecute(Void result) 
	{
		// If everything went ok, update database with items already downloaded
		if(mSuccessFlag == DECOMPRESSION_SUCCESS){
			for(SyncItem item: mSyncItems)
				if(item instanceof Map){
					MapDataSource mapsDs = new MapDataSource(TrailScribeApplication.getDBHelper());
					mapsDs.add((Map) item);
				}
				else if(item instanceof Kml){
					KmlDataSource kmlsDs = new KmlDataSource(TrailScribeApplication.getDBHelper());
					kmlsDs.add((Kml) item);
				}
		}
		this.mTaskCompletedCallback.onTaskCompleted(mSuccessFlag);
	}
}
