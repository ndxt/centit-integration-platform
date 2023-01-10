package com.centit.framework.users.test;

import com.alibaba.fastjson.JSON;
import com.centit.framework.system.po.UserSyncDirectory;
import com.centit.framework.users.controller.LdapLogin;

import java.util.Map;

public class TestLdapSearch {
    public static void main(String[] args) {
        UserSyncDirectory directory = new UserSyncDirectory();
        directory.setTopUnit("centit");
        directory.setUrl("LDAP://192.168.128.5:389");
        directory.setUser("accountcentit@centit.com");
        directory.setUserPwd("*********");
        directory.setSearchBase("{ " +
             " searchBase : \"(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))\", "+
             " searchName : \"CN=Users,DC=centit,DC=com\", "+
             " loginNameField : \"sAMAccountName\", "+
             " returnFields : { "+
                   " userName : \"displayName\", "+
                   " loginName : \"sAMAccountName\", "+
                   " name : \"name\", "+
                   " regEmail : \"mail\", "+
                   " regCellPhone : \"mobilePhone\", "+
                   " userDesc : \"description\" "+
            " }, "+
            " userURIFormat : \"CN={name},CN=Users,DC=centit,DC=com\" "+
         "}");
        //Map<String, Object> userInfo = LdapLogin.searchLdapUserByloginName(directory, "codefan");
        boolean pass = LdapLogin.checkUserPasswordByDn(directory, "codefan", "******");
        System.out.println(JSON.toJSONString(pass));
    }
}
