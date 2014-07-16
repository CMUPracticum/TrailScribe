package edu.cmu.sv.trailscribe.view;

import java.util.ArrayList;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.SynchronizationCenterController;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.Decompressor;
import edu.cmu.sv.trailscribe.model.Downloader;
import edu.cmu.sv.trailscribe.model.Map;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SynchronizationCenterActivity 
    extends BaseActivity implements AsyncTaskCompleteListener {

	public static final ActivityTheme ACTIVITY_THEME = 
            new ActivityTheme("SyncCenter", "Synchronizes TrailScribe", R.color.red);
    
		private ListView mListView;
		private SynchronizationCenterController mController;
		private ArrayList<Map> mMaps;
		private Map mCurrentMap;
		private ProgressDialog mSyncProgressDialog;
		private ProgressDialog mDownloadProgressDialog;
		private ProgressDialog mUnzippingProgressDialog; 
		private ArrayAdapter<Map> mAdapter;
		private String downloadDirectory = Environment.getExternalStorageDirectory() + "/trailscribe/maps/";
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          mSyncProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.synchronizing), true, true);
      	  mDownloadProgressDialog = new ProgressDialog(SynchronizationCenterActivity.this);
      	  mUnzippingProgressDialog = new ProgressDialog(SynchronizationCenterActivity.this);
          mController = new SynchronizationCenterController(this, this);
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
			mMaps = (ArrayList<Map>) result;
			
			if (mSyncProgressDialog != null){
	            mSyncProgressDialog.dismiss();
			}
			
			setContentView(R.layout.activity_sync_center);
			mListView = (ListView) findViewById(R.id.sync_list);
	
            setActionBar(getResources().getString(ACTIVITY_THEME.getActivityColor()));
			mAdapter = new ArrayAdapter<Map>(SynchronizationCenterActivity.this,
	           android.R.layout.simple_list_item_1, android.R.id.text1, mMaps);
	 
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
		new Downloader(mMaps, SynchronizationCenterActivity.this, downloadDirectory,
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
				for (Map map: mMaps){
					int subStringIndex = map.getFilename().lastIndexOf("/") +1;
	            	String mapFileName = map.getFilename().substring(subStringIndex);
	            	Decompressor decompressor = new Decompressor( downloadDirectory + map.getName() + "/" + mapFileName, downloadDirectory + map.getName() + "/");
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
			//Check for success
			mAdapter.clear();
			mAdapter.notifyDataSetChanged();
		}
	}
}
