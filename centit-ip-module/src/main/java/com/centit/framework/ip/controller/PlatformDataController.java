package com.centit.framework.ip.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
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
import com.centit.framework.system.service.SysRoleManager;
import com.centit.framework.system.service.SysUserManager;
import com.centit.framework.system.service.UserSettingManager;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
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
public class PlatformDataController extends BaseController {

    @Resource
    @NotNull
    private UserSettingManager userSettingManager;

    @Resource
    @NotNull
    private SysRoleManager sysRoleManager;

    @Resource(name="platformEnvironment")
    @NotNull
    protected PlatformEnvironment platformEnvironment;

    @Resource
    @NotNull
    private SysUserManager sysUserManager;

    @Resource
    @NotNull
    protected OsInfoManager osInfoManager;

    @Resource
    @NotNull
    protected DatabaseInfoManager oatabaseInfoManager;

    @Resource
    @NotNull
    protected UserAccessTokenManager userAccessTokenManager;

    @RequestMapping
    @ResponseBody
    public String apiInfo() {
        return "This the apis for platform data.";
    }

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

    @RequestMapping(value = "/usersettings/{userCode}/{optID}",
			method = RequestMethod.GET)
    public void getUserAllSettings(@PathVariable String optID, @PathVariable String userCode,
			HttpServletResponse response) {

		JsonResultUtils.writeSingleDataJson(
				userSettingManager.getUserSettings(userCode, optID), response);
    }

    @RequestMapping(value = "/usersetting/{userCode}/{paramCode}",
			method = RequestMethod.GET)
    public void getUserSetting(@PathVariable String userCode,
			@PathVariable String paramCode,HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				userSettingManager.getUserSetting(userCode, paramCode), response);
    }

    @RequestMapping(value = "/allsetting",
        method = RequestMethod.GET)
    public void getUserSetting(HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
            userSettingManager.getAllSettings(), response);
    }

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


    @RequestMapping(value = "/allusers/{appName}",
			method = RequestMethod.GET)
    public void listAllUsers(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUsers(),response);
    }


    @RequestMapping(value = "/allunits/{appName}",
			method = RequestMethod.GET)
    public void listAllUnits(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUnits(),response);
    }

    @RequestMapping(value = "/alluserunits/{appName}",
			method = RequestMethod.GET)
    public void listAllUserUnits(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUserUnits(),response);
    }

    @RequestMapping(value = "/userunits/{appName}/{userCode}",
			method = RequestMethod.GET)
    public void listUserUnits(@PathVariable String appName,@PathVariable String userCode,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listUserUnits(userCode),response);
    }

    @RequestMapping(value = "/unitusers/{appName}/{unitCode}",
			method = RequestMethod.GET)
    public void listUnitUsers(@PathVariable String appName,@PathVariable String unitCode,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listUnitUsers(unitCode),response);
    }

    @RequestMapping(value = "/unitrepo/{appName}",
			method = RequestMethod.GET)
    public void getUnitRepo(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUnits(),response);
    }

    @RequestMapping(value = "/userrepo/{appName}",
			method = RequestMethod.GET)
    public void getUserRepo(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllUsers(),response);
    }

    @RequestMapping(value = "/rolerepo/{appName}",
			method = RequestMethod.GET)
    public void getRoleRepo(@PathVariable String appName,
			HttpServletResponse response) {

		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllRoleInfo(),response);
    }

    @RequestMapping(value = "/userroles/{userCode}",
            method = RequestMethod.GET)
    public void listUserRoles(@PathVariable String userCode,
                            HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listUserRoles(userCode),response);
    }

    @RequestMapping(value = "/roleusers/{roleCode}",
            method = RequestMethod.GET)
    public void listRoleUsers(@PathVariable String roleCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listRoleUsers(roleCode),response);
    }

    @RequestMapping(value = "/userroleinfos/{userCode}",
            method = RequestMethod.GET)
    public void listUserRoleInfos(@PathVariable String userCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listUserRoles(userCode),response);
    }

    @RequestMapping(value = "/roleuserinfos/{roleCode}",
            method = RequestMethod.GET)
    public void listRoleUserInfos(@PathVariable String roleCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listRoleUsers(roleCode),response);
    }

    @RequestMapping(value = "/unitroles/{unitCode}",
            method = RequestMethod.GET)
    public void listUnitRoles(@PathVariable String unitCode,
                              HttpServletResponse response) {

        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listUnitRoles(unitCode),response);
    }

    @RequestMapping(value = "/roleunits/{roleCode}",
            method = RequestMethod.GET)
    public void listRoleUnits(@PathVariable String roleCode,
                              HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
                platformEnvironment.listRoleUnits(roleCode),response);
    }

    @RequestMapping(value = "/optinforepo/{appName}",
			method = RequestMethod.GET)
    public void getOptInfoRepo(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllOptInfo(),response);
    }


    @RequestMapping(value = "/catalogs/{appName}",
			method = RequestMethod.GET)
    public void listAllDataCatalogs(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listAllDataCatalogs(),response);
    }

    @RequestMapping(value = "/dictionary/{appName}/{catalogCode}",
			method = RequestMethod.GET)
    public void listDataDictionaries(@PathVariable String appName,@PathVariable String catalogCode,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				platformEnvironment.listDataDictionaries(catalogCode),response);
    }

    @RequestMapping(value = "/allrolepowers/{appName}",
			method = RequestMethod.GET)
    public void listAllRolePower(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				sysRoleManager.listAllRolePowers(),response);
    }

    @RequestMapping(value = "/alloptmethods/{appName}",
			method = RequestMethod.GET)
    public void listAllOptMethod(@PathVariable String appName,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				sysRoleManager.listAllOptMethods(),response);
    }


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
        resData.addResponseData("userPin", userDetails.getUserInfo().getUserPin());
		JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @RequestMapping(value = "/ipenvironment/databaseinfo",
			method = RequestMethod.GET)
    public void listAllDatabase(HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				oatabaseInfoManager.listDatabase(),response);
    }

    @RequestMapping(value = "/ipenvironment/osinfo",
			method = RequestMethod.GET)
    public void listAllOS(
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				osInfoManager.listObjects(),response);
    }

    @RequestMapping(value = "/ipenvironment/userToken/{tokenId}",
			method = RequestMethod.GET)
    public void getUserToken(@PathVariable String tokenId,
			HttpServletResponse response) {
		JsonResultUtils.writeSingleDataJson(
				userAccessTokenManager.getObjectById(tokenId),response);
    }

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
    @RequestMapping(value = "/insertopt", method = RequestMethod.POST)
    public void insertOpt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> param = JSON.parseObject(request.getInputStream(),Map.class);
		List<OptInfo> optInfos = JSON.parseArray(param.get("optInfos").toString(),OptInfo.class);
		List<OptMethod> optMethods = JSON.parseArray(param.get("optMethods").toString(),OptMethod.class);
		platformEnvironment.insertOrUpdateMenu(optInfos, optMethods);
		JsonResultUtils.writeSuccessJson(response);
    }
}
