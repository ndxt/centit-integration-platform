package com.centit.framework.ip.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "F_USER_ACCESS_TOKEN")

public class UserAccessToken implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TOKEN_ID")
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String tokenId;
    
    @Column(name = "USERCODE")
    private String userCode;
    
    @Column(name = "SECRET_ACCESS_KEY")
    private String secretAccessKey;

    @Column(name = "ISVALID")
    private String isValid;
    
    @Column(name = "CREATE_TIME")
    private Date createTime;


    // Constructors

    /**
     * default constructor
     */
    public UserAccessToken() {
    	this.isValid = "T";
    }

    public UserAccessToken(String userCode) {
    	this.userCode = userCode;
    	this.isValid = "T";
    }

	public String getTokenId() {
		return tokenId;
	}


	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}


	public String getUserCode() {
		return userCode;
	}


	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}


	public String getSecretAccessKey() {
		return secretAccessKey;
	}


	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}


	public String getIsValid() {
		return isValid;
	}


	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

   
}
