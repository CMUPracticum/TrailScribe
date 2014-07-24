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

	public Decompressor(String zipFile, String location) { 
		mCompressedFileFullPath = zipFile; 
		mDecompressingDirectory = location; 
	} 

	public Decompressor(ArrayList<SyncItem> syncItems, String baseDirectory, AsyncTaskCompleteListener<Integer> callback) {
		this.mSyncItems = syncItems;
		this.mBaseDirectory = baseDirectory;
		this.mTaskCompletedCallback = callback;
	}

	private void verifyDirectory(String directory) {
		if (!StorageSystemHelper.verifyDirectory(mDecompressingDirectory + directory)){
			StorageSystemHelper.createFolder(mDecompressingDirectory + directory);
		}
	}

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

	@Override
	protected Void doInBackground(Void... params) {
		for (SyncItem item: mSyncItems){
			String mapFileName = item.getFilename().substring(item.getFilename().lastIndexOf("/") +1);
			String folderName = null;
			if(item instanceof Map){
				folderName = "maps";
			}
			else if(item instanceof Kml){
				folderName = "kmls";
			}
			mCompressedFileFullPath =  mBaseDirectory + folderName + "/"+ item.getName() + "/" + mapFileName;
			mDecompressingDirectory = mBaseDirectory + folderName + "/" + item.getName() + "/";
			decompress();
		}
		return null;
	}

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
			mSuccessFlag = DECOMPRESSION_ERROR;
		}
	}

	@Override
	protected void onPostExecute(Void result) 
	{	
		for(SyncItem item: mSyncItems)
			if(item instanceof Map){
				MapDataSource mapsDs = new MapDataSource(TrailScribeApplication.mDBHelper);
				mapsDs.add((Map) item);
			}
			else if(item instanceof Kml){
				KmlDataSource kmlsDs = new KmlDataSource(TrailScribeApplication.mDBHelper);
				kmlsDs.add((Kml) item);
			}
		this.mTaskCompletedCallback.onTaskCompleted(mSuccessFlag);
	}
}
