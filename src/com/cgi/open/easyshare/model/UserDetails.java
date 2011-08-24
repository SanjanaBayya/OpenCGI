package com.cgi.open.easyshare.model;

public class UserDetails {
	private String userName;
	//private String userType;

	public UserDetails(String userName) {
		this.userName = userName;
		//this.userType = userType;
	}

	public String userName() {
		return this.userName;
	}

}
