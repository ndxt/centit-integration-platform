package com.centit.framework.ip.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.TopUnitSecurityMetadata;
import com.centit.framework.system.po.DataDictionary;
import com.centit.framework.system.po.OsInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.service.OptInfoManager;
import com.centit.framework.system.service.OsInfoManager;
import com.centit.framework.system.service.SysUserManager;
import com.centit.framework.system.service.UserSettingManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author codefan
 */
@Controller
@RequestMapping("/platform")
@Api(tags = "系统数据服务接口", value = "系统数据服务接口")
public class PlatformDataController extends BaseController {

    @Autowired
    private UserSettingManager userSettingManager;

    /*@Autowired
    private SysRoleManager sysRoleManager;*/

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    private SysUserManager sysUserManager;

    @Autowired
    protected OsInfoManager osInfoManager;

    @Autowired
    protected OptInfoManager optInfoManager;

    @RequestMapping
    @WrapUpResponseBody
    public String apiInfo() {
        return "This the apis for platform data.";
    }

    /**
     * 更新用户信息
     *
     * @param userInfo 新的用户信息对象
     */
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息。")
    @ApiImplicitParam(
        name = "userInfo", value = "json格式，用户对象信息", required = true,
        paramType = "body", dataTypeClass = UserInfo.class)
    @RequestMapping(value = "/userinfo", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public void updateUserInfo(@RequestBody UserInfo userInfo) {
        UserInfo dbUserInfo = sysUserManager.getObjectById(userInfo.getUserCode());
        if (null == dbUserInfo) {
            throw new ObjectException(userInfo, ResponseData.ERROR_NOT_FOUND,
                "当前用户不存在");
        }
        //这个接口不能修改用户的主机构，只能修改其他信息
        String primaryUnit = dbUserInfo.getPrimaryUnit();
        userInfo.setPrimaryUnit(primaryUnit);
        sysUserManager.updateUserInfo(userInfo);
    }

    /**
     * 获取用户某个业务相关的所以用户设置
     *
     * @param optID    业务ID
     * @param userCode 用户代码
     * @return json
     */
    @ApiOperation(value = "获取用户某个业务相关的所以用户设置", notes = "获取用户某个业务相关的所以用户设置。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "optID", value = "业务ID",
            required = true, paramType = "path", dataType = "String")
    })
    @RequestMapping(value = "/usersettings/{userCode}/{optID}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UserSetting> getUserAllSettings(@PathVariable String optID,
                                                @PathVariable String userCode) {
        return userSettingManager.getUserSettings(userCode, optID);
    }

    /**
     * 获取用户设置
     *
     * @param userCode  用户代码
     * @param paramCode 设置编码
     * @return json
     */
    @ApiOperation(value = "获取用户设置", notes = "根据用户代码和参数代码获取用户设置。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "paramCode", value = "设置编码",
            required = true, paramType = "path", dataType = "String")
    })
    @RequestMapping(value = "/usersetting/{userCode}/{paramCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public UserSetting getUserSetting(@PathVariable String userCode,
                                      @PathVariable String paramCode) {
        return userSettingManager.getUserSetting(userCode, paramCode);
    }

    /**
     * 设置用户设置
     *
     * @param settingJson json格式的用户设置对象信息
     */
    @ApiOperation(value = "设置用户设置", notes = "设置用户设置。")
    @ApiImplicitParam(
        name = "settingJson", value = "json格式，用户设置对象信息", required = true,
        paramType = "body", dataType = "String")
    @RequestMapping(value = "/usersetting", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public void setUserSetting(@RequestBody String settingJson) {
        UserSetting us = JSON.parseObject(settingJson, UserSetting.class);
        if (us != null) {
            //userSettingManager.saveUserSetting(us);
            platformEnvironment.saveUserSetting(us);
        } else {
            throw new ObjectException(
                ResponseData.ERROR_USER_NOT_LOGIN, "无法获取当前用户信息！");
        }
    }

    /**
     * 获取用户的业务菜单里的所有操作方法
     *
     * @param optid    业务菜单ID
     * @param userCode 用户代码
     * @param asAdmin  是否为管理员
     * @return json
     */
    @ApiOperation(value = "获取用户的业务菜单里的所有操作方法", notes = "获取用户的业务菜单里的所有操作方法。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "optid", value = "业务菜单ID",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "asAdmin", value = "是否为管理员",
            paramType = "query", dataType = "Boolean")
    })
    @RequestMapping(value = "/usermenu/{optid}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOptInfo> listUserMenuOptInfos(@PathVariable String optid,
                                                         @PathVariable String userCode, boolean asAdmin) {
        return platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, optid, asAdmin);
    }

    /**
     * 校验用户密码
     *
     * @param userCode 用户代码
     * @param jsonData json字符串 里面的key必须有password
     * @return boolean
     */
    @ApiOperation(value = "校验用户密码", notes = "校验用户密码。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "jsonData", value = "json字符串 里面的key必须有password",
            required = true, paramType = "body", dataType = "String")
    })
    @RequestMapping(value = "/checkpassword/{userCode}", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public boolean checkUserPassword(@PathVariable String userCode, @RequestBody String jsonData) {
        JSONObject json = (JSONObject) JSON.parse(jsonData);
        String password = StringBaseOpt.objectToString(json.get("password"));
        //String newPassword = StringBaseOpt.objectToString(json.get("newPassword"));
        return platformEnvironment.checkUserPassword(userCode, password);
    }

    /**
     * 修改用户密码
     *
     * @param userCode 用户代码
     * @param jsonData json字符串 里面的key必须有newPassword
     */
    @ApiOperation(value = "修改用户密码", notes = "修改用户密码。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "jsonData", value = "json字符串 里面的key必须有newPassword",
            required = true, paramType = "body", dataType = "String")
    })
    @RequestMapping(value = "/changepassword/{userCode}", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public void changeUserPassword(@PathVariable String userCode, @RequestBody String jsonData) {
        JSONObject json = (JSONObject) JSON.parse(jsonData);
        //String password = StringBaseOpt.objectToString(json.get("password"));
        String newPassword = StringBaseOpt.objectToString(json.get("newPassword"));
        //if(platformEnvironment.checkUserPassword(userCode, password)){
        platformEnvironment.changeUserPassword(userCode, newPassword);
    }

    /**
     * 获取租户下的所有的用户
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下的所有的用户", notes = "获取租户下所有的用户。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/allusers/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserInfo> listAllUsers(@PathVariable String topUnit) {
        return platformEnvironment.listAllUsers(topUnit);
    }

    /**
     * 获取租户下所有的机构
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有的机构", notes = "获取租户下所有的机构。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/allunits/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUnitInfo> listAllUnits(@PathVariable String topUnit) {
        return platformEnvironment.listAllUnits(topUnit);
    }

    /**
     * 获取租户下所有的用户机构关系
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有的用户机构关系", notes = "获取租户下所有的用户机构关系。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/alluserunits/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserUnit> listAllUserUnits(@PathVariable String topUnit) {
        return platformEnvironment.listAllUserUnits(topUnit);
    }

    /**
     * 获取用户所有的租户
     *
     * @param userCode 用户代码
     * @return json
     */
    @ApiOperation(value = "获取用户所有的租户", notes = "获取用户所有的租户。")
    @ApiImplicitParam(
        name = "userCode", value = "用户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/usertopunits/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUnitInfo> listUserTopUnits(@PathVariable String userCode) {
        return platformEnvironment.listUserTopUnits(userCode);
    }

    /**
     * 获取租户下的用户所在的机构
     *
     * @param topUnit  租户代码
     * @param userCode 用户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下的用户所在的机构", notes = "获取租户下的用户所在的机构。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String")
    })
    @RequestMapping(value = "/userunits/{topUnit}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserUnit> listUserUnits(@PathVariable String topUnit, @PathVariable String userCode) {
        return platformEnvironment.listUserUnits(topUnit, userCode);
    }

    /**
     * 获取机构下的所有用户
     *
     * @param appName  客户端名称
     * @param unitCode 机构代码
     * @return json
     */
    @ApiOperation(value = "获取机构下的所有用户", notes = "获取机构下的所有用户。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value = "客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "unitCode", value = "机构代码",
            required = true, paramType = "path", dataType = "String")
    })
    @RequestMapping(value = "/unitusers/{appName}/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserUnit> listUnitUsers(@PathVariable String appName, @PathVariable String unitCode) {
        return platformEnvironment.listUnitUsers(unitCode);
    }


    /**
     * 获取租户下所有的机构
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有的机构", notes = "获取租户下所有的机构。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/unitrepo/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUnitInfo> getUnitRepo(@PathVariable String topUnit) {
        return platformEnvironment.listAllUnits(topUnit);
    }

    /**
     * 获取租户下所有的用户
     *
     * @param topUnit 租户代码
     * @return json
     *//*
    @ApiOperation(value = "获取租户下所有的用户", notes = "获取租户下所有的用户。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/userrepo/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserInfo> getUserRepo(@PathVariable String topUnit) {
        return platformEnvironment.listAllUsers(topUnit);
    }*/

    /**
     * 获取租户下所有的角色
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有的角色", notes = "获取租户下所有的角色。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/rolerepo/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IRoleInfo> getRoleRepo(@PathVariable String topUnit) {
        return platformEnvironment.listAllRoleInfo(topUnit);
    }

    /**
     * 获取租户下用户的所有角色
     *
     * @param topUnit  租户代码
     * @param userCode 用户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下用户的所有角色", notes = "获取租户下用户的所有角色。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String")}
    )
    @RequestMapping(value = "/userroles/{topUnit}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserRole> listUserRoles(@PathVariable String topUnit, @PathVariable String userCode) {
        return platformEnvironment.listUserRoles(topUnit, userCode);
    }

    /**
     * 获取租户下所有的业务菜单
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取用户权限的业务菜单", notes = "获取用户权限的业务菜单。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String")}
    )
    @RequestMapping(value = "/useroptinfs/{topUnit}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOptInfo> listUserOptinfos(@PathVariable String topUnit, @PathVariable String userCode) {
        return optInfoManager.listUserOptinfos(topUnit, userCode);
    }

    /**
     * @param topUnit  租户代码
     * @param userCode 用户代码
     * @return
     */
    @ApiOperation(value = "获取用户权限操作方法信息", notes = "获取用户权限操作方法信息。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String")}
    )
    @RequestMapping(value = "/userpowers/{topUnit}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOptMethod> listUserPowers(@PathVariable String topUnit, @PathVariable String userCode) {
        return sysUserManager.listUserPowers(topUnit, userCode);
    }

    /**
     * @param userCode 用户代码
     * @return
     */
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息。")
    @ApiImplicitParam(
        name = "userCode", value = "用户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/userinfo/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public UserInfo getUserInfo(@PathVariable String userCode) {
        return sysUserManager.getObjectById(userCode);
    }

    /**
     * 验证租户下用户权限
     *
     * @param topUnit   租户代码
     * @param userCode  用户代码
     * @param accessUrl 用户访问url
     * @param request   HttpServletRequest
     * @return 用户是否有权限访问这个url
     */
    @ApiOperation(value = "验证租户下用户权限", notes = "验证租户下用户权限。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "accessUrl", value = "用户访问Url",
            required = true, paramType = "query", dataType = "String")}
    )
    @RequestMapping(value = "/checkuserpower/{topUnit}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public boolean checkUserAccessPower(@PathVariable String topUnit, @PathVariable String userCode,
                                        String accessUrl,
                                        HttpServletRequest request) {
        List<? extends IUserRole> userRoles = platformEnvironment.listUserRoles(topUnit, userCode);
        TopUnitSecurityMetadata metadata = CentitSecurityMetadata.securityMetadata.getCachedValue(topUnit);
        Collection<ConfigAttribute> needRoles = null;
        if (null != metadata) {
            needRoles = metadata.matchUrlToRole(accessUrl, request);
        }
        if (userRoles == null || needRoles == null) {
            return false;
        }
        for (ConfigAttribute attr : needRoles) {
            for (IUserRole role : userRoles) {
                if (StringUtils.equals(
                    CentitSecurityMetadata.ROLE_PREFIX + role.getRoleCode(),
                    attr.getAttribute())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取租户下角色的所有用户
     *
     * @param topUnit  租户代码
     * @param roleCode 角色代码
     * @return json
     */
    @ApiOperation(value = "获取租户下角色的所有用户", notes = "获取租户下角色的所有用户。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "roleCode", value = "角色代码",
            required = true, paramType = "path", dataType = "String")}
    )
    @RequestMapping(value = "/roleusers/{topUnit}/{roleCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserRole> listRoleUsers(@PathVariable String topUnit, @PathVariable String roleCode) {
        return platformEnvironment.listRoleUsers(topUnit, roleCode);
    }

    /**
     * 根据租户和用户获取用户和角色的关联关系
     *
     * @param topUnit  租户代码
     * @param userCode 用户代码
     * @return json
     */
    @ApiOperation(value = "根据租户和用户获取用户和角色的关联关系", notes = "根据租户和用户获取用户和角色的关联关系。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "topUnit", value = "租户代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户代码",
            required = true, paramType = "path", dataType = "String")}
    )
    @RequestMapping(value = "/userroleinfos/{topUnit}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUserRole> listUserRoleInfos(@PathVariable String topUnit, @PathVariable String userCode) {
        return platformEnvironment.listUserRoles(topUnit, userCode);
    }

    /**
     * 获取机构下的所有角色
     *
     * @param unitCode 机构代码
     * @return json
     */
    @ApiOperation(value = "获取机构下的所有角色", notes = "获取机构下的所有角色。")
    @ApiImplicitParam(
        name = "unitCode", value = "机构代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/unitroles/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUnitRole> listUnitRoles(@PathVariable String unitCode) {
        return platformEnvironment.listUnitRoles(unitCode);
    }

    /**
     * 获取角色下的所有机构
     *
     * @param roleCode 角色代码
     * @return json
     */
    @ApiOperation(value = "获取角色下的所有机构", notes = "获取角色下的所有机构。")
    @ApiImplicitParam(
        name = "roleCode", value = "角色代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/roleunits/{roleCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IUnitRole> listRoleUnits(@PathVariable String roleCode) {
        return platformEnvironment.listRoleUnits(roleCode);
    }

    /**
     * 获取租户下所有的业务菜单
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有的业务菜单", notes = "获取租户下所有的业务菜单。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/optinforepo/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOptInfo> getOptInfoRepo(@PathVariable String topUnit) {
        return platformEnvironment.listAllOptInfo(topUnit);
    }

    /**
     * 获取租户下所有的字典类型
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有的字典类型", notes = "获取租户下所有的字典类型。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/catalogs/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IDataCatalog> listAllDataCatalogs(@PathVariable String topUnit) {
        return platformEnvironment.listAllDataCatalogs(topUnit);
    }

    /**
     * 获取字典类型代码下的所有字典明细
     *
     * @param appName     客户端名称
     * @param catalogCode 字典类型代码
     * @return json
     */
    @ApiOperation(value = "获取字典类型代码下的所有字典明细", notes = "获取字典类型代码下的所有字典明细。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value = "客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "catalogCode", value = "字典类型代码",
            required = true, paramType = "path", dataType = "String")
    })
    @RequestMapping(value = "/dictionary/{appName}/{catalogCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IDataDictionary> listDataDictionaries(@PathVariable String appName,
                                                                @PathVariable String catalogCode,
                                                                HttpServletRequest request) {
        List<DataDictionary> dictionaries = (List<DataDictionary>) platformEnvironment.listDataDictionaries(catalogCode);
        String lang = WebOptUtils.getCurrentLang(request);
        dictionaries.forEach(dict -> {
            dict.setDataValue(dict.getLocalDataValue(lang));
        });
        return dictionaries;
    }

    /**
     * 获取租户下所有角色权限
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有角色权限", notes = "获取租户下所有角色权限。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/allrolepowers/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IRolePower> listAllRolePower(@PathVariable String topUnit) {
        return platformEnvironment.listAllRolePower(topUnit);
    }

    /**
     * 获取租户下所有操作方法
     *
     * @param topUnit 租户代码
     * @return json
     */
    @ApiOperation(value = "获取租户下所有操作方法", notes = "获取租户下所有操作方法。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/alloptmethods/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOptMethod> listAllOptMethod(@PathVariable String topUnit) {
        return platformEnvironment.listAllOptMethod(topUnit);
    }

    /**
     * 获取租户下所有操作数据范围设定
     *
     * @param topUnit
     * @return
     */
    @ApiOperation(value = "获取租户下所有操作数据范围设定", notes = "获取租户下所有操作数据范围设定。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/alloptdatascopes/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOptDataScope> listAllOptDataScopes(@PathVariable String topUnit) {
        return platformEnvironment.listAllOptDataScope(topUnit);
    }

    /**
     * 获取字典类型代码下的所有字典明细
     *
     * @param appName    客户端名称
     * @param queryParam 查询类型对应的值
     * @param qtype      查询类型:loginName、userCode、regEmail、regCellPhone 不传为:loginName
     * @return json
     */
    @ApiOperation(value = "获取字典类型代码下的所有字典明细", notes = "获取字典类型代码下的所有字典明细。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appName", value = "客户端名称（暂时未用到可随意传值）",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "queryParam", value = "查询类型对应的值",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "qtype", value = "查询类型:loginName、userCode、regEmail、regCellPhone 不传为:loginName",
            paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/userdetails/{appName}/{queryParam}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseMapData loadUserDetails(@PathVariable String appName,
                                           @PathVariable String queryParam, String qtype) {
        if (qtype == null) {
            qtype = "loginName";
        }
        CentitUserDetails userDetails = null;
        switch (qtype) {
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
        if (userDetails == null) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有指定的用户：" + qtype + "=" + queryParam);
        }

        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("userDetails", userDetails);
        //resData.addResponseData("userRoles", userDetails.getUserRoles());
        //resData.addResponseData("userSettings", userDetails.getUserSettings());
        //resData.addResponseData("userUnits", userDetails.getUserUnits());
        return resData;
    }

    /**
     * 获取租户下所有的业务系统信息
     *
     * @param topUnit
     * @return
     */
    @ApiOperation(value = "获取租户下所有的业务系统信息", notes = "获取租户下所有的业务系统信息。")
    @ApiImplicitParam(
        name = "topUnit", value = "租户代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/ipenvironment/osinfo/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<? extends IOsInfo> listAllOS(@PathVariable String topUnit) {
        return platformEnvironment.listOsInfos(topUnit);
    }
    @ApiOperation(value = "获取单个业务系统信息", notes = "获取单个业务系统信息。")
    @ApiImplicitParam(
        name = "osId", value = "系统ID",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/osinfo/{osId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public IOsInfo getOsInfo(@PathVariable String osId) {
        return platformEnvironment.getOsInfo(osId);
    }
    @ApiOperation(value = "创建单个业务系统信息", notes = "创建单个业务系统信息。")
    @RequestMapping(value = "/osinfo", method = RequestMethod.POST)
    @WrapUpResponseBody
    public IOsInfo addOsInfo(@RequestBody JSONObject osInfo) {
        return platformEnvironment.addOsInfo(osInfo);
    }
    @ApiOperation(value = "删除单个业务系统信息", notes = "删除单个业务系统信息。")
    @ApiImplicitParam(
        name = "osId", value = "系统ID",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/osinfo/{osId}", method = RequestMethod.DELETE)
    @WrapUpResponseBody
    public IOsInfo deleteOsInfo(@PathVariable String osId) {
        return platformEnvironment.deleteOsInfo(osId);
    }
    @ApiOperation(value = "更新单个业务系统信息", notes = "更新单个业务系统信息。")
    @RequestMapping(value = "/osinfo", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public IOsInfo updateOsInfo(@RequestBody JSONObject osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }


    /**
     * 新增菜单和操作
     * @param request HttpServletRequest
     * @throws IOException 异常
     *//*
    @ApiOperation(value="新增菜单和操作",notes="新增菜单和操作。")
    @RequestMapping(value = "/insertopt", method = RequestMethod.POST)
    @WrapUpResponseBody
    public void insertOpt(HttpServletRequest request) throws IOException {
        Map<String, Object> param = JSON.parseObject(request.getInputStream(),Map.class);
        List<OptInfo> optInfos = JSON.parseArray(param.get("optInfos").toString(),OptInfo.class);
        List<OptMethod> optMethods = JSON.parseArray(param.get("optMethods").toString(),OptMethod.class);
        platformEnvironment.insertOrUpdateMenu(optInfos, optMethods);
    }*/
}
