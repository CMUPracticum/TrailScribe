package edu.cmu.sv.trailscribe.View;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.Controller.MapsController;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MapsActivity extends Activity{
	private WebView mWebView;
	private MapsController mController;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps_view);
		mWebView = (WebView) findViewById(R.id.webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setUseWideViewPort(false);
		mWebView.setWebViewClient(new WebViewClient());
		mController = new MapsController();
		mWebView.loadUrl(mController.getURL());
	}
}
