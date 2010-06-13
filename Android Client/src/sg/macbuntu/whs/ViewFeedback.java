package sg.macbuntu.whs;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewFeedback extends ListActivity{
	ImageView ivBack;
	ArrayList<HashMap<String, String>> commentList;
	String site;
	ProgressDialog dialog;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Disable the default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_feedback);
		
        Bundle b = getIntent().getExtras();
        site = b.getString("site");
		
        ivBack = (ImageView) findViewById(R.id.ivBack);
	    TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
	    tvTitle.setText("Comments: " + site);
	    
	    
	    //Get comments parsed from the server
	    commentList = WebService.getData(site);
	  
	    ListView list = getListView();
	    
	    //bind ListView with CommentAdapter with custom layout
	    CommentAdapter ca = new CommentAdapter(this, R.layout.comment_list, commentList);
	    list.setAdapter(ca);
	    
	    
	    //Go back to Main screen when upper-right button is clicked
		ivBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), Main.class);
				startActivity(i);
				finish();
			}
		});   
	}
}
