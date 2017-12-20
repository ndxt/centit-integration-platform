package com.centit.framework.cas.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LdapProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;//=LDAP://192.168.128.5:389
    private String username;//=accounts@centit.com
    private String password;//=yhs@yhs1
    private String searchBase;//=CN=Users,DC=centit,DC=com
    private String principalIdField;
    private String principalAttributes;
    private String dnFormat;
    private List<String> searchFilter = new ArrayList<>(5);
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

    public String getPrincipalIdField() {
        return principalIdField;
    }

    public void setPrincipalIdField(String principalIdField) {
        this.principalIdField = principalIdField;
    }

    public String getPrincipalAttributes() {
        return principalAttributes;
    }

    public String [] getPrincipalAttributesAsArray() {
        return principalAttributes.split(",");
    }


    public void setPrincipalAttributes(String principalAttributes) {
        this.principalAttributes = principalAttributes;
    }

    public List<String> getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(List<String> searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String getDnFormat() {
        return dnFormat;
    }

    public void setDnFormat(String dnFormat) {
        this.dnFormat = dnFormat;
    }
}
