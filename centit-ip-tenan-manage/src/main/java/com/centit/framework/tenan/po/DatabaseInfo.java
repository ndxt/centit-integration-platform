package com.centit.framework.tenan.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "资源表", description = "资源表")
@Table(name = "f_database_info")
public class DatabaseInfo implements Serializable {
    @Id
    @Column(name = "Database_Code")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32)
    @ApiModelProperty(value = "资源代码", name = "DatabaseCode")
    private String DatabaseCode;

    @Column(name = "top_unit")
    @Length(max = 32)
    @ApiModelProperty(value = "所属租户", name = "topUnit")
    private String topUnit;

    @Column(name = "database_name")
    @Length(max = 100)
    @ApiModelProperty(value = "资源名称", name = "databaseName")
    private String databaseName;

    @Column(name = "database_url")
    @Length(max = 1000)
    @ApiModelProperty(value = "资源链接", name = "databaseUrl")
    private String databaseUrl;

    @Column(name = "username")
    @Length(max = 100)
    @ApiModelProperty(value = "用户名", name = "username")
    private String username;

    @Column(name = "password")
    @Length(max = 100)
    @ApiModelProperty(value = "密码", name = "password")
    private String password;

    @Column(name = "database_desc")
    @Length(max = 500)
    @ApiModelProperty(value = "资源描述", name = "databaseDesc")
    private String databaseDesc;

    @Column(name = "last_Modify_DATE")
    @ApiModelProperty(value = "最新修改时间", name = "lastModifyDate")
    private Date lastModifyDate;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间", name = "createTime")
    private Date createTime;

    @Column(name = "created")
    @Length(max = 32)
    @ApiModelProperty(value = "创建人", name = "created")
    private String created;

    @Column(name = "updator")
    @Length(max = 32)
    @ApiModelProperty(value = "修改人", name = "updator")
    private String updator;

    @Column(name = "SOURCE_TYPE")
    @Length(max = 1)
    @ApiModelProperty(value = "资源类型 数据库资源类型：D,应用资源类型:O,文件空间资源类型:F,数据空间资源类型:C", name = "sourceType")
    private String sourceType;

    @Column(name = "EXT_PROPS")
    @ApiModelProperty(value = "扩展属性", name = "extProps")
    private String extProps;

    public String getDatabaseCode() {
        return DatabaseCode;
    }

    public void setDatabaseCode(String databaseCode) {
        DatabaseCode = databaseCode;
    }

    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
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

    public String getDatabaseDesc() {
        return databaseDesc;
    }

    public void setDatabaseDesc(String databaseDesc) {
        this.databaseDesc = databaseDesc;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getExtProps() {
        return extProps;
    }

    public void setExtProps(String extProps) {
        this.extProps = extProps;
    }

    @Override
    public String toString() {
        return "DatabaseInfo{" +
            "DatabaseCode='" + DatabaseCode + '\'' +
            ", topUnit='" + topUnit + '\'' +
            ", databaseName='" + databaseName + '\'' +
            ", databaseUrl='" + databaseUrl + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", databaseDesc='" + databaseDesc + '\'' +
            ", lastModifyDate=" + lastModifyDate +
            ", createTime=" + createTime +
            ", created='" + created + '\'' +
            ", updator='" + updator + '\'' +
            ", sourceType='" + sourceType + '\'' +
            ", extProps='" + extProps + '\'' +
            '}';
    }
}
