package sg.macbuntu.whs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class Feedback extends Activity implements TextWatcher{
	Button cancel, submit;
	EditText email, feedback;
	String emailValue,feedValue,site,rating;
	TextView tvTitle;
	ImageView ivBack;
	RatingBar rb;
	static ProgressDialog progressDialog;
	
	static final private int VIEW_FEEDBACK   = Menu.FIRST;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//Disable the default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback);
		
		//Hide autofocus keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
        Bundle b = getIntent().getExtras();
        site = b.getString("site");
        
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle.setText("Feedback: " + site);

        //Get Widgets from layout
		cancel   = (Button) findViewById(R.id.btnCancel);
		submit   = (Button) findViewById(R.id.btnSubmit);
		email    = (EditText) findViewById(R.id.etEmail);
		feedback = (EditText) findViewById(R.id.etFeedback);
		rb		 = (RatingBar) findViewById(R.id.ratingBar);
		
		//Disable submit button
		submit.setEnabled(false);

		//Go back to Menu when Cancel is clicked
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Feedback.this, Main.class);
				startActivity(i);
				finish();
			}
		});
		
		//Notify user of successful submission of feedback form when Submit button is clicked
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(!isValidEmail(emailValue)){
					Toast.makeText(getBaseContext(), "Invalid email", Toast.LENGTH_SHORT).show();
					email.requestFocus();
				}
				else{
					rating = Float.toString(rb.getRating());
					rating = rating.replace(".0", "");
					sendFeedback();
					Intent i = new Intent(Feedback.this, Main.class);
					startActivity(i);
					finish();
					Toast.makeText(getBaseContext(), "Feedback sent. Thank you for the feedback", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//Go back to Main screen when upper-right button is clicked
		ivBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Feedback.this, Main.class);
				startActivity(i);
				finish();
			}
		});
		
		
		//Keep track of user's input to enable/disable the Submit button
		feedback.addTextChangedListener(this);
		email.addTextChangedListener(this);
	}

	//Methods of implemented TextWatcher
	public void afterTextChanged(Editable arg0) {
		emailValue = email.getText().toString();
		feedValue  = feedback.getText().toString();
		
		if (emailValue.equals("") || feedValue.equals("")) {
			submit.setEnabled(false);
		}
		else{
			submit.setEnabled(true);
		}
		
	}
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Create and add new menu items.
		MenuItem exit = menu.add(0, VIEW_FEEDBACK, Menu.NONE,R.string.view_feedback);
		
		//Set icons for menu items
		exit.setIcon(android.R.drawable.ic_menu_agenda);
		
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
			case (VIEW_FEEDBACK): {
				Intent i = new Intent(getBaseContext(),ViewFeedback.class);
				i.putExtra("site", site);
				startActivity(i);
				finish();
				break;
			}
		}
		return false;
    }
	
	
	//Sending feedback to server, run the Thread to avoid the delay when switching between Activities
	private void sendFeedback(){
        new Thread() {
            public void run() {
                try{
                	WebService.postData(site,emailValue,feedValue,rating);
                } catch (Exception e) { }
            }
        }.start();
	}
	
	//Email validation
	private boolean isValidEmail(String emailAddress){
		 boolean valid;
		  //Set the email pattern string
		  Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		
		  //Match the given string with the pattern
		  Matcher m = p.matcher(emailAddress);
		
		  //check whether match is found 
		  boolean matchFound = m.matches();
		  
		  if(matchFound){
			  valid = true;
		  }
		  else{
			  valid = false;
		  }
		  
		  return valid;
	}
}
