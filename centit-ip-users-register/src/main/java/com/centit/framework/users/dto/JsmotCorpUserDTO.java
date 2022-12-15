package com.centit.framework.users.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zfg
 */
@ApiModel(value = "交通云用户关联企业信息", description = "交通云用户关联企业信息")
@Data
public class JsmotCorpUserDTO {

    @ApiModelProperty(value = "企业id", name = "corpid", required = true)
    private String corpid;

    @ApiModelProperty(value = "企业用户类型：F法人；M管理员；Y普通员工", name = "corpUserType", required = true)
    private String corpUserType;

    @ApiModelProperty(value = "是否为主企业：T主企业；F兼职企业", name = "isPrimary", required = true)
    private String isPrimary;

    @ApiModelProperty(value = "排序号", name = "userOrder", required = true)
    private String userOrder;
}
