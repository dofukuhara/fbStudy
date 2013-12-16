package com.example.douglasfukuharastudy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class NewMainActivity extends FragmentActivity {
	
	// Array Indexes used to manipulate these fragments
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS +1;
	
	// Array of fragments
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	// Flag that indicates a visible activity
	private boolean isResumed = false;
	
	// Menu item for the settings fragment
	private MenuItem settings;
	
	// Track the session and trigger a session state change listener
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
			new Session.StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					onSessionStateChange(session, state, exception);
				}
			};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Created an instance of UiLifecycleHelper and passed in the listener
		uiHelper = new UiLifecycleHelper(this, callback);
		// Calling needed methods to properly keep track of the session
		uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
		
		FragmentTransaction transaction = fm.beginTransaction();
		for(int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Calling needed methods to properly keep track of the session
		uiHelper.onResume();
		// Flag that indicates a visible activity
		isResumed = true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Calling needed methods to properly keep track of the session
		uiHelper.onPause();
		// Flag that indicates a hidden activity
		isResumed = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Calling needed methods to properly keep track of the session
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Calling needed methods to properly keep track of the session
		uiHelper.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		// Calling needed methods to properly keep track of the session
		uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResumeFragments() {
		// Handle the cases where fragments are newly instantiated and the authenticated
		// versus nonauthenticated UI needs to be properly set.
		super.onResumeFragments();
		Session session = Session.getActiveSession();
		
		if (session != null && session.isOpened()) {
			// if the session is already opened,
			// try to show the selection fragment
			showFragment(SELECTION, false);
		} else {
			// otherwise present the splash screen
			// and ask the person to login.
			showFragment(SPLASH, false);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		// only add the menu when the selection fragment is showing
		if (fragments[SELECTION].isVisible()) {
			if (menu.size() == 0) {
				settings = menu.add(R.string.settings);
			}
			return true;
		} else { 
			menu.clear();
			settings = null;
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)) {
			showFragment(SETTINGS, true);
			return true;
		}
		return false;
	}
	
	// Method responsible for showing a given fragment and hiding all other fragments
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
	
	// This method will be called due to session state changes
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		// Only make changes if the activity is visible
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			
			// Clear the back stack
			for (int i = 0; i < backStackSize ; i++) {
				manager.popBackStack();
			}
			
			if (state.isOpened()) {
				// If the session state is open:
				// Show the authenticated fragment
				showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				// If the session state is closed:
				// Show the login fragment
				showFragment(SPLASH, false);
			}
		}
	}

}
