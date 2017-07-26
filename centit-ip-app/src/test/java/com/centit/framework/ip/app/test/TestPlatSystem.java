package com.centit.framework.ip.app.test;

import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;

public class TestPlatSystem {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IPClientPlatformEnvironment clientSytem = new IPClientPlatformEnvironment();
		clientSytem.setPlatServerUrl("http://productsvr.centit.com:8880/centit-ip/service/platform");
		clientSytem.setTopOptId("METAFORM");
		
		CentitUserDetails userinfo = clientSytem.loadUserDetailsByLoginName("admin");
		System.out.println(userinfo.getUserName());
	}

}
