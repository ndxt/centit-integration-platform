package com.centit.framework.tenan.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import javax.persistence.*;
import java.io.Serializable;


@ApiModel(value = "成员信息表", description = "成员信息表")
@Table(name = "F_TENANT_MEMBER_TMP")
//表名没有实际意义，仅仅是为了查询映射使用
public class TenantMember implements Serializable {


    @Id
    @Column(name = "USER_CODE")
    @ApiModelProperty(value = "用户代码", name = "userCode")
    private String userCode;


    @Column(name = "LOGIN_NAME")
    @ApiModelProperty(
        value = "用户登录名", name = "loginName", required = true
    )
    private String loginName;

    @Column(name = "USER_NAME")
    @Length(max = 300, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "用户姓名", name = "userName", required = true)
    private String userName;

    @Column(name = "ENGLISH_NAME")
    @Length(max = 300, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "用户英文姓名", name = "englishName")
    private String englishName;

    @Column(name = "USER_DESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String userDesc;


    @Column(name = "REG_EMAIL")
    @Length(
        max = 60, message = "字段长度不能大于{max}")
    private String regEmail;

    @Column( name = "REG_CELL_PHONE" )
    @Length( max = 15,message = "字段长度不能大于{max}")
    private String regCellPhone;

    @Column( name = "USER_WORD")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String userWord;
    @Column(  name = "USER_TAG")
    @Length(max = 200,message = "字段长度不能大于{max}")
    private String userTag;


    @Column( name = "ROLE_CODE")
    private String roleCode;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getRegEmail() {
        return regEmail;
    }

    public void setRegEmail(String regEmail) {
        this.regEmail = regEmail;
    }

    public String getRegCellPhone() {
        return regCellPhone;
    }

    public void setRegCellPhone(String regCellPhone) {
        this.regCellPhone = regCellPhone;
    }

    public String getUserWord() {
        return userWord;
    }

    public void setUserWord(String userWord) {
        this.userWord = userWord;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
