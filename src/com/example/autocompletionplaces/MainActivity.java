package com.example.autocompletionplaces;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;




public class MainActivity extends Activity implements OnItemClickListener {

	private static final String LOG_TAG = "AutoCompletion";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyCN4cr4Wc7OUFePRM1A5PS0g_3V6QK3FIQ";

	
	//public PlacesAutoCompleteAdapter adapter;
	public ArrayAdapter<String> adapter;
	public AutoCompleteTextView autoCompView;
	// Code to get predictions of places

	public class FetchPlacesPredictions extends AsyncTask <String, Void, ArrayList<String>> {
	
	
		
		protected ArrayList<String> doInBackground(String... input) {
	         
			ArrayList<String> resultList = new ArrayList<String>();
			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				
				StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
				sb.append("?sensor=false&key=" + API_KEY);
				sb.append("&components=country:in");
				sb.append("&input=" + URLEncoder.encode(input[0], "utf8"));
				
				URL url = new URL(sb.toString());
				
				conn = (HttpURLConnection) url.openConnection();
				
				InputStreamReader in = new InputStreamReader(conn.getInputStream());
		        
				
				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
		    	} catch (MalformedURLException e) {
		    		Log.e(LOG_TAG, "Error processing Places API URL", e);
		    		return resultList;
		    	} catch (IOException e) {
		    		Log.e(LOG_TAG, "Error connecting to Places API", e);
		    		return resultList;
		    	} finally {
		    		if (conn != null) {
		    			conn.disconnect();
		    		}
		    	}
			

		    try {
		        // Create a JSON object hierarchy from the results
		        JSONObject jsonObj = new JSONObject(jsonResults.toString());
		        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
		        
		        // Extract the Place descriptions from the results
		        resultList = new ArrayList<String>(predsJsonArray.length());
		        for (int i = 0; i < predsJsonArray.length(); i++) {
		            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
		            
		        }
		    } catch (JSONException e) {
		        Log.e(LOG_TAG, "Cannot process JSON results", e);
		    }
		    
		    System.out.println("DograSuggestions: " + resultList.get(2) + " secondsuggestion");
		    return resultList;
		}
		

		//then our post

		@Override
		protected void onPostExecute(ArrayList<String> resultList) 	{

			Log.d("YourApp", "onPostExecute : " + resultList.size());
			adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item);
			adapter.setNotifyOnChange(true);
			//attach the adapter to textview
			autoCompView.setAdapter(adapter);
			for (String string : resultList) {

				Log.d("YourApp", "onPostExecute : result = " + string);
				adapter.add(string);
				adapter.notifyDataSetChanged();

			}

			Log.d("YourApp", "onPostExecute : autoCompleteAdapter" + adapter.getCount());

		}
		
		
		
	}

	/*
			
	public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;
	    
	    
	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }
	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return resultList.get(index); 
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	        	
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                	 FetchPlacesPredictions task = new FetchPlacesPredictions();
	    			     task.execute(autoCompView.getText().toString());
	                    
	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}
	   */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		 
 		
		//  adapter = new PlacesAutoCompleteAdapter(this, R.layout.list_item);
		 
		autoCompView = (AutoCompleteTextView) findViewById(R.id.Predictions);
		// adapter = new PlacesAutoCompleteAdapter(this,android.R.layout.list_item);
		 autoCompView.setAdapter(adapter);	                          
		 
		 // Add TextChangerListener
		 autoCompView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				  if (((AutoCompleteTextView) autoCompView).isPerformingCompletion()) {
			            return;
			        } 
			        if (s.length() < 4) {
			            return;
			        }
			        
			     FetchPlacesPredictions task = new FetchPlacesPredictions();
			     task.execute(autoCompView.getText().toString());
			  //  autocomplete(query);
			    
			       				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		 
	}	
	
	 public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
	        String str = (String) adapterView.getItemAtPosition(position);
	        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	    }
	
}
