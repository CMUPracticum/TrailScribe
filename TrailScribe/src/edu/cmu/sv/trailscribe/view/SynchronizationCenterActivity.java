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
		if (mSyncProgressDialog != null){
			mSyncProgressDialog.dismiss();
		}
		//close the progress dialog
		if(mDecompressProgressDialog!= null){
			mDecompressProgressDialog.dismiss();
		}

		// Handle any error related to synchronization results
		if(result == null){
			Toast.makeText(this,
					"There was an error during the synchronization process with TrailScribe backend. Please try again",
					Toast.LENGTH_LONG).show();
		}
		// Response from backend. Items to sync.
		if (result instanceof ArrayList<?>){
			mSyncItems = (ArrayList<SyncItem>) result;
			mAdapter = new ArrayAdapter<SyncItem>(SynchronizationCenterActivity.this,
					android.R.layout.simple_list_item_1, android.R.id.text1, mSyncItems);
			mListView.setAdapter(mAdapter); 
		}

		// Response from downloader. 
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
				mController.cancel(true);
				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						Toast.makeText(SynchronizationCenterActivity.this,
								"There was an error during the synchronization process with TrailScribe backend. Please try again",
								Toast.LENGTH_LONG).show();    
					}
				}); 
			}
		}
		else if(result instanceof Integer){
			int successCode = (Integer) result;
			if(successCode == Decompressor.DECOMPRESSION_SUCCESS){
				mAdapter.clear();
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void onSyncAll(View v){
		new Downloader(mSyncItems, SynchronizationCenterActivity.this, baseDirectory,
				mDownloadProgressDialog, SynchronizationCenterActivity.this).execute();
	}
}
