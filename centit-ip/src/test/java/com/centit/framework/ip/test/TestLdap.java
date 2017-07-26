package com.centit.framework.ip.test;

import com.centit.framework.ip.service.impl.ActiveDirectoryUserDirectoryImpl;

public class TestLdap {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ActiveDirectoryUserDirectoryImpl().synchroniseUserDirectory();
	}

}
