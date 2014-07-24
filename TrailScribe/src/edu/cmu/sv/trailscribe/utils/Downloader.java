package edu.cmu.sv.trailscribe.utils;

import java.io.File;
import java.util.ArrayList;

import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.data.Kml;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.model.data.SyncItem;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

public class Downloader extends AsyncTask<Void, Integer, Void> {
	private ArrayList<SyncItem> mSyncItems;
	private Context mContext;
	private ProgressDialog mDownloadProgressDialog;
	private DownloadReceiver mDownloadReceiver; 
	private NetworkReceiver mNetworkReceiver;
	private AsyncTaskCompleteListener<Boolean> mTaskCompletedCallback;
	private ArrayList<Long> mPendingDownloads = new ArrayList<Long>();
	private String mDownloadDirectory;
	private SyncItem mCurrentDownload = null;
	private int mAmountOfDownloads = 0;

	public Downloader (ArrayList<SyncItem> mSyncItems, Context context, String downloadDirectory, ProgressDialog downloadProgressDialog, AsyncTaskCompleteListener<Boolean> callback){
		this.mSyncItems	 = mSyncItems;
		this.mContext = context;
		this.mTaskCompletedCallback = callback;
		this.mDownloadReceiver = new DownloadReceiver();
		this.mNetworkReceiver = new NetworkReceiver();
		this.mDownloadProgressDialog = downloadProgressDialog;
		this.mDownloadDirectory = downloadDirectory;
		this.mContext.registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		this.mContext.registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	protected void onPreExecute() 
	{
		//Set the progress dialog to display a horizontal progress bar 
		mDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//Set the dialog title to 'Loading...'
		mDownloadProgressDialog.setTitle("Synchronizing...");
		//Set the dialog message to 'Loading application View, please wait...'
		mDownloadProgressDialog.setMessage("Synchronizing items, please wait...");
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
		this.mContext.unregisterReceiver(mDownloadReceiver);
		this.mContext.unregisterReceiver(mNetworkReceiver);
		if(mDownloadProgressDialog!= null){
			mDownloadProgressDialog.dismiss();
		}
		boolean success = true;
		if(this.mPendingDownloads.size() != 0){
			success = false;
		}
		this.mTaskCompletedCallback.onTaskCompleted(success);
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (SyncItem item: mSyncItems){
			int subStringIndex = item.getFilename().lastIndexOf("/") +1;
			String mapFileName = item.getFilename().substring(subStringIndex);
			final DownloadManager downloadManager = (DownloadManager) 
					mContext.getSystemService(Context.DOWNLOAD_SERVICE);
			boolean isDownloading = false;

			DownloadManager.Query query = new DownloadManager.Query();
			query.setFilterByStatus(
					DownloadManager.STATUS_PAUSED|
					DownloadManager.STATUS_PENDING|
					DownloadManager.STATUS_RUNNING|
					DownloadManager.STATUS_SUCCESSFUL|
					DownloadManager.STATUS_FAILED);

			Cursor cursor = downloadManager.query(query);
			int pathNameColumn = cursor.getColumnIndex(
					DownloadManager.COLUMN_LOCAL_FILENAME);
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				isDownloading = isDownloading || (mapFileName == cursor.getString(pathNameColumn));
			}
			cursor.close();

			// Download the file if it is not already downloaded
			if (!isDownloading) {
				if (isInternetConnectionAvailable() == true){
					startDownload(item, mapFileName, downloadManager);
				}
				else {
					if(mDownloadProgressDialog!= null){
						mDownloadProgressDialog.dismiss();
					}
					this.mTaskCompletedCallback.onTaskCompleted(false);
				}
			}
		}
		return null;
	}

	private boolean isInternetConnectionAvailable() {
		ConnectivityManager cm =
				(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting());
	}

	private void verifyDirectory(String directory) {
		if (!StorageSystemHelper.verifyDirectory(directory)){
			StorageSystemHelper.createFolder(directory);
		}
		else{
			StorageSystemHelper.removeDirectoryContent(directory);
		}
	}

	private void startDownload(SyncItem item, String mapFileName,
			final DownloadManager downloadManager) {
		Uri source = Uri.parse(item.getFilename());
		String folderName = getFolderNameBaseOnItemType(item);
		String directory = this.mDownloadDirectory + folderName + "/"+ item.getName()+ "/";
		verifyDirectory(directory);
		
		Uri destination = Uri.fromFile(new File(directory + mapFileName));
		DownloadManager.Request request = 
				new DownloadManager.Request(source);
		request.setTitle(item.getName());
		request.setDestinationUri(destination);
		request.setNotificationVisibility(DownloadManager
				.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.allowScanningByMediaScanner();

		final long id = downloadManager.enqueue(request);
		mPendingDownloads.add(id);
		mAmountOfDownloads ++;
		mCurrentDownload = item;

		boolean downloading = true;
		while (downloading) {
			downloading = writeFile(downloadManager, id, downloading);
		}  
	}

	private String getFolderNameBaseOnItemType(SyncItem item) {
		String folderName = null;
		if(item instanceof Map){
			folderName = "maps";
		}
		else if(item instanceof Kml){
			folderName = "kmls";
		}
		return folderName;
	}

	private boolean writeFile(final DownloadManager downloadManager,
			final long id, boolean downloading) {
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

				publishProgress((int) dl_progress);

			}
		});
		cursor.close();
		return downloading;
	}

	protected void onProgressUpdate(Integer... progress) {
		mDownloadProgressDialog.setProgress(progress[0]);
		if(mPendingDownloads != null) {
			mDownloadProgressDialog.setMessage("Synchronizing " + this.mCurrentDownload.getName() + ". Item "+ mAmountOfDownloads + "/" + this.mSyncItems.size());
		}


	}

	class DownloadReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			long receivedID = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
			DownloadManager mgr = (DownloadManager)
					context.getSystemService(Context.DOWNLOAD_SERVICE);

			DownloadManager.Query query = new DownloadManager.Query();
			query.setFilterById(receivedID);
			Cursor cursor = mgr.query(query);
			int status = cursor.getColumnIndex(
					DownloadManager.COLUMN_STATUS);
			if(cursor.moveToFirst()) {
				if(cursor.getInt(status) == DownloadManager.STATUS_SUCCESSFUL){
					mPendingDownloads.remove(receivedID);
				}
			}
			cursor.close();
		}
	}
	
	
	class NetworkReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(isInternetConnectionAvailable() == false){
				mTaskCompletedCallback.onTaskCompleted(false);
			}
		}
		
	}
}
