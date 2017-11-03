package com.centit.framework.ip.app.test;

import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.support.security.AESSecurityUtils;

public class TestPlatSystem {
	
	public static void main(String[] args) {
		System.out.println(encodeDatabasePassword("1315_Com"));
		System.out.println(encodeDatabasePassword("bizdata"));
		System.out.println(decodeDatabasePassword(
				encodeDatabasePassword("bizdata")));
		// TODO Auto-generated method stub
		/*IPClientPlatformEnvironment clientSytem = new IPClientPlatformEnvironment();
		clientSytem.setPlatServerUrl("http://productsvr.centit.com:8880/centit-ip/service/platform");
		clientSytem.setTopOptId("METAFORM");
		
		CentitUserDetails userinfo = clientSytem.loadUserDetailsByLoginName("admin");
		System.out.println(userinfo.getUserName());*/
	}

	public static String encodeDatabasePassword(String password) {
		return AESSecurityUtils.encryptAndBase64(
				password, DatabaseInfo.DESKEY);
	}

	public static String decodeDatabasePassword(String password) {
		return AESSecurityUtils.decryptBase64String(
				password, DatabaseInfo.DESKEY);
	}
}
