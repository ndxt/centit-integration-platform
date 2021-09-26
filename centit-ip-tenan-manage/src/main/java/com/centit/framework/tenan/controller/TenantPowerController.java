package com.centit.framework.tenan.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.tenan.service.TenantPowerManage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tenantPower")
@Api(
    tags = {"租户权限范围接口"},
    value = "租户权限范围接口"
)
public class TenantPowerController extends BaseController {

    @Autowired
    private TenantPowerManage tenantPowerManage;

    @ApiOperation(
        value = "判断当前用户是否为租户所有者",
        notes = "判断当前用户是否为租户所有者 topUnit:租户id"
    )
    @RequestMapping(value = "/userIsTenantOwner", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData userIsTenantOwner(@RequestParam("topUnit") String topUnit) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.userIsTenantOwner(topUnit));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("判断当前用户是否为租户所有者失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("判断当前用户是否为租户所有者失败!");
        }
    }

    @ApiOperation(
        value = "判断当前用户是否为租户管理员",
        notes = "判断当前用户是否为租户管理员 topUnit:租户id"
    )
    @RequestMapping(value = "/userIsTenantAdmin", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData userIsTenantAdmin(@RequestParam("topUnit")String topUnit) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.userIsTenantAdmin(topUnit));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("判断当前用户是否为租户管理员失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("判断当前用户是否为租户管理员失败!");
        }
    }

    @ApiOperation(
        value = "判断当前用户是否为租户成员",
        notes = "判断当前用户是否为租户成员 topUnit:租户id"
    )
    @RequestMapping(value = "/userIsTenantMember", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData userIsTenantMember(@RequestParam("topUnit")String topUnit) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.userIsTenantMember(topUnit));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("判断当前用户是否为租户成员失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("判断当前用户是否为租户成员失败!");
        }
    }

    @ApiOperation(
        value = "判断当前用户是否为应用管理员",
        notes = "判断当前用户是否为应用管理员 osId:应用id"
    )
    @RequestMapping(value = "/userIsApplicationAdmin", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData userIsApplicationAdmin(@RequestParam("osId")String osId) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.userIsApplicationAdmin(osId));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("判断当前用户是否为应用管理员失败,错误原因{},应用id：{}", e, osId);
            return ResponseData.makeErrorMessage("判断当前用户是否为应用管理员失败!");
        }
    }

    @ApiOperation(
        value = "判断当前用户是否为应用成员",
        notes = "判断当前用户是否为应用成员 osId:应用id"
    )
    @RequestMapping(value = "/userIsApplicationMember", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData userIsApplicationMember(@RequestParam("osId")String osId) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.userIsApplicationMember(osId));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("判断当前用户是否为应用成员失败,错误原因{},应用id：{}", e, osId);
            return ResponseData.makeErrorMessage("判断当前用户是否为应用成员失败!");
        }
    }

    @ApiOperation(
        value = "获取租户资源上限",
        notes = "获取租户资源上限 topUnit:租户id"
    )
    @RequestMapping(value = "/tenantResourceLimit", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData tenantResourceLimit(@RequestParam("topUnit")String topUnit) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.tenantResourceLimit(topUnit));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取租户资源上限失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("获取租户资源上限失败!");
        }
    }

    @ApiOperation(
        value = "租户已用资源",
        notes = "租户已用资源 topUnit:租户id"
    )
    @RequestMapping(value = "/tenantResourceUsed", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData tenantResourceUsed(@RequestParam("topUnit")String topUnit) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.tenantResourceUsed(topUnit));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取租户已用资源失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("获取租户已用资源失败!");
        }
    }

    @ApiOperation(
        value = "租户资源详情",
        notes = "租户资源详情。sourceType 资源类型，limit 资源限制个数，usedSource 已用资源个数，" +
            "useAble可用资源个数，isLimit 是否达到上限 true:达到上限 false：未达到上限"
    )
    @RequestMapping(value = "/tenantResourceDetails", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData tenantResourceDetails(@RequestParam("topUnit")String topUnit) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.tenantResourceDetails(topUnit));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取租户已用资源失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("获取租户已用资源失败!");
        }
    }
    @ApiOperation(
        value = "租户指定资源详情",
        notes = "租户指定资源详情。sourceType 资源类型，limit 资源限制个数，usedSource 已用资源个数，" +
            "useAble可用资源个数，isLimit 是否达到上限 true:达到上限 false：未达到上限"
    )
    @RequestMapping(value = "/specialResourceDetails", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData specialResourceDetails(@RequestParam("topUnit")String topUnit,@RequestParam("resourceType")String resourceType ) {

        try {
            return ResponseData.makeResponseData(tenantPowerManage.specialResourceDetails(topUnit,resourceType));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取租户指定资源详情失败,错误原因{},租户id：{}", e, topUnit);
            return ResponseData.makeErrorMessage("获取租户指定资源详情失败!");
        }
    }

}
