package com.centit.framework.ip.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.ip.service.DatabaseInfoManager;
import com.centit.framework.ip.service.OsInfoManager;
import com.centit.framework.ip.service.UserAccessTokenManager;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.service.OptInfoManager;
import com.centit.framework.system.service.SysRoleManager;
import com.centit.framework.system.service.SysUserManager;
import com.centit.framework.system.service.UserSettingManager;
import com.centit.support.algorithm.StringBaseOpt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author codefan
 *
 */
@Controller
@RequestMapping("/platform")
@Api(tags= "系统数据服务接口",value = "系统数据服务接口")
public class PlatformDataController extends BaseController {

    @Autowired
    private UserSettingManager userSettingManager;

    @Autowired
    private SysRoleManager sysRoleManager;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    private SysUserManager sysUserManager;

    @Autowired
    protected OsInfoManager osInfoManager;

    @Autowired
    protected OptInfoManager optInfoManager;

    @Autowired
    protected DatabaseInfoManager oatabaseInfoManager;

    @Autowired
    protected UserAccessTokenManager userAccessTokenManager;

    @RequestMapping
    @ResponseBody
    public String apiInfo() {
        return "This the apis for platform data.";
    }

    /**
     * 更新用户信息
     * @param userInfo 新的用户信息对象
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="更新用户信息",notes="更新用户信息。")
    @ApiImplicitParam(
        name = "userInfo", value="json格式，用户对象信息",required = true,
        paramType = "body", dataTypeClass= UserInfo.class)
    @RequestMapping(value = "/userinfo", method = RequestMethod.PUT)
    public void updateUserInfo(@RequestBody UserInfo userInfo,
                     HttpServletRequest request, HttpServletResponse response) {

        UserInfo dbUserInfo = sysUserManager.getObjectById(userInfo.getUserCode());
        if (null == dbUserInfo) {
            JsonResultUtils.writeErrorMessageJson("当前用户不存在", response);
            return;
        }
        //这个接口不能修改用户的主机构，只能修改其他信息
        String  primaryUnit = dbUserInfo.getPrimaryUnit();
        dbUserInfo.copyNotNullProperty(userInfo);
        dbUserInfo.setPrimaryUnit(primaryUnit);
        sysUserManager.updateUserInfo(dbUserInfo);
        JsonResultUtils.writeSuccessJson(response);
    }

    /**
     * 获取用户某个业务相关的所以用户设置
     * @param optID 业务ID
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取用户某个业务相关的所以用户设置",notes="获取用户某个业务相关的所以用户设置。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value="用户代码",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "optID", value="业务ID",
            required = true, paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/usersettings/{userCode}/{optID}",
			method = RequestMethod.GET)
    public void getUserAllSettings(@PathVariable String optID, @PathVariable String userCode,
			HttpServletResponse response) {

		JsonResultUtils.writeSingleDataJson(
				userSettingManager.getUserSettings(userCode, optID), response);
    }

    /**
     * 获取用户设置
     * @param userCode 用户代码
     * @param paramCode 设置编码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取用户设置",notes="根据用户代码和参数代码获取用户设置。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value="用户代码",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "paramCode", value="设置编码",
            required = true, paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/usersetting/{userCode}/{paramCode}",
			method = RequestMethod.GET)
    public void getUserSetting(@PathVariable String userCode,
			@PathVariable String paramCode,HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				userSettingManager.getUserSetting(userCode, paramCode), response);
    }

    /**
     * 获取所有的用户设置
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的用户设置",notes="获取所有的用户设置。")
    @RequestMapping(value = "/allsetting",
        method = RequestMethod.GET)
    public void getUserSetting(HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
            userSettingManager.getAllSettings(), response);
    }

    /**
     * 设置用户设置
     * @param settingJson json格式的用户设置对象信息
     * @param response HttpServletResponse
     */
    @ApiOperation(value="设置用户设置",notes="设置用户设置。")
    @ApiImplicitParam(
        name = "settingJson", value="json格式，用户设置对象信息",required = true,
        paramType = "body", dataType = "String")
    @RequestMapping(value = "/usersetting",
			method = RequestMethod.PUT)
    public void setUserSetting(@RequestBody String settingJson,HttpServletResponse response) {
		UserSetting us = JSON.parseObject(settingJson, UserSetting.class);
		if(us!=null){
			//userSettingManager.saveUserSetting(us);
            platformEnvironment.saveUserSetting(us);
			JsonResultUtils.writeSuccessJson(response);
		}else {
            JsonResultUtils.writeErrorMessageJson("put 的用户设置不正确！", response);
        }
    }

