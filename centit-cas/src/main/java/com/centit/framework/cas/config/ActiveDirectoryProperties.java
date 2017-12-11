package com.centit.framework.cas.config;

import java.io.Serializable;

public class ActiveDirectoryProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;//=LDAP://192.168.128.5:389
    private String username;//=accounts@centit.com
    private String password;//=yhs@yhs1
    private String searchBase;//=CN=Users,DC=centit,DC=com

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }
}
