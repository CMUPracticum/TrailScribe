package edu.cmu.sv.trailscribe.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.controller.MapsController;


public class MapsActivity extends Activity {
	public static final ActivityTheme ACTIVITY_THEME = 
			new ActivityTheme("MapActivity", "Display map and layers", R.color.green);
	
	private WebView mWebView;
	private MapsController mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();
	}
	
	private void setView() {
		setContentView(R.layout.activity_maps);
		
		setTitleBar();
		setMap();
	}
	
//	FIXME: Bad design, the code should be reusable
	private void setTitleBar() {
		View titleBar = (View) findViewById(R.id.mapsview_titlebar);
		titleBar.setBackgroundColor(getResources().getColor(ACTIVITY_THEME.getActivityColor()));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return mapPoints.toString();
	}
}
