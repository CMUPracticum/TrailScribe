package edu.cmu.sv.trailscribe.view;

import java.util.ArrayList;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.SynchronizationCenterController;
import edu.cmu.sv.trailscribe.dao.KmlDataSource;
import edu.cmu.sv.trailscribe.dao.MapDataSource;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.Kml;
import edu.cmu.sv.trailscribe.model.Map;
import edu.cmu.sv.trailscribe.model.SyncItem;
import edu.cmu.sv.trailscribe.utils.Decompressor;
import edu.cmu.sv.trailscribe.utils.Downloader;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SynchronizationCenterActivity 
    extends BaseActivity implements AsyncTaskCompleteListener {

	public static final ActivityTheme ACTIVITY_THEME = 
            new ActivityTheme("SyncCenter", "Synchronizes TrailScribe", R.color.red);
    
		private ListView mListView;
		private SynchronizationCenterController mController;
		private ArrayList<SyncItem> mSyncItems;
		private ProgressDialog mSyncProgressDialog;
		private ProgressDialog mDownloadProgressDialog;
		private ProgressDialog mUnzippingProgressDialog; 
		private ArrayAdapter<SyncItem> mAdapter;
		private String downloadDirectory = Environment.getExternalStorageDirectory() + "/trailscribe/";
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          mSyncProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.synchronizing), true, true);
      	  mDownloadProgressDialog = new ProgressDialog(SynchronizationCenterActivity.this);
      	  mUnzippingProgressDialog = new ProgressDialog(SynchronizationCenterActivity.this);
          mController = new SynchronizationCenterController(this);
          mController.execute();
      }
	  
	  @Override
	  protected void onPause(){
		  super.onPause();
	  }
	  
	  @Override
	  protected void onResume(){
		  super.onResume();
	  }
	  
	@SuppressWarnings("unchecked")
	@Override
	public void onTaskCompleted(Object result) {
		if (result instanceof ArrayList<?>){
			mSyncItems = (ArrayList<SyncItem>) result;
			
			if (mSyncProgressDialog != null){
	            mSyncProgressDialog.dismiss();
			}
			
			setContentView(R.layout.activity_sync_center);
			mListView = (ListView) findViewById(R.id.sync_list);
	
            setActionBar(getResources().getString(ACTIVITY_THEME.getActivityColor()));
			mAdapter = new ArrayAdapter<SyncItem>(SynchronizationCenterActivity.this,
	           android.R.layout.simple_list_item_1, android.R.id.text1, mSyncItems);
	 
	         // Assign adapter to ListView
	         mListView.setAdapter(mAdapter); 
		}
		else if(result instanceof Boolean){
			boolean downloadResult = (Boolean) result;
			if(downloadResult == true){
	            	new UnzipTask().execute();
	            }
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onSyncAll(View v){
		new Downloader(mSyncItems, SynchronizationCenterActivity.this, downloadDirectory,
				mDownloadProgressDialog, SynchronizationCenterActivity.this).execute();
	}
	
	private class UnzipTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected void onPreExecute() 
		{
			mUnzippingProgressDialog = ProgressDialog.show(SynchronizationCenterActivity.this, "", getResources().getString(R.string.uncompressing), true, true);
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			//Get the current thread's token
			synchronized (this) 
			{
				for (SyncItem item: mSyncItems){
					int subStringIndex = item.getFilename().lastIndexOf("/") +1;
	            	String mapFileName = item.getFilename().substring(subStringIndex);
	            	String folderName = null;
	        		if(item instanceof Map){
	        			folderName = "maps";
	        		}
	        		else if(item instanceof Kml){
	        			folderName = "kmls";
	        		}
	            	Decompressor decompressor = new Decompressor( downloadDirectory + folderName + "/"+ item.getName() + "/" + mapFileName, downloadDirectory + folderName + "/" + item.getName() + "/");
	            	decompressor.unzip();
				}
			}
			return null;
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) 
		{
			//close the progress dialog
			if(mUnzippingProgressDialog!= null){
				mUnzippingProgressDialog.dismiss();
			}
			
			for(SyncItem item: mSyncItems)
			if(item instanceof Map){
    			MapDataSource mapsDs = new MapDataSource(TrailScribeApplication.mDBHelper);
    			mapsDs.add(item);
    		}
    		else if(item instanceof Kml){
    			KmlDataSource kmlsDs = new KmlDataSource(TrailScribeApplication.mDBHelper);
    			kmlsDs.add(item);
    		}
			
			//Check for success
			mAdapter.clear();
			mAdapter.notifyDataSetChanged();
		}
	}
}
