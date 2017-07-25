package com.centit.product.ip.test;

import com.centit.product.ip.service.impl.ActiveDirectoryUserDirectoryImpl;

public class TestLdap {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ActiveDirectoryUserDirectoryImpl().synchroniseUserDirectory();
	}

}
