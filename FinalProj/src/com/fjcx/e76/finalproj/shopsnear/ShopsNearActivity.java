package com.fjcx.e76.finalproj.shopsnear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.pantry.AddPantryItemActivity;
import com.fjcx.e76.finalproj.pantry.PantryActivity;
import com.fjcx.e76.finalproj.pantry.UpdatePantryItemActivity;
import com.fjcx.e76.finalproj.recipe.FindRecipesActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ShopsNearActivity  extends FragmentActivity implements LocationListener {
	
	ImageButton homeMenuBtn;
	private DatabaseHandler dbHandler;
	Context _context;
	ListView snListView;
	GoogleMap mGoogleMap;	
	Spinner mSprPlaceType;		
	String[] mPlaceType=null;
	String[] mPlaceTypeName=null;
	double mLatitude=0;
	double mLongitude=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shopsnear);
		_context = this;
		addListenerOnButton();
		dbHandler =  new DatabaseHandler(this);
		
		// populate list with shops too!!
		snListView = (ListView)findViewById(R.id.shopsnear_list);
		
		// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
        	int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }else { // Google Play Services are available
	    	// Getting reference to the SupportMapFragment
	    	SupportMapFragment fragment = ( SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
	    			
	    	// Getting Google Map
	    	mGoogleMap = fragment.getMap();
	    			
	    	// Enabling MyLocation in Google Map
	    	mGoogleMap.setMyLocationEnabled(true);
	    	
	    	// Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
            	onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 6000, 0, this);
	    	
	    	// Setting up the grocery store query search	
			StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
			sb.append("location="+mLatitude+","+mLongitude);
			sb.append("&radius=1000");
			sb.append("&types=grocery_or_supermarket|convenience_store");
			sb.append("&sensor=true");
			sb.append("&key=AIzaSyBQmV9vDc7SA15y-uZVl7oi8b_Ozwoy8OQ");
			
			// Creating a new non-ui thread task to download Google place json data 
	        PlacesTask placesTask = new PlacesTask();		        			        
	        
			// Invokes the "doInBackground()" method of the class PlaceTask
	        placesTask.execute(sb.toString());
					
        }
	}
	
	public void addListenerOnButton() {
		final Context context = this;
		
		homeMenuBtn = (ImageButton) findViewById(R.id.shops_menu_btn);
		homeMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, MainActivity.class);
			    startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// get json result from url
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);                
            
            // Creating an http connection to communicate with url 
            urlConnection = (HttpURLConnection) url.openConnection();                

            // Connecting to url 
            urlConnection.connect();                

            // Reading data from url 
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while((line = br.readLine())  != null){
                    sb.append(line);
            }

            data = sb.toString();
            br.close();
        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
    }         

	// get google places
	private class PlacesTask extends AsyncTask<String, Integer, String>{
		String data = null;
		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				 Log.d("Background Task",e.toString());
			}
			return data;
		}
		
		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result){			
			ParserTask parserTask = new ParserTask();
			// Parse some places
			parserTask.execute(result);
		}
		
	}
	
	// parse places task
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		JSONObject jObject;
		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String,String>> doInBackground(String... jsonData) {
			List<HashMap<String, String>> places = null;			
			ShopsJSONParser placeJsonParser = new ShopsJSONParser();
	        try{
	        	jObject = new JSONObject(jsonData[0]);
	            places = placeJsonParser.parse(jObject);
	        }catch(Exception e){
	                Log.d("Exception",e.toString());
	        }
	        return places;
		}
		
		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String,String>> list){			
			// set values from shops nearby list
			String[] from = new String[] {"place_name"};
			int[] to = new int[] {R.id.item1};
			if(list!=null){
				SimpleAdapter adapter = new SimpleAdapter(_context, list, R.layout.shoplistitem, from, to);
				snListView.setAdapter(adapter);
				
				registerForContextMenu(snListView);
				snListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					    ((Activity) _context).openContextMenu(view);
					}
				});
				
				// Clears all the existing markers 
				mGoogleMap.clear();
				
				for(int i=0;i<list.size();i++){
				
					// Creating a marker
		            MarkerOptions markerOptions = new MarkerOptions();
		            
		            // Getting a place from the places list
		            HashMap<String, String> hmPlace = list.get(i);
		
		            // Getting latitude of the place
		            double lat = Double.parseDouble(hmPlace.get("lat"));	            
		            
		            // Getting longitude of the place
		            double lng = Double.parseDouble(hmPlace.get("lng"));
		            
		            // Getting name
		            String name = hmPlace.get("place_name");
		            
		            // Getting vicinity
		            String vicinity = hmPlace.get("vicinity");
		            LatLng latLng = new LatLng(lat, lng);
		            
		            // Setting the position for the marker
		            markerOptions.position(latLng);
		
		            // Setting the title for the marker. 
		            //This will be displayed on taping the marker
		            markerOptions.title(name + " : " + vicinity);	            
		
		            // Placing a marker on the touched position
		            mGoogleMap.addMarker(markerOptions);            
	            
				}
			}else{
				// throw message up
			}
		}
	}
	

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	
	 @Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.layout.shopnearcontextmenu, menu);
		menu.setHeaderTitle("Select an Option");
	}
	 
	 @Override
	 public boolean onContextItemSelected(MenuItem item) {
		 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		 TextView rowViewItem1 = (TextView)info.targetView.findViewById(R.id.item1);
		 String selectedRowName = rowViewItem1.getText().toString();
		 
		 switch(item.getItemId()) {
			
			case R.id.snmenu_addtoslist:
				if(selectedRowName!=null){
					Toast.makeText(ShopsNearActivity.this,"Update Item Details!",Toast.LENGTH_SHORT ).show();
					Intent intentup = new Intent(this, UpdatePantryItemActivity.class);
					Bundle bundleup = new Bundle();
					if(selectedRowName!=null){
						bundleup.putString("updateStore", selectedRowName.trim());
					}
					bundleup.putString("prevPage", "shopsnear");
	                // pass file prevpage to know where to return to
	                intentup.putExtras(bundleup);
				    startActivity(intentup);
					return true;
				}
				return true;
		}
		return super.onContextItemSelected(item);
	}

}
