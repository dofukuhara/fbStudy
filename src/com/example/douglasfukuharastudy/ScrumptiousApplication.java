package com.example.douglasfukuharastudy;

import java.util.List;

import android.app.Application;

import com.facebook.model.GraphUser;

public class ScrumptiousApplication extends Application {
	
	// Private variable to store the selected friends
	private List<GraphUser> selectedUsers;

	
	/*
	 * Getters and setters
	 */
	
	public List<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<GraphUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	

}
