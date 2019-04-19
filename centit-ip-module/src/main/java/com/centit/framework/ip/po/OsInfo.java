package com.centit.framework.ip.po;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "F_OS_INFO")
@ApiModel(value="系统信息对象",description="系统信息对象 OsInfo")
@Data
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

    @Column(name = "OAUTH_PASSWORD")
    @Length(max = 128, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "oauth2 登录密码 密文",name = "oauthPassword")
    private String oauthPassword;

    @Column(name = "CREATED")
    @Length(max = 8, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName = "createUserName", value = "userCode")
    private String created;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "today()", condition = GeneratorCondition.ALWAYS, occasion = GeneratorTime.ALWAYS )
    @Column(name = "LAST_MODIFY_DATE")
    private Date lastModifyDate;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "today()")
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
