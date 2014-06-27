package edu.cmu.sv.trailscribe.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;


public class MainActivity extends Activity implements OnItemClickListener {
	private static final String MSG_TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_main);
		
		setGrid();
	}
	
	private void setGrid() {
		GridView gridView = (GridView) findViewById(R.id.main_buttongrid);
		
//		TODO Design a better data structure for the options
		String[] options = new String[]{"Expeditions", "Maps", "Sync Center", "Settings"};
		String[] optionDescriptions = new String[]{"Description", "Description", "Description", "Description"};
		List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
		
		for (int i = 0; i < options.length; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("option", options[i]);
			item.put("optionDescription", optionDescriptions[i]);
			items.add(item);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.component_buttongrid,
				new String[]{"option", "optionDescription"}, new int[]{R.id.buttongrid_title, R.id.buttongrid_text});
		
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(MainActivity.this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		
		Log.d(MSG_TAG, "onItemClick: " + position + " is clicked");
		Toast.makeText(getApplicationContext(), "onItemClick", Toast.LENGTH_SHORT).show();
		
		Intent intent;
		switch (position) {
		case 1:
			intent = new Intent(this, MapsActivity.class);
			break;
			default:
				Toast.makeText(getApplicationContext(), "Sorry, the feature is not implemented yet!", Toast.LENGTH_SHORT).show();
				return;
		}
		
		startActivity(intent);
	}
}