    /**
     * 获取用户的业务菜单里的所有操作方法
     * @param optid 业务菜单ID
     * @param userCode 用户代码
     * @param asAdmin 是否为管理员
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取用户的业务菜单里的所有操作方法",notes="获取用户的业务菜单里的所有操作方法。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "optid", value="业务菜单ID",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "userCode", value="用户代码",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "asAdmin", value="是否为管理员",
            paramType = "query", dataType= "Boolean")
    })
    @RequestMapping(value = "/usermenu/{optid}/{userCode}",
			method = RequestMethod.GET)
    public void listUserMenuOptInfos(@PathVariable String optid, @PathVariable String userCode, boolean asAdmin,
			HttpServletResponse response) {

		List<? extends IOptInfo> menuFunsByUser = null;
        if(StringUtils.isBlank(optid) || "null".equals(optid)){
			 menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userCode ,asAdmin);
		}else {
			 menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, optid, asAdmin);
		}

		JsonResultUtils.writeSingleDataJson(menuFunsByUser, response);
    }

    /**
     * 校验用户密码
     * @param userCode 用户代码
     * @param jsonData json字符串 里面的key必须有password
     * @param response HttpServletResponse
     */
    @ApiOperation(value="校验用户密码",notes="校验用户密码。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value="用户代码",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "jsonData", value="json字符串 里面的key必须有password",
            required = true, paramType = "body", dataType= "String")
    })
    @RequestMapping(value = "/checkpassword/{userCode}",
			method = RequestMethod.PUT)
    public void changeUserPassword(@PathVariable String userCode,@RequestBody String jsonData ,
			HttpServletResponse response) {
		JSONObject json = (JSONObject)JSON.parse(jsonData);
		String password = StringBaseOpt.objectToString(json.get("password"));
		//String newPassword = StringBaseOpt.objectToString(json.get("newPassword"));
		JsonResultUtils.writeOriginalObject(
				platformEnvironment.checkUserPassword(userCode, password), response);
    }

    /**
     * 修改用户密码
     * @param userCode 用户代码
     * @param jsonData json字符串 里面的key必须有newPassword
     * @param response HttpServletResponse
     */
    @ApiOperation(value="修改用户密码",notes="修改用户密码。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value="用户代码",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "jsonData", value="json字符串 里面的key必须有newPassword",
            required = true, paramType = "body", dataType= "String")
    })
    @RequestMapping(value = "/changepassword/{userCode}",
			method = RequestMethod.PUT)
    public void checkUserPassword(@PathVariable String userCode,@RequestBody String jsonData ,
			HttpServletResponse response) {
		JSONObject json = (JSONObject)JSON.parse(jsonData);
		//String password = StringBaseOpt.objectToString(json.get("password"));
		String newPassword = StringBaseOpt.objectToString(json.get("newPassword"));
		//if(platformEnvironment.checkUserPassword(userCode, password)){
			platformEnvironment.changeUserPassword(userCode, newPassword);
			JsonResultUtils.writeSuccessJson(response);
		//}else
		//    JsonResultUtils.writeErrorMessageJson("用户提供的密码不正确！", response);
    }

    /**
     * 获取所有的用户
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的用户",notes="获取所有的用户。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/allusers/{appName}",
			method = RequestMethod.GET)
    public void listAllUsers(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUsers(),response);
    }

    /**
     * 获取所有的机构
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的机构",notes="获取所有的机构。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/allunits/{appName}",
			method = RequestMethod.GET)
    public void listAllUnits(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUnits(),response);
    }

    /**
     * 获取所有的用户机构关系
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的用户机构关系",notes="获取所有的用户机构关系。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/alluserunits/{appName}",
			method = RequestMethod.GET)
    public void listAllUserUnits(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUserUnits(),response);
    }

    /**
     * 获取用户所以在的机构
     * @param appName 客户端名称
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取用户所以在的机构",notes="获取用户所以在的机构。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value="客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "userCode", value="用户代码",
            required = true, paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/userunits/{appName}/{userCode}",
			method = RequestMethod.GET)
    public void listUserUnits(@PathVariable String appName,@PathVariable String userCode,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listUserUnits(userCode),response);
    }

    /**
     * 获取机构下的所有用户
     * @param appName 客户端名称
     * @param unitCode 机构代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取机构下的所有用户",notes="获取机构下的所有用户。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value="客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "unitCode", value="机构代码",
            required = true, paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/unitusers/{appName}/{unitCode}",
			method = RequestMethod.GET)
    public void listUnitUsers(@PathVariable String appName,@PathVariable String unitCode,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listUnitUsers(unitCode),response);
    }


    /**
     * 获取所有的机构
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的机构",notes="获取所有的机构。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/unitrepo/{appName}",
			method = RequestMethod.GET)
    public void getUnitRepo(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUnits(),response);
    }

    /**
     * 获取所有的用户
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的用户",notes="获取所有的用户。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/userrepo/{appName}",
			method = RequestMethod.GET)
    public void getUserRepo(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUsers(),response);
    }

    /**
     * 获取所有的角色
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的角色",notes="获取所有的角色。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/rolerepo/{appName}",
			method = RequestMethod.GET)
    public void getRoleRepo(@PathVariable String appName,
			HttpServletResponse response) {

		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllRoleInfo(),response);
    }

    /**
     * 获取用户下的所有角色
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取用户下的所有角色",notes="获取用户下的所有角色。")
    @ApiImplicitParam(
        name = "userCode", value="用户代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/userroles/{userCode}",
            method = RequestMethod.GET)
    public void listUserRoles(@PathVariable String userCode,
                            HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listUserRoles(userCode),response);
    }

    /**
     * 获取角色下的所有用户
     * @param roleCode 角色代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取角色下的所有用户",notes="获取角色下的所有用户。")
    @ApiImplicitParam(
        name = "roleCode", value="角色代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/roleusers/{roleCode}",
            method = RequestMethod.GET)
    public void listRoleUsers(@PathVariable String roleCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listRoleUsers(roleCode),response);
    }

    /**
     * 根据用户获取用户和角色的关联关系
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="根据用户获取用户和角色的关联关系",notes="根据用户获取用户和角色的关联关系。")
    @ApiImplicitParam(
        name = "userCode", value="用户代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/userroleinfos/{userCode}",
            method = RequestMethod.GET)
    public void listUserRoleInfos(@PathVariable String userCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listUserRoles(userCode),response);
    }

    /**
     * 根据角色获取用户和角色的关联关系
     * @param roleCode 角色代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="根据角色获取用户和角色的关联关系",notes="根据角色获取用户和角色的关联关系。")
    @ApiImplicitParam(
        name = "roleCode", value="角色代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/roleuserinfos/{roleCode}",
            method = RequestMethod.GET)
    public void listRoleUserInfos(@PathVariable String roleCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listRoleUsers(roleCode),response);
    }

    /**
     * 获取机构下的所有角色
     * @param unitCode 机构代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取机构下的所有角色",notes="获取机构下的所有角色。")
    @ApiImplicitParam(
        name = "unitCode", value="机构代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/unitroles/{unitCode}",
            method = RequestMethod.GET)
    public void listUnitRoles(@PathVariable String unitCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listUnitRoles(unitCode),response);
    }

    /**
     * 获取角色下的所有机构
     * @param roleCode 角色代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取角色下的所有机构",notes="获取角色下的所有机构。")
    @ApiImplicitParam(
        name = "roleCode", value="角色代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/roleunits/{roleCode}",
            method = RequestMethod.GET)
    public void listRoleUnits(@PathVariable String roleCode,
                              HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listRoleUnits(roleCode),response);
    }


    /**
     * 获取所有的业务菜单
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的业务菜单",notes="获取所有的业务菜单。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/optinforepo/{appName}",
			method = RequestMethod.GET)
    public void getOptInfoRepo(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllOptInfo(),response);
    }

    /**
     * 获取所有的字典类型
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的字典类型",notes="获取所有的字典类型。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/catalogs/{appName}",
			method = RequestMethod.GET)
    public void listAllDataCatalogs(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllDataCatalogs(),response);
    }

    /**
     * 获取字典类型代码下的所有字典明细
     * @param appName 客户端名称
     * @param catalogCode 字典类型代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取字典类型代码下的所有字典明细",notes="获取字典类型代码下的所有字典明细。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value="客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "catalogCode", value="字典类型代码",
            required = true, paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/dictionary/{appName}/{catalogCode}",
			method = RequestMethod.GET)
    public void listDataDictionaries(@PathVariable String appName,@PathVariable String catalogCode,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listDataDictionaries(catalogCode),response);
    }

    /**
     * 获取所有角色权限
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有角色权限",notes="获取所有角色权限。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/allrolepowers/{appName}",
			method = RequestMethod.GET)
    public void listAllRolePower(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				sysRoleManager.listAllRolePowers(),response);
    }

    /**
     * 获取所有操作方法
     * @param appName 客户端名称
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有操作方法",notes="获取所有操作方法。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/alloptmethods/{appName}",
			method = RequestMethod.GET)
    public void listAllOptMethod(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				sysRoleManager.listAllOptMethods(),response);
    }


    @ApiOperation(value="获取所有操作数据范围设定",notes="获取所有操作数据范围设定。")
    @ApiImplicitParam(
        name = "appName", value="客户端名称（暂时未用到可随意传值）",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/alloptdatascopes/{appName}",
        method = RequestMethod.GET)
    public void listAllOptDataScopes(@PathVariable String appName,
                                 HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
            optInfoManager.listAllDataScope(),response);
    }
    /**
     * 获取字典类型代码下的所有字典明细
     * @param appName 客户端名称
     * @param queryParam 查询类型对应的值
     * @param qtype 查询类型:loginName、userCode、regEmail、regCellPhone 不传为:loginName
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取字典类型代码下的所有字典明细",notes="获取字典类型代码下的所有字典明细。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value="客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "queryParam", value="查询类型对应的值",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "qtype", value="查询类型:loginName、userCode、regEmail、regCellPhone 不传为:loginName",
            paramType = "query", dataType= "String")
    })
    @RequestMapping(value = "/userdetails/{appName}/{queryParam}",
			method = RequestMethod.GET)
    public void loadUserDetails(@PathVariable String appName,
			@PathVariable String queryParam,String qtype,
			HttpServletResponse response) {
		if(qtype==null) {
            qtype = "loginName";
        }
		CentitUserDetails userDetails = null;
		switch(qtype){
		case "loginName":
			userDetails = platformEnvironment.loadUserDetailsByLoginName(queryParam);
			break;
		case "userCode":
			userDetails = platformEnvironment.loadUserDetailsByUserCode(queryParam);
			break;
		case "regEmail":
			userDetails = platformEnvironment.loadUserDetailsByRegEmail(queryParam);
			break;
		case "regCellPhone":
			userDetails = platformEnvironment.loadUserDetailsByRegCellPhone(queryParam);
			break;
		default:
			userDetails = platformEnvironment.loadUserDetailsByLoginName(queryParam);
			break;
		}
		if(userDetails==null){
			JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_USER_LOGIN_ERROR,
					"没有指定的用户："+qtype+"="+queryParam, response);
			return;
		}

		ResponseMapData resData = new ResponseMapData();
		resData.addResponseData("userDetails", userDetails);
		//resData.addResponseData("userRoles", userDetails.getUserRoles());
        //resData.addResponseData("userSettings", userDetails.getUserSettings());
		resData.addResponseData("userUnits", userDetails.getUserUnits());
		JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @ApiOperation(value="所有的数据源信息",notes="获取所有的数据源信息。")
    @RequestMapping(value = "/ipenvironment/databaseinfo",
			method = RequestMethod.GET)
    public void listAllDatabase(HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				oatabaseInfoManager.listDatabase(),response);
    }

    @ApiOperation(value="所有的业务系统信息",notes="获取所有的业务系统信息。")
    @RequestMapping(value = "/ipenvironment/osinfo",
			method = RequestMethod.GET)
    public void listAllOS(
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				osInfoManager.listObjects(),response);
    }

    /**
     * 获取认证的用户信息
     * @param tokenId tokenId
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取认证的用户信息",notes="获取认证的用户信息。")
    @ApiImplicitParam(
        name = "tokenId", value="tokenId",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/ipenvironment/userToken/{tokenId}",
			method = RequestMethod.GET)
    public void getUserToken(@PathVariable String tokenId,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				userAccessTokenManager.getObjectById(tokenId),response);
    }

    /**
     * 获取所有的用户认证信息
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取所有的用户认证信息",notes="获取所有的用户认证信息。")
    @RequestMapping(value = "/ipenvironment/allUserToken",
        method = RequestMethod.GET)
    public void listAllUserToken(HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
            userAccessTokenManager.listObjects(),response);
    }

    /**
     * 新增菜单和操作
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException 异常
     */
    @ApiOperation(value="新增菜单和操作",notes="新增菜单和操作。")
    @RequestMapping(value = "/insertopt", method = RequestMethod.POST)
    public void insertOpt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> param = JSON.parseObject(request.getInputStream(),Map.class);
		List<OptInfo> optInfos = JSON.parseArray(param.get("optInfos").toString(),OptInfo.class);
		List<OptMethod> optMethods = JSON.parseArray(param.get("optMethods").toString(),OptMethod.class);
		platformEnvironment.insertOrUpdateMenu(optInfos, optMethods);
		JsonResultUtils.writeSuccessJson(response);
    }
}
