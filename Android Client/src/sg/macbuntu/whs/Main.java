package sg.macbuntu.whs;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ListActivity {
	ArrayList<HeritageSite> heritageArray;
	ArrayList<String> geoArray,sitesArray,urlArray;
	ArrayAdapter<String> aa;
	ImageView ivHelp;
	
	//Menu indices
	static final private int EXIT   = Menu.FIRST;
	static final private int ABOUT  = Menu.FIRST + 1;
	static final private int REPORT = Menu.FIRST + 2;

	//ContextMenu indices
	static final private int WEB_VIEW      = Menu.FIRST;
	static final private int MAP_VIEW      = Menu.FIRST + 1;
	static final private int SEND_FEEDBACK = Menu.FIRST + 2;
	static final private int VIEW_FEEDBACK = Menu.FIRST + 3;
	static final private int DELETE        = Menu.FIRST + 4;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//Disable default title bar
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.main);
	    
	    TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
	    ivHelp = (ImageView) findViewById(R.id.ivBack);
	    
	    //Set image of the upper-right button
	    tvTitle.setText("World Heritage Site");
	    ivHelp.setImageResource(android.R.drawable.ic_menu_help);
	    
	    //Show HELP dialog when clicking upper-right button
	    ivHelp.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showDialog("help");
			}
		});
	    
	    
		populateSitesData();
		
		//Bind ListView with ArrayAdapter
		ListView sitesList = getListView();
		aa = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sitesArray);
		sitesList.setAdapter(aa);
		
		registerForContextMenu(sitesList);
		
	}

	//Show map when item is selected (tapped on)
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		showMap(position);
	}

	//Show website of the site
	private void showWeb(int index) {
		String url = heritageArray.get(index).getUrl();
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	//Show feedback form of the site
	private void showFeedback(boolean form,int index){
		if(form){
			Intent i = new Intent(Main.this, Feedback.class);
	        i.putExtra("site", heritageArray.get(index).getName());
	        startActivity(i);
		}
		else{
			Intent i = new Intent(Main.this, ViewFeedback.class);
	        i.putExtra("site", heritageArray.get(index).getName());
	        startActivity(i);
		}

	}
	
	//Show site on map
	private void showMap(int index){
        Intent i = new Intent(Main.this, Map.class);
        i.putExtra("site", heritageArray.get(index).getName());
        i.putExtra("geo", heritageArray.get(index).getGeo());
        startActivity(i);
	}
	
	//Delete site from the list
	private void deleteSite(int index){
		Toast.makeText(getBaseContext(), "'" + sitesArray.get(index) + "' has been deleted from list", Toast.LENGTH_SHORT).show();
		sitesArray.remove(index);
		heritageArray.remove(index);
		aa.notifyDataSetChanged();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Options");
		menu.add(0, WEB_VIEW, Menu.NONE, R.string.webView);
		menu.add(0, MAP_VIEW, Menu.NONE, R.string.mapView);
		menu.add(0, SEND_FEEDBACK, Menu.NONE, R.string.send_feedback);
		menu.add(0, VIEW_FEEDBACK, Menu.NONE, R.string.view_feedback);
		menu.add(0, DELETE, Menu.NONE, R.string.delete);
	}

	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);

		// Get the position of the selected item
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int index = menuInfo.position;

		switch (item.getItemId()) {
			case (WEB_VIEW): {
				showWeb(index);
				break;
			}
			case (MAP_VIEW): {
				showMap(index);
				break;
			}
			case(SEND_FEEDBACK):{
				showFeedback(true,index);
				break;
			}
			case(VIEW_FEEDBACK):{
				showFeedback(false,index);
				break;
			}
			case(DELETE):{
				deleteSite(index);
				break;
			}

		}
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Create and add new menu items.
		MenuItem exit = menu.add(0, EXIT, Menu.NONE,
				R.string.menu_close);
		MenuItem about = menu.add(0, ABOUT, Menu.NONE,
				R.string.menu_about);
		MenuItem report = menu.add(0, REPORT, Menu.NONE,
				R.string.menu_report);
		
		//Set icons for menu items
		exit.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		about.setIcon(android.R.drawable.ic_menu_info_details);
		report.setIcon(android.R.drawable.ic_menu_send);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
			//Exit application
			case (EXIT): {
				finish();
				break;
			}
			//Show about information
			case (ABOUT): {
				showDialog("about");
				break;
			}
			//Report a bug via Email
			case (REPORT): {
				sendEmail();
				break;
			}
		}
		return false;
    }
	
	//======= Populate sites data with name, geo location and url =======//
	private void populateSitesData(){
		heritageArray = new ArrayList<HeritageSite>();
		sitesArray = new ArrayList<String>();
		geoArray = new ArrayList<String>();
		urlArray = new ArrayList<String>();
		
		//Get arrays from site.xml
		String [] sitesTemp = this.getResources().getStringArray(R.array.sites);
		String [] geoTemp   = this.getResources().getStringArray(R.array.geo);
		String [] urlTemp   = this.getResources().getStringArray(R.array.url);

		
		for(int i=0; i<sitesTemp.length;i++){
			sitesArray.add(sitesTemp[i]);
			geoArray.add(geoTemp[i]);
			urlArray.add(urlTemp[i]);
			//Construct HeritageSite and add it to heritageArray
			heritageArray.add(new HeritageSite(sitesArray.get(i), urlArray.get(i), geoArray.get(i)));
		}
	}
	
	//Show either HELP or ABOUT dialog based on the type parameter
	private void showDialog(String type){
		String message = "";
		Builder builder = new AlertDialog.Builder(this);
		
		if(type.equals("about")){
			message = "Assignment for C345 module \n \n" +
			 		  "Application: World Heritage Site \n \n" +
			 		  "Developer: Ngo Minh Nam \n \n" +
			 		  "Copyright 2010. All rights reserved";
			builder.setTitle(R.string.menu_about);
		}
		else if(type.equals("help")){
			message = "1. Choose heritage from the list to view the map  \n \n" +
			 		  "2. Tap on the icon on the map to comment \n \n" +
			 		  "3. Long press on the heritage for all options";
			builder.setTitle(R.string.help);
		}
		
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
	}
	
	//Send a email to report a bug
	private void sendEmail(){
		/* Create the Intent */
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		/* Fill it with Data */
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"emoinrp@gmail.com"});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Report a bug");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "The bug is as following: ");

		/* Send it off to the Activity-Chooser */
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}
}