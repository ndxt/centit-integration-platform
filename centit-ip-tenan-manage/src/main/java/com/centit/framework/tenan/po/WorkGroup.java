package com.centit.framework.tenan.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
@Entity
@Table(name = "work_group")
@ApiModel(value = "工作组", description = "工作组")
public class WorkGroup implements Serializable {
    @Id
    @Column(name = "group_id")
    @Length(max = 32)
    @ApiModelProperty(value = "组id", name = "groupId")
    private String groupId;

    @Id
    @Column(name = "user_code")
    @Length(max = 32)
    @ApiModelProperty(value = "用户代码", name = "userCode")
    private String userCode;

    @Id
    @Column(name = "role_code")
    @Length(max = 32)
    @ApiModelProperty(value = "角色", name = "roleCode")//在os中role对应 组长：OSZZ 组员：OSZY
    private String roleCode;

    @Id
    @Column(name = "is_valid")
    @Length(max = 32)
    @ApiModelProperty(value = "是否生效", name = "isValid")
    private String isValid;

    @Id
    @Column(name = "AUTH_TIME")
    @Length(max = 32)
    @ApiModelProperty(value = "创建时间", name = "authTime")
    private Date authTime;

    @Id
    @Column(name = "creator")
    @Length(max = 32)
    @ApiModelProperty(value = "创建人", name = "creator")
    private String creator;

    @Id
    @Column(name = "updator")
    @Length(max = 32)
    @ApiModelProperty(value = "更新人", name = "updator")
    private String updator;

    @Id
    @Column(name = "UPDATE_DATE")
    @Length(max = 32)
    @ApiModelProperty(value = "更新时间", name = "updateDate")
    private Date updateDate;

    @Id
    @Column(name = "USER_ORDER")
    @Length(max = 32)
    @ApiModelProperty(value = "排序号", name = "userOrder")
    private Date userOrder;

    @Id
    @Column(name = "RUN_TOKEN")
    @Length(max = 32)
    @ApiModelProperty(value = "运行令牌", name = "runToken")
    private Date runToken;

    @Id
    @Column(name = "AUTH_DESC")
    @Length(max = 32)
    @ApiModelProperty(value = "授权说明", name = "authDesc")
    private Date authDesc;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(Date userOrder) {
        this.userOrder = userOrder;
    }

    public Date getRunToken() {
        return runToken;
    }

    public void setRunToken(Date runToken) {
        this.runToken = runToken;
    }

    public Date getAuthDesc() {
        return authDesc;
    }

    public void setAuthDesc(Date authDesc) {
        this.authDesc = authDesc;
    }

    @Override
    public String toString() {
        return "WorkGroup{" +
            "groupId='" + groupId + '\'' +
            ", userCode='" + userCode + '\'' +
            ", roleCode='" + roleCode + '\'' +
            ", isValid='" + isValid + '\'' +
            ", authTime=" + authTime +
            ", creator='" + creator + '\'' +
            ", updator='" + updator + '\'' +
            ", updateDate=" + updateDate +
            ", userOrder=" + userOrder +
            ", runToken=" + runToken +
            ", authDesc=" + authDesc +
            '}';
    }
}
