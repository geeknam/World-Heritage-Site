package sg.macbuntu.whs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebService {
	
	static String getUrl   = "http://androidnam.appspot.com/get/";   //"http://rpshi.zzl.org/android/get.php";
	static String storeUrl = "http://androidnam.appspot.com/store"; //"http://rpshi.zzl.org/android/post.php"; 
	
	public static ArrayList<HashMap<String, String>> getData(String place){
		
		ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map ;
		
		//Encode url to avoid spaces in places
		place = URLEncoder.encode(place);
		place = place.replace("+", "%20");
		String jsonReturn = getUrl + place ; //"?place="+place;

		JSONObject jsonResponse = getJSONResponse(jsonReturn);	
		
		//Show no comments if json response is empty
		if(jsonResponse == null){
			map = new HashMap<String, String>();
			map.put("email",    "");
			map.put("feedback", "No comments");
			map.put("stars",    "0");
			dataList.add(map);
		}
		else{
			//Iterate through json response 
			Iterator<?> it = jsonResponse.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				try {
					Object comment = jsonResponse.get(key);
					//Add data to Hashmap
					map = new HashMap<String, String>();
					map.put("email",    ((JSONObject) comment).get("user").toString());
					map.put("feedback", ((JSONObject) comment).get("comment").toString());
					map.put("stars",    ((JSONObject) comment).get("star").toString());
					//Add Hashmap to ArrayList
					dataList.add(map);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return dataList;
	}
	
	public static void postData(String heritage, String email, String comment, String star) {
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(storeUrl);
	    
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("heritage", heritage));
	        nameValuePairs.add(new BasicNameValuePair("email", email));
	        nameValuePairs.add(new BasicNameValuePair("comment", comment));
	        nameValuePairs.add(new BasicNameValuePair("star", star));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        Log.i("tag", nameValuePairs.toString() + response.getStatusLine());
	        
	    } 
	    catch (ClientProtocolException e) {} 
	    catch (IOException e) {}
	    
	} 
	
	
	
	private static String convertStreamToString(InputStream is) {
		Charset ch = Charset.forName("UTF8");
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, ch));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static JSONObject getJSONResponse(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				JSONObject json = new JSONObject(result);
				instream.close();

				return json;
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
