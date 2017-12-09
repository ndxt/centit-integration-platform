package com.centit.framework.cas.model;

import org.apereo.cas.authentication.Credential;

import java.io.Serializable;

public abstract class ComplexAuthCredential implements Credential, Serializable {

    private String authType;
    private String macAddr;

    public ComplexAuthCredential(String authType) {
        this.authType = authType;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
}
