package com.centit.framework.ip.app.test;

import com.centit.support.security.AESSecurityUtils;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

public class TestPlatSystem {

    public static void main(String[] args) {
//		System.out.println(encodeDatabasePassword("1315_Com"));
//		System.out.println(encodeDatabasePassword("bizdata"));
//		System.out.println(decodeDatabasePassword(
//				encodeDatabasePassword("bizdata")));
		// TODO Auto-generated method stub
		/*IPClientPlatformEnvironment clientSytem = new IPClientPlatformEnvironment();
		clientSytem.setPlatServerUrl("http://productsvr.centit.com:8880/centit-ip/service/platform");
		clientSytem.setTopOptId("METAFORM");

		CentitUserDetails userinfo = clientSytem.loadUserDetailsByLoginName("admin");
		System.out.println(userinfo.getUserName());*/
        try{
            String url = URLEncoder.encode("http://ceshi.centit.com/locode/api/framework/system/ddlogin/getUserInfo?returnUrl=http://ceshi.centit.com/locode/A/application","UTF-8");
            System.out.println(url);
//
//            File file = new File("D://wx.css");
//            FileInputStream inputFile = new FileInputStream(file);
//            byte[] buffer = new byte[(int)file.length()];
//            inputFile.read(buffer);
//            inputFile.close();
//            System.out.println(new BASE64Encoder().encode(buffer));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String encodeDatabasePassword(String password) {
		return AESSecurityUtils.encryptAndBase64(
				password, AESSecurityUtils.AES_DEFAULT_KEY);
    }

    public static String decodeDatabasePassword(String password) {
		return AESSecurityUtils.decryptBase64String(
				password, AESSecurityUtils.AES_DEFAULT_KEY);
    }
}
