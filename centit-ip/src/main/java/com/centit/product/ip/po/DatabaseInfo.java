package com.centit.product.ip.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.support.security.DESSecurityUtils;


@Entity
@Table(name = "F_DATABASE_INFO")
public class DatabaseInfo implements EntityWithTimestamp, Serializable {
    private static final long serialVersionUID = 1L;
    public static String DESKEY="0123456789abcdefghijklmnopqrstuvwxyzABCDEF"; 
    // 数据库名
    @Id
    @Column(name = "DATABASE_CODE")
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String databaseCode;
    
    @Column(name = "OS_ID")
    private String osId;
    
    @Column(name = "DATABASE_NAME")
    private String databaseName;

    @Column(name = "DATABASE_URL")
    @Length(max = 1000, message = "字段长度不能大于{max}")
    private String databaseUrl;
    
    @Column(name = "USERNAME")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String username;
    @Column(name = "PASSWORD")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String password;
    
    @Column(name = "DATABASE_DESC")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String databaseDesc;
    
    @Column(name = "LAST_MODIFY_DATE")
    private Date lastModifyDate;
    
    @Column(name = "CREATED")    
    private String created;
    @Column(name = "CREATE_TIME")
    private Date createTime;
  
    // Constructors

    /**
     * default constructor
     */
    public DatabaseInfo() {
    }

    /**
     * minimal constructor
     */
    public DatabaseInfo(String databaseCode,String databaseName) {
    	this.databaseCode = databaseCode;
        this.databaseName = databaseName;
    }

    /**
     * full constructor
     */
    public DatabaseInfo(String databaseCode, String databaseName, String databaseUrl,
    		String username, String password,
                        String dataDesc) {
    	this.databaseCode = databaseCode;
        this.databaseName = databaseName;
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
        this.databaseDesc = dataDesc;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }
    public String getDatabaseCode() {
		return databaseCode;
	}

	public void setDatabaseCode(String databaseCode) {
		this.databaseCode = databaseCode;
	}
   

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDatabaseUrl() {
        return this.databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseDesc() {
        return this.databaseDesc;
    }

    public void setDatabaseDesc(String dataDesc) {
        this.databaseDesc = dataDesc;
    }

    /**
     * 替换子类对象数组，这个函数主要是考虑hibernate中的对象的状态，以避免对象状态不一致的问题
     */



    /**
     * 替换子类对象数组，这个函数主要是考虑hibernate中的对象的状态，以避免对象状态不一致的问题
     */

    public void copy(DatabaseInfo other) {
    	this.databaseCode = other.getDatabaseCode();
        this.setDatabaseName(other.getDatabaseName());
        this.databaseUrl = other.getDatabaseUrl();
        this.username = other.getUsername();
        this.password = other.getPassword();
        this.databaseDesc = other.getDatabaseDesc();
        this.osId = other.getOsId();
        this.created = other.getCreated();
        this.createTime = other.getCreateTime();
        this.lastModifyDate = other.getLastModifyDate();

    }

    public void copyNotNullProperty(DatabaseInfo other) {

        if (other.getDatabaseName() != null)
            this.setDatabaseName(other.getDatabaseName());
        if (other.getDatabaseCode() != null)
            this.databaseCode = other.getDatabaseCode();
        if (other.getDatabaseUrl() != null)
            this.databaseUrl = other.getDatabaseUrl();
        if (other.getUsername() != null)
            this.username = other.getUsername();
        if (other.getPassword() != null)
            this.password = other.getPassword();
        if (other.getDatabaseDesc() != null)
            this.databaseDesc = other.getDatabaseDesc();
        if (other.getOsId() != null)
        	this.osId = other.getOsId();
        if (other.getCreated() != null)
        	this.created = other.getCreated();
        if (other.getCreateTime() != null)
        	this.createTime = other.getCreateTime();
        if (other.getLastModifyDate() != null)
        	this.lastModifyDate = other.getLastModifyDate();
    }

    public void clearProperties() {

    	this.databaseCode = null;
        this.databaseName = null;
        this.databaseUrl = null;
        this.lastModifyDate = null;
        this.osId = null;
        this.username = null;
        this.password = null;
        this.databaseDesc = null;
        this.created = null;
        this.createTime = null;
    }

   
    
    public String getClearPassword(){
    	return DESSecurityUtils.decryptBase64String(
    			getPassword(),DatabaseInfo.DESKEY);
    }

}
