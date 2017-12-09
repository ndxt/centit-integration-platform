package com.centit.framework.cas.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UsbKeyCredential extends ComplexAuthCredential{
    private String username;
    private String keySerialNumber;

    public UsbKeyCredential(){
        super("usbKey");
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

    public String getKeySerialNumber() {
        return keySerialNumber;
    }

    public void setKeySerialNumber(String keySerialNumber) {
        this.keySerialNumber = keySerialNumber;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.username)
            .append(this.keySerialNumber)
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
        final UsbKeyCredential other = (UsbKeyCredential) obj;
        if( this.username != null ? !this.username.equals(other.username) : other.username != null){
            return false;
        }
        return this.keySerialNumber != null ? this.keySerialNumber.equals(other.keySerialNumber) : other.keySerialNumber == null;
    }

    @Override
    public String toString() {
        return this.username;
    }
}
