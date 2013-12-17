package com.example.douglasfukuharastudy.fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglasfukuharastudy.PickerActivity;
import com.example.douglasfukuharastudy.R;
import com.example.douglasfukuharastudy.ScrumptiousApplication;
import com.example.douglasfukuharastudy.adapter.ActionListAdapter;
import com.example.douglasfukuharastudy.adapter.BaseListElement;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class SelectionFragment extends Fragment{
	
	// Debugging TAG information
	private static final String TAG = "SelectionFragment";
	
	// Douglas Fukuhara - debug
	private int count; 
	
	// Variable used to decide whether to update a session's info in the onActivityResult() method
	private static final int REAUTH_ACTIVITY_CODE = 100;

	// User's profile picture and name
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	
	// ListView variables
	private ListView listView;
	private List<BaseListElement> listElements;
	
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

		// Douglas Fukuhara - debug
		count = 0;
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
		
		// Find the list view
		listView = (ListView) view.findViewById(R.id.selection_list);
		
		// Set up the list view items, based on a list of BaseListElements items
		listElements = new ArrayList<BaseListElement>();
		// Add an item for the friend picker
		listElements.add(new PeopleListElement(0));
		// Set the list view adapter
		listView.setAdapter(new ActionListAdapter(getActivity(),
				R.id.selection_list, listElements));
		
		// restore the list items' state after they populte
		listElements.add(new PeopleListElement(0));
		if (savedInstanceState != null) {
			// Restore the state of each list element
			for (BaseListElement listElement : listElements) {
				listElement.restoreState(savedInstanceState);
			}
		}
		
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
			// Call the corresponding UiLifecycleHelper method if the REAUTH_ACTIVITY_CODE request is passed in
			uiHelper.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_OK && 
		        requestCode >= 0 && requestCode < listElements.size()) {
		    listElements.get(requestCode).onActivityResult(data);
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
		
		// Save the list element values
		for (BaseListElement listElement : listElements) {
			listElement.onSaveInstanceState(bundle);
		}
		
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

						// Douglas Fukuhara - debug
						count++;
						Toast.makeText(getActivity().getApplicationContext(), "Count: " + count, Toast.LENGTH_SHORT).show();
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
	
	
	
	
	// Inner private class that represents the friend picker list item
	public class PeopleListElement extends BaseListElement {
		
		// Private variable for the selected friends
		private List<GraphUser> selectedUsers;
		
		// Key for the bundle by setting a private variable
		private static final String FRIENDS_KEY = "friends";

		public PeopleListElement(int requestCode) {
			super(getActivity().getResources().getDrawable(R.drawable.action_people), 
					getActivity().getResources().getString(R.string.action_people),
					getActivity().getResources().getString(R.string.action_people_default),
					requestCode);
		}

		@Override
		protected View.OnClickListener getOnClickListener() {
			return new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startPickerActivity(PickerActivity.FRIEND_PICKER, getRequestCode());
					
				}
			};
		}
		
		@Override
		public void onActivityResult(Intent data) {
			selectedUsers = ((ScrumptiousApplication) getActivity()
					.getApplication())
					.getSelectedUsers();
			
			setUserText();
			notifyDataChanged();
		}
		
		@Override
		public void onSaveInstanceState(Bundle bundle) {
			if (selectedUsers != null) {
				bundle.putByteArray(FRIENDS_KEY, getByteArray(selectedUsers));
			}
		}
		
		@Override
		public boolean restoreState(Bundle savedState) {
			byte[] bytes = savedState.getByteArray(FRIENDS_KEY);
			if (bytes != null) {
				selectedUsers = restoreByteArray(bytes);
				setUserText();
				return true;
			}
			return false;
		}
		
		private void setUserText() {
			String text = null;
			
			if (selectedUsers != null) {
				// If there is one friend
				if  (selectedUsers.size() == 1) {
					text = String.format(getResources().getString(R.string.single_user_selected),
							selectedUsers.get(0).getName());
				} else if (selectedUsers.size() == 2) {
					// If there are two friends
					text = String.format(getResources().getString(R.string.two_users_selected), 
							selectedUsers.get(0).getName(),
							selectedUsers.get(1).getName());
				}else if (selectedUsers.size() > 2) {
					text = String.format(getResources().getString(R.string.multiple_users_selected), 
							selectedUsers.get(0).getName(),
							(selectedUsers.size() - 1));
				}
			}
			
			if (text == null) {
				// If no text, use the placeholder text
				text = getResources().getString(R.string.action_people_default);
			}
			// Set the text in list element. THis will notify the adapter that the data
			// has changed to refresh the list view.
			setText2(text);
		}
		
		private byte[] getByteArray(List<GraphUser> users) {
			// Convert the list of GraphUers to a list of String where each element in JSON
			// representation of the GraphUser so it can be stored in a Bundle
			List<String> userAsString = new ArrayList<String>(users.size());
			
			for (GraphUser user : users) {
				userAsString.add(user.getInnerJSONObject().toString());
			}
			try {
				ByteArrayOutputStream outputStram = new ByteArrayOutputStream();
				new ObjectOutputStream(outputStram).writeObject(userAsString);
				return outputStram.toByteArray();
			} catch (IOException e) {
				Log.e(TAG, "Unable to serialize users.", e);
			}
			
			return null;
		}
		
		private List<GraphUser> restoreByteArray(byte[] bytes) {
			try {
				@SuppressWarnings("unchecked")
				List<String> usersAsString = 
						(List<String>) (new ObjectInputStream
										(new ByteArrayInputStream(bytes)))
										.readObject();
				if (usersAsString != null) {
					List<GraphUser> users = new ArrayList<GraphUser>(usersAsString.size());
					
					for (String user : usersAsString) {
						GraphUser graphUser = GraphObject.Factory
								.create(new JSONObject(user), GraphUser.class);
						users.add(graphUser);
					}
					return users;
				}
			} catch (ClassNotFoundException e) {
		        Log.e(TAG, "Unable to deserialize users.", e); 
		    } catch (IOException e) {
		        Log.e(TAG, "Unable to deserialize users.", e); 
		    } catch (JSONException e) {
		        Log.e(TAG, "Unable to deserialize users.", e); 
		    }
			return null;
		}
	}
	
	private void startPickerActivity(Uri data, int requestCode) {
	     Intent intent = new Intent();
	     intent.setData(data);
	     intent.setClass(getActivity(), PickerActivity.class);
	     startActivityForResult(intent, requestCode);
	 }
}
