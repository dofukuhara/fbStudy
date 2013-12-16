package com.example.douglasfukuharastudy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class SelectionFragment extends Fragment{
	
	// Debugging TAG information
	private static final String TAG = "SelectionFragment";
	
	// Variable used to decide whether to update a session's info in the onActivityResult() method
	private static final int REAUTH_ACTIVITY_CODE = 100;

	// User's profile picture and name
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	
	// UiLifecycle object and the Session.StatusCallback listener implementation
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize the UiLifecycleHelper object and call it's onCreate() method
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.selection, container, false);
		
		// Find the user's profile picture custom view
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		profilePictureView.setPresetSize(profilePictureView.SMALL);
		
		// Find the user's name view
		userNameView = (TextView) view.findViewById(R.id.selection_user_name);
		
		// Check for an open session
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
		}
		
		return view;		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			// Call te corresponding UiLifecycleHelper method if the REAUTH_ACTIVITY_CODE request is passed in
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// make sure all other the fragment lifecycle methods call the relevant methods in the UiLifecycleHelper class:
		uiHelper.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		// make sure all other the fragment lifecycle methods call the relevant methods in the UiLifecycleHelper class:
		uiHelper.onSaveInstanceState(bundle);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// make sure all other the fragment lifecycle methods call the relevant methods in the UiLifecycleHelper class:
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// make sure all other the fragment lifecycle methods call the relevant methods in the UiLifecycleHelper class:
		uiHelper.onDestroy();
	}
	
	// Method to request the user's data
	private void makeMeRequest (final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the request is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						// Set the id for the ProfilePictureView
						// view that in turn display the profile picture.
						profilePictureView.setProfileId(user.getId());
						
						// Set the Textview's text to the user's name
						userNameView.setText(user.getName());
					}
				}
				if (response.getError() != null) {
					// Handle errors, will do so later.
				}
			}
		});
		request.executeAsync();
	}
	
	// Method that will respond to session changes and call the makeMeRequest() method if the session is open
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
		}
	}
}
