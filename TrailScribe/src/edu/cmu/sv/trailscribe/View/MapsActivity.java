package edu.cmu.sv.trailscribe.View;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.Controller.MapsController;


public class MapsActivity extends Activity{
	private WebView mWebView;
	private MapsController mController;

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		mWebView = (WebView) findViewById(R.id.mapsview_webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, "android");
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setUseWideViewPort(false);
		mWebView.setWebViewClient(new WebViewClient());//{

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
