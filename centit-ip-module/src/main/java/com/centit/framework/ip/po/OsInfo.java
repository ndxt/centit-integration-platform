package com.centit.framework.ip.po;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "F_OS_INFO")
@ApiModel(value="系统信息对象",description="系统信息对象 OsInfo")
public class OsInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OS_ID")
//    @GeneratedValue(generator = "assignedGenerator")
//    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    @ApiModelProperty(value = "业务系统ID",name = "osId",required = true)
    private String osId;

    @Column(name = "OS_NAME")
    @Length(max = 200, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "系统名",name = "osName")
    private String osName;

    @Column(name = "OS_URL")
    @Length(max = 200, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "系统地址",name = "osUrl")
    private String osUrl;

    @Column(name = "DDE_SYNC_URL")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String ddeSyncUrl;

    @Column(name = "SYS_DATA_PUSH_OPTION")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String sysDataPushOption;

    @Column(name = "LOGIN_USER_NAME")
    @Length(max = 64, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "登录名",name = "loginUserName")
    private String loginUserName;

    @Column(name = "LOGIN_USER_PASSWORD")
    @Length(max = 64, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "登录密码",name = "loginUserPassword")
    private String loginUserPassword;

    @Column(name = "CREATED")
    @Length(max = 8, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName = "createUserName", value = "userCode")
    private String created;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "now", condition = GeneratorCondition.ALWAYS, occasion = GeneratorTime.ALWAYS )
    @Column(name = "LAST_MODIFY_DATE")
    private Date lastModifyDate;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "now")
    @Column(name = "CREATE_TIME")
    private Date createTime;


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
        this.ddeSyncUrl=ddeSyncUrl;
        this.sysDataPushOption=sysDataPushOption;
        this.osUrl = osUrl;
        this.created = created;
        this.lastModifyDate= lastUpdateTime;
        this.createTime = createTime;
    }


    public String getOsId() {
        return this.osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }
    // Property accessors

    public String getOsName() {
        return this.osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    public String getOsUrl() {
		return osUrl;
    }

    public void setOsUrl(String osUrl) {
		this.osUrl = osUrl;
    }

    public String getDdeSyncUrl() {
		return ddeSyncUrl;
    }

    public void setDdeSyncUrl(String ddeSyncUrl) {
		this.ddeSyncUrl = ddeSyncUrl;
    }

    public String getSysDataPushOption() {
		return sysDataPushOption;
    }

    public void setSysDataPushOption(String sysDataPushOption) {
		this.sysDataPushOption = sysDataPushOption;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public String getLoginUserPassword() {
        return loginUserPassword;
    }

    public void setLoginUserPassword(String loginUserPassword) {
        this.loginUserPassword = loginUserPassword;
    }

    public Date getLastModifyDate() {
		return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public void copy(OsInfo other) {
        this.setOsId(other.getOsId());
        this.osName = other.getOsName();
        this.ddeSyncUrl=other.getDdeSyncUrl();
        this.sysDataPushOption=other.getSysDataPushOption();
        this.osUrl = other.getOsUrl();
        this.lastModifyDate= other.getLastModifyDate();
        this.createTime = other.getCreateTime();
        this.created = other.getCreated();
        this.loginUserName = other.getLoginUserName();
        this.loginUserPassword = other.getLoginUserPassword();
    }

    public void copyNotNullProperty(OsInfo other) {
        if (other.getOsId() != null)
            this.setOsId(other.getOsId());
        if (other.getOsName() != null)
            this.osName = other.getOsName();
        if (other.getDdeSyncUrl() != null)
            this.ddeSyncUrl=other.getDdeSyncUrl();
        if (other.getSysDataPushOption() != null)
            this.sysDataPushOption=other.getSysDataPushOption();
        if (other.getOsUrl() != null)
            this.osUrl = other.getOsUrl();
        if (other.getLastModifyDate() != null)
            this.lastModifyDate= other.getLastModifyDate();
        if (other.getCreated() != null)
            this.created = other.getCreated();
        if (other.getCreateTime() != null)
            this.createTime = other.getCreateTime();
        if (other.getLoginUserName() != null)
            this.loginUserName = other.getLoginUserName();
        if (other.getLoginUserPassword() != null)
            this.loginUserPassword = other.getLoginUserPassword();
    }

    public void clearProperties() {
        this.osId = null;
        this.osName = null;
        this.ddeSyncUrl=null;
        this.sysDataPushOption=null;
        this.osUrl = null;
        this.created = null;
        this.lastModifyDate= null;
        this.createTime = null;
        this.loginUserName = null;
        this.loginUserPassword = null;
    }
}
