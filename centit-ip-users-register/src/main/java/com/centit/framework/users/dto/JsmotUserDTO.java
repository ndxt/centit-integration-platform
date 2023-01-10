package com.centit.framework.users.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author zfg
 */
@ApiModel(value = "交通云用户从业人员", description = "交通云用户从业人员")
@Data
public class JsmotUserDTO {

    @ApiModelProperty(value = "用户userCode", name = "userCode")
    private String userCode;

    @ApiModelProperty(value = "从业人员姓名", name = "userName", required = true)
    private String userName;

    @ApiModelProperty(value = "邮箱", name = "regEmail")
    private String regEmail;

    @ApiModelProperty(value = "手机号码", name = "regCellPhone", required = true)
    private String regCellPhone;

    @ApiModelProperty(value = "密码", name = "userPwd", required = true)
    private String userPwd;

    @ApiModelProperty(value = "身份证号码", name = "idCardNo", required = true)
    private String idCardNo;

    @ApiModelProperty(value = "性别", name = "sex", required = true)
    private String sex;

    @ApiModelProperty(value = "手机号", name = "mobile")
    private String mobile;

    @ApiModelProperty(value = "用户关联企业列表", name = "mobile")
    private List<JsmotCorpUserDTO> corpUserList;
}
