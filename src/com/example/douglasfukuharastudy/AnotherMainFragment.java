package com.example.douglasfukuharastudy;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class AnotherMainFragment extends Fragment {

	
	private static final String TAG = "AnotherMainFragment";
	
	private UiLifecycleHelper uiHelper;
	
	private TextView tvName;
	private ProfilePictureView ppViewPhoto;
	
	private Button queryButton;
	private Button multiQueryButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
	    
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.another_main_activity, container, false);
		
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
		
		tvName = (TextView) view.findViewById(R.id.userName);
		ppViewPhoto = (ProfilePictureView) view.findViewById(R.id.userProfilePicture);
		
		queryButton = (Button) view.findViewById(R.id.queryButton);
		multiQueryButton = (Button) view.findViewById(R.id.multiQueryButton);
		
		multiQueryButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {

		        String fqlQuery = "{" +
		              "'friends':'SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 25'," +
		              "'friendinfo':'SELECT uid, name, pic_square FROM user WHERE uid IN " +
		              "(SELECT uid2 FROM #friends)'," +
		              "}";
		        Bundle params = new Bundle();
		        params.putString("q", fqlQuery);
		        Session session = Session.getActiveSession();
		        Request request = new Request(session,
		            "/fql",                         
		            params,                         
		            HttpMethod.GET,                 
		            new Request.Callback(){         
		                public void onCompleted(Response response) {
		                    //Log.i(TAG, "Result: " + response.toString());
		                    parseFqlResponse(response);
		                }                  
		        }); 
		        Request.executeBatchAsync(request);                 
		    }
		});
		
		queryButton.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				String fqlQuery = "SELECT uid, name, pic_square FROM user WHERE uid IN " +
									"(SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 25)";
				Bundle params = new Bundle();
				params.putString("q", fqlQuery);
				
				Session session = Session.getActiveSession();
				Request request = new Request(session,
						"/fql",
						params,
						HttpMethod.GET,
						new Request.Callback() {
							
							@Override
							public void onCompleted(Response response) {
								// Log.i(TAG, "Result: " + response.toString());
								parseFqlResponse(response);
							}
						});
				request.executeBatchAsync(request);
			}
		});
		
		return view;
	}
	
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        
	        Request request = Request.newMeRequest(session, 
	        		new Request.GraphUserCallback() {

				  // callback after Graph API response with user object
				  @Override
				  public void onCompleted(GraphUser user, Response response) {
					  if (user != null) {
						  tvName.setText("Hello " + user.getName() + "!");
						  
						  
						  ppViewPhoto.setProfileId(user.getId());
						  ppViewPhoto.setVisibility(View.VISIBLE);
						  
						}
				  }
				});
	        request.executeAsync();
	        
	        queryButton.setVisibility(View.VISIBLE);
	        multiQueryButton.setVisibility(View.VISIBLE);
	        
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        
	        tvName.setText("Hello Dude!");
	        ppViewPhoto.setVisibility(ppViewPhoto.GONE);
	        
	        queryButton.setVisibility(View.INVISIBLE);
	        multiQueryButton.setVisibility(View.INVISIBLE);
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	public static final void parseFqlResponse(Response response) {
		try {
			
			GraphObject go = response.getGraphObject();
			
			JSONObject jso = go.getInnerJSONObject();

			
			JSONArray arr = jso.getJSONArray("data");
			
			
			Log.i(TAG, "ARR: " + arr.length());
			
			for (int i = 0; i < arr.length(); i++) {
				JSONObject json_obj = arr.getJSONObject(i);
				
				String name = json_obj.getString("name");
				Log.i(TAG, "Name: " + name);
				
			}
			
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
