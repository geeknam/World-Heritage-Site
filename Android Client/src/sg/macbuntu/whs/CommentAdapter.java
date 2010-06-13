package sg.macbuntu.whs;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<HashMap<String, String>>{
	
	ArrayList<HashMap<String, String>> mylist;
	
	public CommentAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> data) {
		super(context, textViewResourceId,data);
		this.mylist = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        HashMap<String,String> map = mylist.get(position);
       
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.comment_list, null);
        }
        //Check if there are any comments
        if (!map.get("email").equals("")) {
            TextView email   = (TextView) v.findViewById(R.id.tv_user_email);
            TextView comment = (TextView) v.findViewById(R.id.tv_user_comment);
            RatingBar rb     = (RatingBar)v.findViewById(R.id.rating);
            
            //Convert string to float of the rating
            float rating = Float.valueOf(map.get("stars")).floatValue();
            
            if (email != null) {
            	email.setText(map.get("email"));                           
            }
            if(comment != null){
            	comment.setText(map.get("feedback"));
            }
            if(comment != null){
            	rb.setRating(rating);
            }
        }
        else{
        	//Hide RatingBar and display No comments  
            TextView comment = (TextView) v.findViewById(R.id.tv_user_comment);
            RatingBar rb     = (RatingBar)v.findViewById(R.id.rating);
        	comment.setText("No comments");
        	rb.setVisibility(RatingBar.INVISIBLE);
        }
        return v;
	}
}
