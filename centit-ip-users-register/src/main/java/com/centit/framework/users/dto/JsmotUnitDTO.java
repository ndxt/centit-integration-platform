package com.centit.framework.users.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author zfg
 */
@ApiModel(value = "交通云从业企业", description = "交通云从业企业")
@Data
public class JsmotUnitDTO {

    @ApiModelProperty(value = "企业id", name = "id")
    private String id;

    @ApiModelProperty(value = "企业名称", name = "name", required = true)
    private String name;

    @ApiModelProperty(value = "统一社会信用代码", name = "tyshxydm", required = true)
    private String tyshxydm;

    @ApiModelProperty(value = "企业传真", name = "fax")
    private String fax;

    @ApiModelProperty(value = "企业电话", name = "phone")
    private String phone;

    @ApiModelProperty(value = "企业电子邮箱", name = "email")
    private String email;

    @ApiModelProperty(value = "邮政编码", name = "postcode")
    private String postcode;

    @ApiModelProperty(value = "企业办公地点", name = "position")
    private String position;

    @ApiModelProperty(value = "法定代表人信息", name = "userInfo")
    private JsmotUserDTO userInfo;
}
