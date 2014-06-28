package edu.cmu.sv.trailscribe.view;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;


public class MainActivity extends Activity implements OnItemClickListener {
	private static final String MSG_TAG = "MainActivity";
	public static final ActivityTheme ACTIVITY_THEME = 
			new ActivityTheme("MainActivity", "Main page", R.color.blue);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_main);
		
		setTitleBar();
		setGrid();
	}
	
//	FIXME: Bad design, the code should be reusable
	private void setTitleBar() {
		View titleBar = (View) findViewById(R.id.main_titlebar);
		titleBar.setBackgroundColor(getResources().getColor(ACTIVITY_THEME.getActivityColor()));
	}
	
	private void setGrid() {
		GridView gridView = (GridView) findViewById(R.id.main_buttongrid);
		
//		TODO: Update themes when other activities are implemented
		ActivityTheme[] options = new ActivityTheme[]{
				new ActivityTheme("Expeditions", "Description", R.color.blue),
				MapsActivity.ACTIVITY_THEME,
				new ActivityTheme("Sync Center", "Description", R.color.red),
				new ActivityTheme("Settings", "Description", R.color.gray)
		};
		
		List<ActivityTheme> list = Arrays.asList(options);
		OptionAdapter adapter = new OptionAdapter(this, list);
		
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(MainActivity.this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Log.d(MSG_TAG, "onItemClick: " + position + " is clicked");
		
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
	
	private class OptionAdapter extends ArrayAdapter<ActivityTheme> {
	    public OptionAdapter(Context context, List<ActivityTheme> options) {
	       super(context, R.layout.component_buttongrid, options);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       ActivityTheme option = getItem(position);    
	       if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(
	        		  R.layout.component_buttongrid, parent, false);
	       }
	       
	       TextView optionName = (TextView) convertView.findViewById(R.id.buttongrid_title);
	       TextView optionDescription = (TextView) convertView.findViewById(R.id.buttongrid_text);
	       View optionBar = (View) convertView.findViewById(R.id.buttongrid_bar);
	       
	       option.updateActivityDescription();
	       
	       optionName.setText(option.getActivityName());
	       optionDescription.setText(option.getActivityDescription());
	       optionBar.setBackgroundColor(getResources().getColor(option.getActivityColor()));
	       
	       return convertView;
	   }
	}
}
