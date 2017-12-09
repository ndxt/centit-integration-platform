package com.centit.framework.cas.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apereo.cas.authentication.RememberMeCredential;

public abstract class AbstractPasswordCredential extends ComplexAuthCredential implements RememberMeCredential {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String validateCode;
    private boolean rememberMe;

    public AbstractPasswordCredential(String authType ){
        super(authType);
        this.rememberMe = false;
    }

    @Override
    public boolean isRememberMe() {
        return this.rememberMe;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.username)
            .append(this.password)
            .append(this.rememberMe)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPasswordCredential other = (AbstractPasswordCredential) obj;
        if (this.password != null ? !this.password.equals(other.password) : other.password != null) {
            return false;
        }

        if( this.username != null ? !this.username.equals(other.username) : other.username != null){
            return false;
        }
        return this.rememberMe == other.rememberMe;
    }

    @Override
    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public String getId() {
        return this.username;
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

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    @Override
    public String toString() {
        return this.username;
    }
}
