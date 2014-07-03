package edu.cmu.sv.trailscribe.view;

import java.util.ArrayList;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.SynchronizationCenterController;
import edu.cmu.sv.trailscribe.model.Map;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SynchronizationCenterActivity extends Activity {
	  ListView mListView;
	  SynchronizationCenterController mController;
	  
	  public static final ActivityTheme ACTIVITY_THEME = 
				new ActivityTheme("SyncCenter", "Synchronizes TrailScribe", R.color.red);
	  
	  @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          mController = new SynchronizationCenterController();
          setContentView(R.layout.activity_sync_center);
          
          // Get ListView object from xml
          mListView = (ListView) findViewById(R.id.sync_list);
          
          // Defined Array values to show in ListView
          ArrayList<Map> maps = mController.syncMaps();
         // String[] values = new String[] { maps.get(0).getName()
                                          //};
          String[] values = new String[] { "Map 1"
                  };
                               
  
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, values);
  
  
          // Assign adapter to ListView
          mListView.setAdapter(adapter); 
          
          // ListView Item Click Listener
          mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                   int position, long id) {
                  
                  // Show Alert 
                  Toast.makeText(getApplicationContext(),
                    "Synchronizing..." , Toast.LENGTH_LONG)
                    .show();
               
                }
  
           }); 
      }
  
}
