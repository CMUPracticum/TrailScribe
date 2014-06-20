package edu.cmu.sv.trailscribe.View;

import edu.cmu.sv.trailscribe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
	private Button mStartButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_view);
	}
	
	public void openMap(View view) {
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
}
