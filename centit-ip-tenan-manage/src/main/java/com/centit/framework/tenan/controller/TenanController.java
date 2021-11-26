package com.centit.framework.tenan.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.tenan.po.*;
import com.centit.framework.tenan.service.TenantPowerManage;
import com.centit.framework.tenan.service.TenantService;
import com.centit.framework.tenan.vo.PageListTenantInfoQo;
import com.centit.framework.tenan.vo.TenantMemberApplyVo;
import com.centit.framework.tenan.vo.TenantMemberQo;
import com.centit.support.common.ObjectException;
import com.centit.support.common.ParamName;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tenant")
@Api(
    tags = {"租户管理接口"},
    value = "租户管理接口"
)
public class TenanController extends BaseController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantPowerManage tenantPowerManage;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @ApiOperation(
        value = "注册用户账号",
        notes = "注册用户账号,请求体(用户基本信息)"
    )
    @RequestMapping(value = "/registerUserAccount", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData registerUserAccount(@RequestBody @Validated UserInfo userInfo) {

        try {
            return tenantService.registerUserAccount(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户注册失败,错误原因{},用户名数据：{}", e, userInfo.toString());
            return ResponseData.makeErrorMessage("用户注册失败!");
        }
    }

    @ApiOperation(
        value = "用户申请新建租户,目前租户申请后不需要管理员再次审核",
        notes = "用户申请新建租户,请求体(租户基本信息)"
    )
    @RequestMapping(value = "/applyAddTenant", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData applyAddTenant(@RequestBody @Validated TenantInfo tenantInfo) {

        try {
            return tenantService.applyAddTenant(tenantInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户申请新建租户失败,错误原因{},用户名数据：{}", e, tenantInfo.toString());
            return ResponseData.makeErrorMessage("用户申请新建租户失败!");
        }
    }

    @ApiOperation(
        value = "申请加入租户",
        notes = "可以是用户主动申请，也可以是管理员邀请,请求体(租户成员申请信息)"
    )
    @RequestMapping(value = "/applyJoinTenant", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData applyJoinTenant(@RequestBody @Validated TenantMemberApply tenantMemberApply) {

        try {
            return tenantService.applyJoinTenant(tenantMemberApply);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("成员申请失败,错误原因{},申请数据：{}", e, tenantMemberApply.toString());
            return ResponseData.makeErrorMessage("成员申请失败!");
        }
    }


    @ApiOperation(
        value = "列出申请信息",
        notes = "可以是管理员邀请的信息，也可以是用户主动申请的信息。" +
            "租户主动邀请用户加入，租户查看未审批的用户列表 applyType:2,topUnit= topUnit,applyState_in=[1,2]"
    )
    @ApiImplicitParams({@ApiImplicitParam(
        name = "applyType",
        value = "1:用户主动申请2:租户主动邀请",
        paramType = "String",
        dataTypeClass = String.class),
        @ApiImplicitParam(
            name = "userCode/topUnit",
            value = "用户代码或机构代码[userCode=][topUnit=]",
            paramType = "String",
            dataTypeClass = String.class),
        @ApiImplicitParam(
            name = "applyState",
            value = "审批类型 未审批：applyState_in=1,2,已审批：applyState_in=3,4,审批通过：applyState=3,不同意：applyState=4",
            paramType = "String",
            dataTypeClass = String.class),
        @ApiImplicitParam(
            name = "pageDesc",
            value = "分页对象",
            paramType = "body",
            dataTypeClass = PageDesc.class
        )})
    @RequestMapping(value = "/listApplyInfo", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult listApplyInfo(HttpServletRequest httpServletRequest,
                                         PageDesc pageDesc) {

        return tenantService.listApplyInfo(collectRequestParameters(httpServletRequest), pageDesc);
    }

    @ApiOperation(
        value = "撤销申请",
        notes = "用户或者租户撤销申请或邀请"
    )
    @ApiImplicitParams({@ApiImplicitParam(
        name = "topUnit",
        value = "租户id",
        paramType = "String",
        dataTypeClass = String.class),
        @ApiImplicitParam(
            name = "userCode",
            value = "用户code",
            paramType = "String",
            dataTypeClass = String.class)
    }
    )
    @RequestMapping(value = "/cancelApply", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData cancelApply(HttpServletRequest request) {
        Map<String, Object> parameters = collectRequestParameters(request);
        if (StringUtils.isAnyBlank(MapUtils.getString(parameters, "topUnit"),
            MapUtils.getString(parameters, "userCode"))) {
            return ResponseData.makeErrorMessage("topUnit或userCode不能为空");
        }
        return tenantService.cancelApply(parameters);
    }

    @ApiOperation(
        value = "注销租户",
        notes = "注销租户，只有租户所有者才可以操作"
    )
    @RequestMapping(value = "/deleteTenant", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData deleteTenant(HttpServletRequest request) {
        Map<String, Object> parameters = collectRequestParameters(request);
        if (StringUtils.isBlank(MapUtils.getString(parameters, "topUnit"))) {
            return ResponseData.makeErrorMessage("topUnit不能为空");
        }
        return tenantService.deleteTenant(parameters);
    }

    @ApiOperation(
        value = "同意加入租户",
        notes = "可以是平台管理员审核用户的加入,也可以是普通用户同意管理员的邀请"
    )
    @RequestMapping(value = "/agreeJoin", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData agreeJoin(@RequestBody @Validated TenantMemberApplyVo tenantMemberApply) {
        return tenantService.agreeJoin(tenantMemberApply);
    }

    @ApiOperation(
        value = "平台管理员审核租户",
        notes = "平台管理员审核租户，请求体(租户信息)"
    )
    @RequestMapping(value = "/adminCheckTenant", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData adminCheckTenant(@RequestBody TenantInfo tenantInfo) {

        return tenantService.adminCheckTenant(tenantInfo);
    }

    @ApiOperation(
        value = "更新用户基本信息",
        notes = "更新用户基本信息，请求体(用户信息)"
    )
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData updateUserInfo(@RequestBody UserInfo userInfo) {

        try {
            return tenantService.updateUserInfo(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("更新人员信息失败。失败原因：{},入参：{}", e, userInfo.toString());
        }
        return ResponseData.errorResponse;
    }

    @ApiOperation(
        value = "退出租户",
        notes = "退出租户，请求示例：{\"topUnit\":\"f0c0368da826434bbb158ed2ef0b1726\"}"
    )
    @RequestMapping(value = "/quitTenant", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData quitTenant(@RequestBody Map<String, Object> paraMaps) {
        String topUnit = MapUtils.getString(paraMaps, "topUnit");
        if (StringUtils.isBlank(topUnit)) {
            return ResponseData.makeErrorMessage("参数topUnit不能为空");
        }
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_USER_NOT_LOGIN, "当前用户未登录");
        }
        try {
            return tenantService.quitTenant(topUnit, userCode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("退出租户失败。失败原因：{},入参：userCode={},topUnit={}", e, userCode, topUnit);
        }
        return ResponseData.errorResponse;
    }


    @ApiOperation(
        value = "把成员移除租户",
        notes = "把成员移除租户，请求示例：{\"userCode\":\"U6n6uge0\",\"topUnit\":\"f0c0368da826434bbb158ed2ef0b1726\"}"
    )
    @RequestMapping(value = "/removeTenantMember", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData removeTenantMember(@RequestBody Map<String, Object> paraMaps) {
        String topUnit = MapUtils.getString(paraMaps, "topUnit");
        String userCode = MapUtils.getString(paraMaps, "userCode");
        if (StringUtils.isAnyBlank(topUnit, userCode)) {
            return ResponseData.makeErrorMessage("参数topUnit,userCode不能为空");
        }
        try {
            return tenantService.removeTenantMember(topUnit, userCode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("移除租户失败。失败原因：{},入参：userCode={},topUnit={}", e, userCode, topUnit);
        }
        return ResponseData.errorResponse;
    }

    @ApiOperation(
        value = "租户转让申请",
        notes = "租户转让申请，请求体(租户转让记录信息)"
    )
    @RequestMapping(value = "/businessTenant", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData businessTenant(@RequestBody @Validated TenantBusinessLog tenantBusinessLog) {

        try {
            return tenantService.businessTenant(tenantBusinessLog);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("租户转让申请失败。失败原因：{},入参：tenantBusinessLog={}", e, tenantBusinessLog.toString());
        }
        return ResponseData.makeErrorMessage("租户转让申请失败");
    }


    @ApiOperation(
        value = "分页展示租户列表",
        notes = "分页展示租户列表"
    )
    @RequestMapping(value = "/pageListTenantApply", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<TenantInfo> pageListTenantApply(PageListTenantInfoQo tenantInfo, PageDesc pageDesc) {

        return tenantService.pageListTenantApply(tenantInfo, pageDesc);

    }

    @ApiOperation(
        value = "分页展示租户成员列表",
        notes = "分页展示租户成员列表"
    )
    @RequestMapping(value = "/pageListTenantMember", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Object> pageListTenantMember(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> parameters = collectRequestParameters(request);
        return tenantService.pageListTenantMember(parameters, pageDesc);

    }

    @ApiOperation(
        value = "设置租户成员角色",
        notes = "设置租户成员角色"
    )
    @RequestMapping(value = "/assignTenantRole", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData assignTenantRole(@RequestBody TenantMemberQo tenantMemberQo) {

        try {
            return tenantService.assignTenantRole(tenantMemberQo);
        } catch (ObjectException obe) {
            return ResponseData.makeErrorMessage(obe.getExceptionCode(), obe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("设置租户成员角色出错，错误原因:{},入参:{}", e, tenantMemberQo.toString());
        }
        return ResponseData.makeErrorMessage("设置租户成员角色出错");

    }

    @ApiOperation(
        value = "移除租户成员角色",
        notes = "移除租户成员角色"
    )
    @RequestMapping(value = "/deleteTenantRole", method = RequestMethod.DELETE)
    @WrapUpResponseBody
    public ResponseData deleteTenantRole(TenantMemberQo tenantMemberQo) {

        try {
            return tenantService.deleteTenantRole(tenantMemberQo);
        } catch (ObjectException obe) {
            return ResponseData.makeErrorMessage(obe.getExceptionCode(), obe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除租户成员角色出错，错误原因:{},入参:{}", e, tenantMemberQo.toString());
        }
        return ResponseData.makeErrorMessage("删除租户成员角色出错");

    }

    @ApiOperation(
        value = "获取用户所在租户",
        notes = "获取用户所在租户"
    )
    @RequestMapping(value = "/userTenants", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData userTenants(HttpServletRequest request) {

        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录!");
        }
        try {
            List<Map> userTenants = tenantService.userTenants(userCode);
            return ResponseData.makeResponseData(userTenants);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取用户所在租户出错，错误原因:{},入参:{}", e, userCode);
        }
        return ResponseData.makeErrorMessage("获取用户所在租户出错");

    }


    @ApiOperation(
        value = "获取租户下的应用列表",
        notes = "获取租户下的应用列表"
    )
    @RequestMapping(value = "/listTenantApplication", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData listTenantApplication(@RequestParam("topUnit") String topUnit) {

        try {
            return tenantService.listTenantApplication(topUnit);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取租户下的应用列表出错，错误原因:{},入参:{}", e, topUnit);
        }
        return ResponseData.makeErrorMessage("获取租户下的应用列表失败!");

    }


    @ApiOperation(
        value = "查询租户信息",
        notes = "根据unitName模糊查询租户信息"
    )
    @RequestMapping(value = "/pageListTenants", method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult pageListTenants(@ParamName("unitName") String unitName, PageDesc pageDesc) {
        if (StringUtils.isBlank(unitName)) {
            throw new ObjectException("unitName不能为空");
        }
        return tenantService.pageListTenants(unitName, pageDesc);

    }

    @ApiOperation(
        value = "查询用户信息",
        notes = "查询租户信息，只能根据userCode，userName，regCellPhone精确查找，unitCode:必传 当前用户所在租户topUnit"
    )
    @RequestMapping(value = "/findUsers", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData findUsers(HttpServletRequest request) {
        Map<String, Object> paramMap = collectRequestParameters(request);

        return tenantService.findUsers(paramMap);

    }


    @ApiOperation(
        value = "修改租户信息",
        notes = "修改租户信息"
    )
    @RequestMapping(value = "/updateTenant", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData updateTenant(@RequestBody TenantInfo tenantInfo) {
        return tenantService.updateTenant(tenantInfo);

    }

    @ApiOperation(value = "获取用户登录信息", notes = "是对/mainframe/currentuser接口的扩展")
    @RequestMapping(value = {"/currentuser"}, method = {RequestMethod.GET})
    @WrapUpResponseBody
    public Object getCurrentUser(HttpServletRequest request) {
        Object ud = WebOptUtils.getLoginUser(request);
        if (ud == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户没有登录或者超时，请重新登录！");
        }
        String userCode = "";
        //tenantRole和userTenant信息随时有可能改变，所以不建议放到SecurityContext中
        if (ud instanceof CentitUserDetails) {
            //补充tenantRole字段信息
            CentitUserDetails centitUserDetails = (CentitUserDetails) ud;
            JSONObject userInfo = centitUserDetails.getUserInfo();
            userCode = userInfo.getString("userCode");
            String topUnitCode = centitUserDetails.getTopUnitCode();
            String tenantRole = "";
            if (StringUtils.isNotBlank(topUnitCode)) {
                tenantRole = tenantPowerManage.userTenantRole(topUnitCode);
            }
            userInfo.put("tenantRole", tenantRole);
        }
        //补充userTenants字段信息
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(ud));
        List<Map> userTenants = tenantService.userTenants(userCode);
        jsonObject.put("userTenants", userTenants);
        return jsonObject;
    }
}
