package com.centit.framework.ip.po;

import lombok.Data;

import java.util.Date;

@Data
public class OsInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String osId;
    private String osName;
    private String osUrl;
    private String created;
    private Date lastModifyDate;
    private Date createTime;
    /**oauth2 登录密码 密文*/
    private String oauthPassword;
    // Constructors

    /**
     * default constructor
     */
    public OsInfo() {
    }

    public OsInfo(
            String osId
            , String osName) {
        this.osId = osId;
        this.osName = osName;
    }

    public OsInfo(
            String osId
            , String osName,
            String sysDataPushOption, String osUrl,String ddeSyncUrl,
            String created, Date lastUpdateTime, Date createTime) {
        this.osId = osId;
        this.osName = osName;
        this.osUrl = osUrl;
        this.created = created;
        this.lastModifyDate= lastUpdateTime;
        this.createTime = createTime;
    }

    public void copy(OsInfo other) {
        this.setOsId(other.getOsId());
        this.osName = other.getOsName();
        this.osUrl = other.getOsUrl();
        this.lastModifyDate= other.getLastModifyDate();
        this.createTime = other.getCreateTime();
        this.created = other.getCreated();
        this.oauthPassword = other.getOauthPassword();
    }

    public void copyNotNullProperty(OsInfo other) {
        if (other.getOsId() != null)
            this.setOsId(other.getOsId());
        if (other.getOsName() != null)
            this.osName = other.getOsName();
        if (other.getOsUrl() != null)
            this.osUrl = other.getOsUrl();
        if (other.getLastModifyDate() != null)
            this.lastModifyDate= other.getLastModifyDate();
        if (other.getCreated() != null)
            this.created = other.getCreated();
        if (other.getCreateTime() != null)
            this.createTime = other.getCreateTime();
        if (other.getOauthPassword() != null)
            this.oauthPassword = other.getOauthPassword();
    }

    public void clearProperties() {
        this.osId = null;
        this.osName = null;
        this.osUrl = null;
        this.created = null;
        this.lastModifyDate= null;
        this.createTime = null;
        this.oauthPassword = null;
    }
}
