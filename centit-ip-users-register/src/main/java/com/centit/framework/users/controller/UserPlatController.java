package com.centit.framework.users.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.operationlog.RecordOperationLog;
import com.centit.framework.system.po.UserRole;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.UserPlatService;
import com.centit.support.common.ParamName;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/userplat")
public class UserPlatController extends BaseController {

    @Autowired
    private UserPlatService userPlatService;


    /*
     * 创建用户平台关联信息
     *
     * @param userRole UserRole
     * @param userCode userCode
     */

    @ApiOperation(value = "创建用户平台关联信息", notes = "创建用户平台关联信息")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "userPlat", value = "json格式的用户角色对象信息",
        required = true, paramType = "body", dataTypeClass = UserRole.class
    ), @ApiImplicitParam(
        name = "userCode", value = "用户代码集合（数组）",
        allowMultiple = true, paramType = "query", dataType = "String"
    )})
    @RequestMapping(method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData create(@ParamName("platId") @Valid UserPlat userPlat,
                               @ParamName("userCode") @Valid String[] userCode) {
        userPlat.setCreateDate(new Date());
        if (userCode != null && userCode.length > 0) {
            for (String u : userCode) {
                userPlat.setUserCode(u);
                userPlatService.mergeObject(userPlat);
            }
        } else {
            userPlatService.mergeObject(userPlat);
        }
        return ResponseData.successResponse;
    }
}
