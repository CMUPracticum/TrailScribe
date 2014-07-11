package edu.cmu.sv.trailscribe.model;

import java.io.File;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class Downloader extends AsyncTask<Void, Integer, Void> {
	private Map mMap;
	private Context mContext;
	private ProgressDialog mDownloadProgressDialog;
	private DownloadReceiver mDownloadReceiver; 
	private AsyncTaskCompleteListener<Integer> mTaskCompletedCallback;


	public Downloader (Map map, Context context, ProgressDialog downloadProgressDialog, AsyncTaskCompleteListener<Integer> callback){
		this.mMap = map;
		this.mContext = context;
		this.mTaskCompletedCallback = callback;
		this.mDownloadReceiver = new DownloadReceiver();
		this.mDownloadProgressDialog = downloadProgressDialog;
        this.mContext.registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	@Override
	protected void onPreExecute() 
	{
		//Set the progress dialog to display a horizontal progress bar 
		mDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//Set the dialog title to 'Loading...'
		mDownloadProgressDialog.setTitle("Downloading...");
		//Set the dialog message to 'Loading application View, please wait...'
		mDownloadProgressDialog.setMessage("Downloading items, please wait...");
		//This dialog can't be canceled by pressing the back key
		mDownloadProgressDialog.setCancelable(false);
		//This dialog isn't indeterminate
		mDownloadProgressDialog.setIndeterminate(false);
		//The maximum number of items is 100
		mDownloadProgressDialog.setMax(100);
		//Set the current progress to zero
		mDownloadProgressDialog.setProgress(0);
		//Display the progress dialog
		mDownloadProgressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Void result) 
	{
		//close the progress dialog
		if(mDownloadProgressDialog!= null){
			mDownloadProgressDialog.dismiss();
		}
		this.mContext.unregisterReceiver(mDownloadReceiver);

	}

	@Override
	protected Void doInBackground(Void... params) {
		int subStringIndex = mMap.getFilename().lastIndexOf("/") +1;
    	String mapFileName = mMap.getFilename().substring(subStringIndex);
    	final DownloadManager downloadManager = (DownloadManager) 
				mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		boolean isDownloading = false;
		
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterByStatus(
		    DownloadManager.STATUS_PAUSED|
		    DownloadManager.STATUS_PENDING|
		    DownloadManager.STATUS_RUNNING|
		    DownloadManager.STATUS_SUCCESSFUL);
		Cursor cur = downloadManager.query(query);
		int col = cur.getColumnIndex(
		    DownloadManager.COLUMN_LOCAL_FILENAME);
		for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
		    isDownloading = isDownloading || (mapFileName == cur.getString(col));
		}
		cur.close();
			
		// Download the file if it is not already downloaded
		if (!isDownloading) {
		    startDownload(mapFileName, downloadManager);            
   		}
		return null;
	}

	private void startDownload(String mapFileName,
			final DownloadManager downloadManager) {
		Uri source = Uri.parse(mMap.getFilename());
		Uri destination = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/trailscribe/"+ mapFileName));
 
		DownloadManager.Request request = 
		    new DownloadManager.Request(source);
		request.setTitle(mMap.getName());
		request.setDestinationUri(destination);
		request.setNotificationVisibility(DownloadManager
		    .Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.allowScanningByMediaScanner();
 
		final long id = downloadManager.enqueue(request);
   
		boolean downloading = true;
		while (downloading) {

		    DownloadManager.Query q = new DownloadManager.Query();
		    q.setFilterById(id);

		    Cursor cursor = downloadManager.query(q);
		    cursor.moveToFirst();
		    int bytes_downloaded = cursor.getInt(cursor
		            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
		    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

		    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
		        downloading = false;
		    }

		    final int dl_progress = (int) ((double)bytes_downloaded / (double)bytes_total * 100f);
		    ((Activity)mContext).runOnUiThread(new Runnable() {

		        @Override
		        public void run() {

		        	mDownloadProgressDialog.setProgress((int) dl_progress);

		        }
		    });
		    cursor.close();
		}
	}
	
	class DownloadReceiver extends BroadcastReceiver{
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	//close the progress dialog
			if(mDownloadProgressDialog!= null){
				mDownloadProgressDialog.dismiss();
			}
			
	        long receivedID = intent.getLongExtra(
	            DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
	        DownloadManager mgr = (DownloadManager)
	            context.getSystemService(Context.DOWNLOAD_SERVICE);
	 
	        DownloadManager.Query query = new DownloadManager.Query();
	        query.setFilterById(receivedID);
	        Cursor cur = mgr.query(query);
	        int index = cur.getColumnIndex(
	            DownloadManager.COLUMN_STATUS);
	        if(cur.moveToFirst()) {
	        	mTaskCompletedCallback.onTaskCompleted(cur.getInt(index));
	        }
	        cur.close();
	    }
	}
}
