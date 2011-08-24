package com.cgi.open.Utilities;

import com.cgi.open.easyshare.model.UserDetails;

public class ServiceInvoker {
	private static ThreadLocal<UserDetails> accessingUser = new ThreadLocal<UserDetails>();
	
	public static String getUserName() {
		return accessingUser.get().userName();
	}

	public static void setUserDetails(UserDetails userDetails) {
		accessingUser.set(userDetails);
	}
}
