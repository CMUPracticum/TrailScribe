package edu.cmu.sv.trailscribe.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

public class BackendFacade extends AsyncTask <String, Void, String>{
	private String urlParameters;	
	private String endpoint;
	private AsyncTaskCompleteListener<String> mTaskCompletedCallback;
	

	
	public BackendFacade(String endpoint, AsyncTaskCompleteListener<String> callback, String urlParameters){
		this.endpoint = endpoint;
		this.mTaskCompletedCallback = callback;
		this.urlParameters = urlParameters;
	}
	
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
	    	  // Define what to do
		  e.printStackTrace();
	  } catch (IOException e) {
		  // Define what to do
		  e.printStackTrace();
	  }
	  return new String();
	}
	
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
		String response = stringBuffer.toString();
		return response;
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
