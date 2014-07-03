package edu.cmu.sv.trailscribe.view;

import java.util.Arrays;
import java.util.List;

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


public class MainActivity extends BaseActivity implements OnItemClickListener {

	public static ActivityTheme ACTIVITY_THEME = new ActivityTheme("MainActivity", "Main page", R.color.blue);
	public static String MSG_TAG = "Main page";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_main);
		
		setTitleBar(R.id.main_titlebar, ACTIVITY_THEME.getActivityColor());
		setGrid();
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
