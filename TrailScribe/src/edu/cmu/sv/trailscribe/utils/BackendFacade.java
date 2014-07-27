package edu.cmu.sv.trailscribe.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import edu.cmu.sv.trailscribe.model.AsyncTaskCompleteListener;
import android.os.AsyncTask;

public class BackendFacade extends AsyncTask <String, Void, String>{
	private String urlParameters;	
	private String endpoint;
	private AsyncTaskCompleteListener<String> mTaskCompletedCallback;
	private NetworkMonitor mNetworkMonitor;

	/**
	 * @param endpoint URL of the backend to establish the connection
	 * @param callback The class which waits for this class results
	 * @param urlParameters The POST parameters to the request 
	 */
	public BackendFacade(String endpoint, AsyncTaskCompleteListener<String> callback, String urlParameters){
		this.endpoint = endpoint;
		this.mTaskCompletedCallback = callback;
		this.urlParameters = urlParameters;
		this.mNetworkMonitor = new NetworkMonitor();

		//Register callback for connection events
		mNetworkMonitor.setCallback(new Runnable() {
            @Override
            public void run() {
            	mTaskCompletedCallback.onTaskCompleted("");
            }
        });
	}
	
	 /**
     * Establishes connection to the backend and retrieves the response data in String format
     * @return response in String format 
     */
	private String getResourceInfoFromBackend(){
		URL url;
		HttpURLConnection connection = null;
		try {
			url = new URL(endpoint);

			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", 
					"application/json");

			connection.setRequestProperty("Content-Length", "" + 
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.write(urlParameters.getBytes());
			wr.flush ();
			wr.close ();

			return getResponseContent(connection);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new String();
		} catch (IOException e) {
			e.printStackTrace();
			return new String();
		}
	}
	
	 /**
     * Given the connection to the backend established, read the data from the channel    
     * 
     * @param connection the HTTP connection established
     * @return the data retrieved by the backend as String 
     */
	private String getResponseContent(HttpURLConnection connection){
		StringBuffer stringBuffer = new StringBuffer();
		if(connection!=null){
			try {			
				BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String input;

				while ((input = br.readLine()) != null){
					stringBuffer.append(input);
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
				return new String();
			}
		}
		return stringBuffer.toString();
	}

	@Override
	protected String doInBackground(String... arg0) {
		return getResourceInfoFromBackend();
	}

	@Override
	protected void onPostExecute(String response){
		super.onPostExecute(response);
		this.mTaskCompletedCallback.onTaskCompleted(response);
	}
}
