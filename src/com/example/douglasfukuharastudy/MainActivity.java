package com.example.douglasfukuharastudy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class MainActivity extends Activity {

	private ProfilePictureView profilePictureView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
		
		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {
		
			// callback when session changes state
			@SuppressWarnings("deprecation")
			@Override
			public void call(final Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					// make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

					  // callback after Graph API response with user object
					  @Override
					  public void onCompleted(GraphUser user, Response response) {
						  if (user != null) {
							  TextView welcome = (TextView) findViewById(R.id.welcome);
							  welcome.setText("Hello " + user.getName() + "!");
							  
							  
							  profilePictureView.setProfileId(user.getId());
							  
							}
					  }
					});
				}
		    }

		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

}
