package com.example.douglasfukuharastudy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class AnotherMainActivity extends FragmentActivity {

	private AnotherMainFragment mainFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new AnotherMainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (AnotherMainFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
	}
}
