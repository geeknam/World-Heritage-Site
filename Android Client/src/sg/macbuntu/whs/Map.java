package sg.macbuntu.whs;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;



public class Map extends MapActivity implements LocationListener{
	MapView mv; 
	MapController mc;
	LocationManager myManager;
	Drawable marker;
	MyItemizedOverlay itemizedOverlay;
	Location myLocation, destination;
	String site, location;
	double lat,lng;
	float bearing;
	
	static final private int MY_LOCATION = Menu.FIRST;
	static final private int MAPVIEW     = Menu.FIRST + 1;
	static final private int SATELLITE   = Menu.FIRST + 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map);

        
        mv = (MapView) findViewById(R.id.map);
        mv.setClickable(true);
        mv.setBuiltInZoomControls(true);
        
        mc = mv.getController();
        mc.setZoom(15);
 	    
        
        //Get site's name and geo location from the previous Intent
        Bundle b = getIntent().getExtras();
        site 	 = b.getString("site");
        location = b.getString("geo");
        mc.animateTo(getGEOPoint(location));
        

        
        //Set title in TitleBar to site's name
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
	    TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
	    tvTitle.setText("Map: " + site);
	    
		ivBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), Main.class);
				startActivity(i);
			}
		}); 
		
		//Iniztialize LocationManager and getLastKnownLcoation
        myManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
        
        //Check if GPS settings is enabled
        if (!myManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            enableGps();
        }
        else{
	        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*1000, 50, this);
	        Location initLoc = myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        
	        try{
		        lat = initLoc.getLatitude();
		        lng = initLoc.getLongitude();
		        
	        }
	        catch (Exception e) {
	        	Toast.makeText(getBaseContext(), "GPS cannot find your current location", Toast.LENGTH_LONG).show();
			}
	
	 	    getDistance(lat,lng);
        }
        
        showMyOverlay();
	}

	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void getDistance(double lat, double lng){
		//Create 2 locations
 	    myLocation  = new Location("My Location");
 	    destination = new Location("Heritage Location");
 	    
 	    //Set current Location based on the lat,lng provided
 	    myLocation.setLatitude(lat);   
 	    myLocation.setLongitude(lng); 
 	    
 	    //Set current Location based on the location string passed from previous Intent
 	    destination.setLatitude(getGEOPoint(location).getLatitudeE6()/1e6);
 	    destination.setLatitude(getGEOPoint(location).getLongitudeE6()/1e6);
 	    
 	    
 	    
 	    //Get distance and bearing between 2 locations
 	    int distance = (int) myLocation.distanceTo(destination)/1000;
 	    bearing = myLocation.bearingTo(destination);
 	    
 	    //Make a Toast to show the distance
 	    String toastString = "Distance: " + distance + "km \nBearing : " + bearing;
 	    Toast.makeText(getBaseContext(), toastString, Toast.LENGTH_LONG).show();
	}
	
	private void enableGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.gps_alert)
       .setCancelable(false)
       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface arg0, int arg1) {
    		   launchGPSOptions();
    	}});
	    
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	//Launch the GPS preferences panel
    private void launchGPSOptions() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }  
	
	//Convert location from String to GeoPoint
	private GeoPoint getGEOPoint(String gPoint){
 	   String[] gPointSplit = gPoint.split(",");	
	   double gLat=Double.parseDouble(gPointSplit[0]);
	   double gLng=Double.parseDouble(gPointSplit[1]);
	   GeoPoint selGeoPoint=new GeoPoint((int)(gLat*1E6),(int)(gLng*1E6));
	   return selGeoPoint;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Create and add new menu items.
		MenuItem myLocation = menu.add(0, MY_LOCATION, Menu.NONE,
				R.string.my_location);
		MenuItem mapView = menu.add(0, MAPVIEW, Menu.NONE,
				R.string.map_view);
		MenuItem satelliteView = menu.add(0, SATELLITE, Menu.NONE,
				R.string.satellite_view);
		
		
		//Set icons for menu items
		mapView.setIcon(android.R.drawable.ic_menu_mapmode);
		satelliteView.setIcon(android.R.drawable.ic_menu_compass);
		myLocation.setIcon(android.R.drawable.ic_menu_mylocation);
		
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
			case (MY_LOCATION): {
				GeoPoint p = new GeoPoint((int)(lat*1E6),(int)(lng*1E6));
		        mc.animateTo(p);
				break;
			}
			case (MAPVIEW): {
				mv.setSatellite(false);
				break;
			}
			case (SATELLITE): {
				mv.setSatellite(true);
				break;
			}
		}
		return false;
    }
	
	
	private void showMyOverlay() {
		//Get market icon
		marker = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedOverlay = new MyItemizedOverlay(marker, this);
		
		//Initialize OverlayItem with site's GEOPoint and its name
		OverlayItem overlayItem = new OverlayItem(getGEOPoint(location), "Site" ,site);
		itemizedOverlay.addOverlay(overlayItem);

		mv.getOverlays().add(itemizedOverlay);
	}

	
	public void onLocationChanged(Location loc) {
		lat = loc.getLatitude();
		lng = loc.getLongitude();
		
		//Show distance Toast when Location is changed
		getDistance(lat, lng);
	}


	public void onProviderDisabled(String arg0) {
		Toast.makeText(getBaseContext(), "GPS is disabled", Toast.LENGTH_LONG).show();
	}
	public void onProviderEnabled(String arg0) {
		Toast.makeText(getBaseContext(), "GPS is enabled", Toast.LENGTH_LONG).show();
	}
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

}
