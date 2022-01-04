package com.centit.framework.tenant.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;


@ApiModel(value = "租户基本信息", description = "租户基本信息")
@Table(name = "f_tenant_info")
public class TenantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "top_unit")
    @ValueGenerator(strategy = GeneratorType.RANDOM_ID, value = "7:T")
    @Length(max = 32)
    @ApiModelProperty(value = "机构id", name = "topUnit")
    private String topUnit;

    @Column(name = "unit_name")
    @Length(max = 300)
    @ApiModelProperty(value = "机构名", name = "unitName")
    @NotEmpty
    private String unitName;

    @Column(name = "source_url")
    @Length(max = 300)
    @ApiModelProperty(value = "资源地址", name = "sourceUrl")
    private String sourceUrl;

    @Column(name = "use_limittime")
    @ApiModelProperty(value = "使用时限", name = "useLimittime")
    private Date useLimittime;

    @Column(name = "tenant_fee")
    @ApiModelProperty(value = "租用费用", name = "tenantFee")
    private int tenantFee;

    @Column(name = "own_user")
    @Length(max = 32)
    @ApiModelProperty(value = "租户所有人", name = "ownUser")
    private String ownUser;

    @Column(name = "is_available")
    @Length(max = 1)
    @ApiModelProperty(value = "受否可用,T:可用，F：不可用", name = "isAvailable")
    private String isAvailable;

    @Column(name = "apply_time")
    @ApiModelProperty(value = "申请时间", name = "applyTime")
    private Date applyTime;

    @Column(name = "pass_time")
    @ApiModelProperty(value = "通过时间", name = "passTime")
    private Date passTime;

    @Column(name = "memo")
    @ApiModelProperty(value = "备注", name = "memo")
    private String memo;

    @Column(name = "remarks")
    @Length(max = 500)
    @ApiModelProperty(value = "备注", name = "remarks")
    private String remarks;

    @Column(name = "creator")
    @Length(max = 32)
    @ApiModelProperty(value = "申请人", name = "creator")
    private String creator;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间", name = "createTime")
    private Date createTime;

    @Column(name = "updator")
    @Length(max = 32)
    @ApiModelProperty(value = "更新人", name = "updator")
    private String updator;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间", name = "updateTime")
    private Date updateTime;

    @Column(name = "database_number_limit")
    @ApiModelProperty(value = "数据库个数上限 ,数据类型：D", name = "databaseNumberLimit")
    private Integer databaseNumberLimit;

    @Column(name = "os_number_limit")
    @ApiModelProperty(value = "应用个数上限 数据类型：O", name = "osNumberLimit")
    private Integer osNumberLimit;

    @Column(name = "file_space_limit")
    @ApiModelProperty(value = "文件服务空间上限 数据类型：F", name = "fileSpaceLimit")
    private Integer fileSpaceLimit;

    @Column(name = "data_space_limit")
    @ApiModelProperty(value = "数据空间上限 数据类型：C", name = "dataSpaceLimit")
    private Integer dataSpaceLimit;

    @Column(name = "user_number_limit")
    @ApiModelProperty(value = "租户下用户总数上限 ", name = "userNumberLimit")
    private Integer userNumberLimit;

    @Column(name = "unit_number_limit")
    @ApiModelProperty(value = "租户下单位个数上限 ", name = "unitNumberLimit")
    private Integer unitNumberLimit;

    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Date getUseLimittime() {
        return useLimittime;
    }

    public void setUseLimittime(Date useLimittime) {
        this.useLimittime = useLimittime;
    }

    public int getTenantFee() {
        return tenantFee;
    }

    public void setTenantFee(int tenantFee) {
        this.tenantFee = tenantFee;
    }

    public String getOwnUser() {
        return ownUser;
    }

    public void setOwnUser(String ownUser) {
        this.ownUser = ownUser;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getPassTime() {
        return passTime;
    }

    public void setPassTime(Date passTime) {
        this.passTime = passTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDatabaseNumberLimit() {
        return databaseNumberLimit;
    }

    public void setDatabaseNumberLimit(Integer databaseNumberLimit) {
        this.databaseNumberLimit = databaseNumberLimit;
    }

    public Integer getOsNumberLimit() {
        return osNumberLimit;
    }

    public void setOsNumberLimit(Integer osNumberLimit) {
        this.osNumberLimit = osNumberLimit;
    }

    public Integer getFileSpaceLimit() {
        return fileSpaceLimit;
    }

    public void setFileSpaceLimit(Integer fileSpaceLimit) {
        this.fileSpaceLimit = fileSpaceLimit;
    }

    public Integer getDataSpaceLimit() {
        return dataSpaceLimit;
    }

    public void setDataSpaceLimit(Integer dataSpaceLimit) {
        this.dataSpaceLimit = dataSpaceLimit;
    }

    public Integer getUserNumberLimit() {
        return userNumberLimit;
    }

    public void setUserNumberLimit(Integer userNumberLimit) {
        this.userNumberLimit = userNumberLimit;
    }

    public Integer getUnitNumberLimit() {
        return unitNumberLimit;
    }

    public void setUnitNumberLimit(Integer unitNumberLimit) {
        this.unitNumberLimit = unitNumberLimit;
    }

    @Override
    public String toString() {
        return "TenantInfo{" +
            "topUnit='" + topUnit + '\'' +
            ", unitName='" + unitName + '\'' +
            ", sourceUrl='" + sourceUrl + '\'' +
            ", useLimittime=" + useLimittime +
            ", tenantFee=" + tenantFee +
            ", ownUser='" + ownUser + '\'' +
            ", isAvailable='" + isAvailable + '\'' +
            ", applyTime=" + applyTime +
            ", passTime=" + passTime +
            ", memo='" + memo + '\'' +
            ", remarks='" + remarks + '\'' +
            ", creator='" + creator + '\'' +
            ", createTime=" + createTime +
            ", updator='" + updator + '\'' +
            ", updateTime=" + updateTime +
            ", databaseNumberLimit=" + databaseNumberLimit +
            ", osNumberLimit=" + osNumberLimit +
            ", fileSpaceLimit=" + fileSpaceLimit +
            ", dataSpaceLimit=" + dataSpaceLimit +
            ", userNumberLimit=" + userNumberLimit +
            ", unitNumberLimit=" + unitNumberLimit +
            '}';
    }
}
