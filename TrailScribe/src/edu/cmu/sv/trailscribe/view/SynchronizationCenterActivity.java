package edu.cmu.sv.trailscribe.view;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.SynchronizationCenterController;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.data.SyncItem;
import edu.cmu.sv.trailscribe.utils.Decompressor;
import edu.cmu.sv.trailscribe.utils.Downloader;

@SuppressWarnings("rawtypes") // Suppressing warning given this class listens to 2 different AsyncTasks
public class SynchronizationCenterActivity 
extends BaseActivity implements AsyncTaskCompleteListener {

	public static final ActivityTheme ACTIVITY_THEME = 
			new ActivityTheme("SyncCenter", "Synchronizes TrailScribe", R.color.red);

	private ListView mListView;
	private SynchronizationCenterController mController;
	private ArrayList<SyncItem> mSyncItems;
	private ProgressDialog mSyncProgressDialog;
	private ProgressDialog mDownloadProgressDialog;
	private ProgressDialog mDecompressProgressDialog; 
	private ArrayAdapter<SyncItem> mAdapter;
	private String baseDirectory = Environment.getExternalStorageDirectory() + "/trailscribe/";
	private Downloader mDownloader; 

	@SuppressWarnings("unchecked") // Suppressing warning given this class listens to 2 different AsyncTasks
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync_center);
		mListView = (ListView) findViewById(R.id.sync_list);
		setActionBar(getResources().getString(ACTIVITY_THEME.getActivityColor()));

		mSyncProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.synchronizing), true, true);
		mDownloadProgressDialog = new ProgressDialog(SynchronizationCenterActivity.this);
		mDecompressProgressDialog = new ProgressDialog(SynchronizationCenterActivity.this);
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
		//close the progress dialogs

		if (mSyncProgressDialog != null){
			try{
				mSyncProgressDialog.dismiss();
			}catch (IllegalArgumentException e) {
                // "Sync Dialog dismissed after view stopped"
			}
		}
		if(mDecompressProgressDialog!= null){
			try{
				mDecompressProgressDialog.dismiss();
			}catch (IllegalArgumentException e) {
                // "Decompress dialog dismissed after view stopped"
			}
		}

		// Handle any error related to synchronization results
		if(result == null){
			showMessage(getResources().getString(R.string.connection_error));		
		}
		// Response from backend. Items to sync.
		if (result instanceof ArrayList<?>){
			mSyncItems = (ArrayList<SyncItem>) result;
			mAdapter = new ArrayAdapter<SyncItem>(SynchronizationCenterActivity.this,
					android.R.layout.simple_list_item_1, android.R.id.text1, mSyncItems);
			mListView.setAdapter(mAdapter); 
			if(mSyncItems.size() == 0){
				showMessage(getResources().getString(R.string.up_to_date));
			}
		}

		// Response from downloader. If success, start uncompressing
		else if(result instanceof Boolean){
			if (mDownloadProgressDialog != null){
				mDownloadProgressDialog.dismiss();
			}
			boolean downloadResult = (Boolean) result;
			if(downloadResult == true){
				mDecompressProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.uncompressing), true, true);
				new Decompressor (mSyncItems, baseDirectory, SynchronizationCenterActivity.this).execute();
			}
			else{
				if(mDownloader != null){
					mDownloader.cancel(true);
				}
				showMessage(getResources().getString(R.string.connection_error));
			}
		}
		
		//Response from Decompressor
		else if(result instanceof Integer){
			int successCode = (Integer) result;
			if(successCode == Decompressor.DECOMPRESSION_SUCCESS){
				mAdapter.clear();
				mAdapter.notifyDataSetChanged();
				showMessage(getResources().getString(R.string.up_to_date));
			}
		}
	}
	
	// Helper method to display messages in a toast
	private void showMessage(final String message){
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				Toast.makeText(SynchronizationCenterActivity.this, message, Toast.LENGTH_LONG).show();    
			}
		}); 
	}
	
	//This method is invoked whenever the SyncAll button is clicked
	@SuppressWarnings("unchecked")
	public void onSyncAll(View v){
		mDownloader = (Downloader)new Downloader(mSyncItems, SynchronizationCenterActivity.this, baseDirectory,
				mDownloadProgressDialog, SynchronizationCenterActivity.this).execute();
	}
}
