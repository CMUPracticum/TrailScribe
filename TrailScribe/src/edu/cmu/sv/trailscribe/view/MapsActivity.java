package edu.cmu.sv.trailscribe.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.MapsController;


public class MapsActivity extends Activity implements OnClickListener {
	public static final ActivityTheme ACTIVITY_THEME = 
			new ActivityTheme("MapActivity", "Display map and layers", R.color.green);
	private static final String MSG_TAG = "MapsActivity";

//	Controllers
	private MapsController mController;
	
//	Views
	private WebView mWebView;
	private Button mPointsOfInterestButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_maps);
		
		setTitleBar();
		setMap();
		setListener();
	}
	
//	FIXME: Bad design, the code should be reusable
	private void setTitleBar() {
		View titleBar = (View) findViewById(R.id.mapsview_titlebar);
		titleBar.setBackgroundColor(getResources().getColor(ACTIVITY_THEME.getActivityColor()));
	}
	
	private void setListener() {
		mPointsOfInterestButton = (Button) findViewById(R.id.mapsview_points_of_interest);
		mPointsOfInterestButton.setOnClickListener(this);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setMap() {
		mWebView = (WebView) findViewById(R.id.mapsview_webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, "android");
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setUseWideViewPort(false);
		mWebView.setWebViewClient(new WebViewClient());

		mController = new MapsController();
		mWebView.loadUrl(mController.getURL());
	}
	
	private void setPointsOfInterest(boolean display) {
		String message = (display)? 
				MessageToWebview.DisplayPointsOfInterest.getMessage() : 
					MessageToWebview.HidePointsOfInterest.getMessage();
		mWebView.loadUrl("javascript:displayPointsOfInterest(\"" + message + "\")");
	}
	
	/** This passes our data out to the JS */
	@JavascriptInterface
	public String getData() {
		return getMapData();
	}
	
	private String getMapData(){
		JSONObject mapPoints = null;
		try {
			mapPoints = new JSONObject("{'points':[{'x':'-122.04451', 'y':'37.41800'},{'x':'-122.07451', 'y':'37.41800'}, {'x':'-122.10451', 'y':'37.39800'}]}'");
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return mapPoints.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mapsview_points_of_interest:
			String state = mPointsOfInterestButton.getText().toString();
			boolean display = (state.equals(getResources().getString(R.string.map_display_points_of_interest)));
			
			if (display) {
				mPointsOfInterestButton.setText(R.string.map_hide_points_of_interest);
			} else {
				mPointsOfInterestButton.setText(R.string.map_display_points_of_interest);
			}
			
			setPointsOfInterest(display);
			break;
			default:
				Toast.makeText(getApplicationContext(), 
						"Sorry, the feature is not implemented yet!", Toast.LENGTH_SHORT).show();
				return;
		}
	}
	
	private enum MessageToWebview {
		DisplayPointsOfInterest("display"),
		HidePointsOfInterest("hide");
		
		private final String message;
		MessageToWebview(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
	};
}