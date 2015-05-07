package com.example.silentphone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

//import android.app.ActionBar;
import android.app.Dialog;
import android.app.PendingIntent;
//import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.csc.GeocodeJSONParser;
//import com.example.csc.LocationsDB;
//import com.example.csc.MainActivity.LocationInsertTask;
//import com.example.csc.MainActivity.ParserTask;
//import com.example.csc.R;
//import com.example.csc.MainActivity.DownloadTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
//import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	
	GoogleMap googleMap;
    LocationManager locationManager;
    PendingIntent pendingIntent;
    SharedPreferences sharedPreferences;
    SharedPreferences sp;
    Button ok;
    int fl=0;
    Button mBtnFind;
    EditText etPlace;
    int locationCount = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		//ActionBar actionBar = getActionBar();
		//actionBar.setLogo(R.drawable.icon);
		//actionBar.setDisplayUseLogoEnabled(true);
		//actionBar.setDisplayShowHomeEnabled(true);
		/***********************************************************/
		sp=getSharedPreferences("flag", 0);
		fl=sp.getInt("fl", 0);
		
		setContentView(R.layout.activity_main);
		if(fl==0)
		{
			fl=1;
			SharedPreferences.Editor edit=sp.edit();
        	edit.putInt("fl", fl);
        	edit.commit();
			Intent i = new Intent("com.example.silentphone.Help");
			startActivity(i);
		}
		
		
		/**********************************************************/
		
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if(status!=ConnectionResult.SUCCESS){ 

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else {   
        	
        	mBtnFind = (Button) findViewById(R.id.btn_show);
            etPlace = (EditText) findViewById(R.id.et_place);
            
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            googleMap = fm.getMap();
            googleMap.setMyLocationEnabled(true);  
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            
    		sharedPreferences = getSharedPreferences("location", 0);
    		
            locationCount = sharedPreferences.getInt("locationCount", 0);    		
    		
    		String zoom = sharedPreferences.getString("zoom", "0");    		
    		
            if(locationCount!=0){

                    String lat = "";
                    String lng = "";

                    for(int i=0;i<locationCount;i++){

                            lat = sharedPreferences.getString("lat"+i,"0");
                            lng = sharedPreferences.getString("lng"+i,"0");
                            
                            
                            if(lat!="0" && lng!="0")
                            {drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),i+1);
                            drawCircle(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                    
                            }
                    }        

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
            }   		
    		
            
            googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng point) {				 
					
					locationCount++;
					
					drawMarker(point,locationCount);
					drawCircle(point);					
					
			        Intent proximityIntent = new Intent("com.example.activity.proximity");
			        proximityIntent.putExtra("lat",point.latitude);
			        proximityIntent.putExtra("lng", point.longitude);
					pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
			        
					locationManager.addProximityAlert(point.latitude, point.longitude, 100, -1, pendingIntent);		        
			        
					SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(point.latitude));
                    editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(point.longitude));
                    editor.putInt("locationCount", locationCount);
			        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));		        
			        editor.commit();		        
			        
			        Toast.makeText(getBaseContext(), "Proximity Alert is added", Toast.LENGTH_SHORT).show();			        
			        
				}
			});    
            
            googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {				
				@Override
				public void onInfoWindowClick(Marker point) {
					Intent proximityIntent = new Intent("com.example.activity.proximity");
					pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
					locationManager.removeProximityAlert(pendingIntent);
					String k=point.getTitle();
					k=k.substring(0,1);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					int k1=Integer.parseInt(k)-1;
					editor.remove("lat"+k1);
					editor.remove("lng"+k1);
					
					editor.commit();
					
					point.remove();
					
					Toast.makeText(getBaseContext(), "Proximity Alert is removed", Toast.LENGTH_LONG).show();
				}
			});   
            
               mBtnFind.setOnClickListener(new OnClickListener() {
        
                   @Override
                   public void onClick(View v) {
                       String location = etPlace.getText().toString();
        
                       if(location==null || location.equals("")){
                           Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                           return;
                       }
        
                       String url = "https://maps.googleapis.com/maps/api/geocode/json?";
        
                       try {
                           location = URLEncoder.encode(location, "utf-8");
                       } catch (UnsupportedEncodingException e) {
                           e.printStackTrace();
                       }
        
                       String address = "address=" + location;
        
                       String sensor = "sensor=false";
                       url = url + address + "&" + sensor;
                       DownloadTask downloadTask = new DownloadTask();
                       downloadTask.execute(url);
                   }
               });
               
		}
		
	}
	
	
	private void drawCircle(LatLng point){
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(point);
		circleOptions.radius(20);
		circleOptions.strokeColor(Color.BLACK);
		circleOptions.fillColor(0x30ff0000);
		circleOptions.strokeWidth(2);
		googleMap.addCircle(circleOptions);
		
	}	
	
	private void drawMarker(LatLng point,int sr){
		MarkerOptions markerOptions = new MarkerOptions();					
		markerOptions.position(point);
		markerOptions.title(sr+".Location Coordinates");		
		markerOptions.snippet(Double.toString(point.latitude) + "," + Double.toString(point.longitude));		
		googleMap.addMarker(markerOptions);
		
	}
	
	
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
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
	
    private class DownloadTask extends AsyncTask<String, Integer, String>{
 
        String data = null;
 
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        @Override
        protected void onPostExecute(String result){
 
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    
    
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        JSONObject jObject;
 
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                places = parser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
 
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
 
            for(int i=0;i<list.size();i++){
 
                MarkerOptions markerOptions = new MarkerOptions();
    
                HashMap<String, String> hmPlace = list.get(i);
                double lat = Double.parseDouble(hmPlace.get("lat"));
                double lng = Double.parseDouble(hmPlace.get("lng"));
                String name = hmPlace.get("formatted_address");
                locationCount++;
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(locationCount+"."+name);
                markerOptions.snippet("Tap here to remove this marker");
                googleMap.addMarker(markerOptions);
 				
				drawCircle(latLng);					

				Intent proximityIntent = new Intent("com.example.activity.proximity");
		        proximityIntent.putExtra("lat",latLng.latitude);
		        proximityIntent.putExtra("lng", latLng.longitude);
				pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
		        locationManager.addProximityAlert(latLng.latitude, latLng.longitude, 100, -1, pendingIntent);		        
		        SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(latLng.latitude));
                editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(latLng.longitude));
                editor.putInt("locationCount", locationCount);
		        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));		        
		        editor.commit();		        
		       			        
	            if(i==0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int Itemid = item.getItemId();
		
		if(Itemid == R.id.help)
		{
			Intent i = new Intent("com.example.silentphone.Help");
			startActivity(i);
		}
		else if(Itemid == R.id.action_settings)
		{
			//Intent p = new Intent(Settings.ACTION_SYNC_SETTINGS);
			Intent q=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(q);
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
