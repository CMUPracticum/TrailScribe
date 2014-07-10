package edu.cmu.sv.trailscribe.view;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.SynchronizationCenterController;
import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import edu.cmu.sv.trailscribe.model.Map;

public class SynchronizationCenterActivity 
    extends BaseActivity implements AsyncTaskCompleteListener<ArrayList<Map>> {

    public static final ActivityTheme ACTIVITY_THEME = 
            new ActivityTheme("SyncCenter", "Synchronizes TrailScribe", R.color.red);
    
    private SynchronizationCenterController mController;
    private ArrayList<Map> maps;

//  Views
    private ListView mListView;
    private ProgressDialog mProgressDialog;
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.synchronizing), true, true);
          mController = new SynchronizationCenterController(this);
          mController.execute();
      }
	  
	@Override
	public void onTaskCompleted(ArrayList<Map> result) {
		maps = result;
		
		if (mProgressDialog != null) {
            mProgressDialog.dismiss();
		}
		
		setView();
	}
	
	private void setView() {
	    setContentView(R.layout.activity_sync_center);
	    mListView = (ListView) findViewById(R.id.sync_list);
        
	    String[] values = new String[maps.size()];
	    for (int i = 0; i< values.length; i++){
	        values[i] = maps.get(i).getName();
	    }

	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	            SynchronizationCenterActivity.this, 
	            android.R.layout.simple_list_item_1, android.R.id.text1, values);
         // Assign adapter to ListView
	    
	    mListView.setAdapter(adapter); 

	    // ListView Item Click Listener
	    mListView.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // Show Alert 
	            Toast.makeText(getApplicationContext(), "Synchronizing..." , Toast.LENGTH_LONG).show();
	        }
	    }); 
	}
}
