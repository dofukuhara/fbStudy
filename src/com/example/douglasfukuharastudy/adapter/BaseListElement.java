package com.example.douglasfukuharastudy.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class BaseListElement {
	
	// member variables for the basic UI for the list element
	private Drawable icon;
	private String text1;
	private String text2;
	
	// Field that will provide a request code that is passed to new activities
	private int requestCode;
	
	private BaseAdapter adapter;
	
	public BaseListElement(Drawable icon, String text1, String text2,
			int requestCode) {
		super();
		this.icon = icon;
		this.text1 = text1;
		this.text2 = text2;
		this.requestCode = requestCode;
	}
	
	// Handle click events
	protected abstract View.OnClickListener getOnClickListener();
	
	// Handle results callbacks from a launched activity
	public void onActivityResult (Intent data) {}
	
	 // Save selected friends
	public void onSaveInstanceState (Bundle bundle) {}
	
	// Restore selected friends from a saved state
	public boolean restoreState(Bundle savedState) {
		return false;
	}
	
	// Notify observers of data changes
	protected void notifyDataChanged() {
		adapter.notifyDataSetChanged();
	}
	
	/*
	 * Getters and Setters
	 */
	
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
		
		// Notify any observers when the text changes
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
		
		// Notify any observers when the text changes
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	public Drawable getIcon() {
		return icon;
	}
	public int getRequestCode() {
		return requestCode;
	}
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}
	
}
