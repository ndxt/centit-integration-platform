package com.centit.framework.ip.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "F_USER_ACCESS_TOKEN")
@Data
public class UserAccessToken implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TOKEN_ID")
//    @GeneratedValue(generator = "assignedGenerator")
//    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String tokenId;

    @Column(name = "USER_CODE")
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

}
