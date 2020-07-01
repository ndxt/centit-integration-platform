package com.centit.framework.ip.po;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.metadata.IDatabaseInfo;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import com.centit.support.security.AESSecurityUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "F_DATABASE_INFO")
@ApiModel(value="数据库信息对象",description="数据库信息对象 DatabaseInfo")
public class DatabaseInfo implements IDatabaseInfo, Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DESKEY="0123456789abcdefghijklmnopqrstuvwxyzABCDEF";

    // 数据库名
    @Id
    @Column(name = "DATABASE_CODE")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String databaseCode;

    @Column(name = "OS_ID")
    @ApiModelProperty(value = "系统代码",name = "osId")
    @DictionaryMap(value="osInfo",fieldName = "osName",isExpression = false)
    private String osId;

    @Column(name = "DATABASE_NAME")
    @ApiModelProperty(value = "数据库名",name = "databaseName")
    private String databaseName;

    @Column(name = "DATABASE_URL")
    @Length(max = 1000, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "数据库地址",name = "databaseUrl")
    private String databaseUrl;

    @Column(name = "USERNAME")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "数据库用户名",name = "username")
    private String username;

    @Column(name = "PASSWORD")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "数据库密码",name = "password")
    private String password;

    @Column(name = "DATABASE_DESC")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "数据库描述信息",name = "databaseDesc")
    private String databaseDesc;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "today()", condition = GeneratorCondition.ALWAYS, occasion = GeneratorTime.ALWAYS )
    @Column(name = "LAST_MODIFY_DATE")
    private Date lastModifyDate;

    @Column(name = "CREATED")
    @DictionaryMap(fieldName = "createUserName", value = "userCode")
    private String created;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "today()")
    @Column(name = "CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "扩展属性，json格式，clob字段")
    //@Column(name = "EXT_PROPS")
    //@Basic(fetch = FetchType.LAZY)
    private JSONObject extProps;

    // Constructors
    /**
     * default constructor
     */
    public DatabaseInfo() {
    }

    public DatabaseInfo(String databaseCode,String databaseName) {
        this.databaseCode = databaseCode;
        this.databaseName = databaseName;
    }

    public DatabaseInfo(String databaseCode, String databaseName, String databaseUrl,
                 String username, String password, String dataDesc) {
        this.databaseCode = databaseCode;
        this.databaseName = databaseName;
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
        this.databaseDesc = dataDesc;
    }

    public void setPassword(String password) {
        if(password.startsWith("cipher:")) {
            this.password = password;
        }else {
            this.password = "cipher:" + AESSecurityUtils.encryptAndBase64(
                password, DatabaseInfo.DESKEY);
        }
    }

    @Override
    @JSONField(serialize = false)
    public String getClearPassword(){
        return AESSecurityUtils.decryptBase64String(
            getPassword().substring(7),DatabaseInfo.DESKEY);
    }

}
