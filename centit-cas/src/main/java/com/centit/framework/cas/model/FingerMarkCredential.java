package com.centit.framework.cas.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FingerMarkCredential extends ComplexAuthCredential {

    private String username;
    private String fingerMark;

    public FingerMarkCredential(){
        super("fingerMark");
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

    public String getFingerMark() {
        return fingerMark;
    }

    public void setFingerMark(String fingerMark) {
        this.fingerMark = fingerMark;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.username)
            .append(this.fingerMark)
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
        final FingerMarkCredential other = (FingerMarkCredential) obj;
        if( this.username != null ? !this.username.equals(other.username) : other.username != null){
            return false;
        }
        return this.fingerMark != null ? this.fingerMark.equals(other.fingerMark) : other.fingerMark == null;
    }

    @Override
    public String toString() {
        return this.username;
    }
}
