package com.centit.framework.users.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author zfg
 */
@ApiModel(value = "同步系统机构至交通云机构部门", description = "同步系统机构至交通云机构部门")
@Data
public class JsmotUnitDTO {

    @ApiModelProperty(value = "机构代码", name = "unitCode", required = true)
    @Length(max = 32)
    private String unitCode;

    @ApiModelProperty(value = "机构名称", name = "unitName", required = true)
    @Length(max = 300)
    private String unitName;

    @ApiModelProperty(value = "上级机构代码", name = "parentUnit", required = true)
    @Length(max = 32)
    private String parentUnit;

}
