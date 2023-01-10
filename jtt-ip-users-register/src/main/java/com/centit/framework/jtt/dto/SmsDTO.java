package com.centit.framework.jtt.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zfg
 */
@ApiModel(value = "短信发送内容", description = "短信发送内容")
@Data
public class SmsDTO {

    @ApiModelProperty(value = "短信内容", name = "content", required = true)
    private String content;

    @ApiModelProperty(value = "单个手机号", name = "mobile")
    private String mobile;

    @ApiModelProperty(value = "手机号数组", name = "mobiles")
    private List<String> mobiles;
}
