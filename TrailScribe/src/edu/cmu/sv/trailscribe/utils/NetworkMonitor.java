package edu.cmu.sv.trailscribe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkMonitor extends BroadcastReceiver{
	private Runnable mCallback;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(isInternetConnectionAvailable(context) == false){
			this.mCallback.run();
		}
	}
	
	public boolean isInternetConnectionAvailable(Context context) {
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting());
	}
	
	public void setCallback(Runnable callback){
		this.mCallback = callback;
	}
	

}
