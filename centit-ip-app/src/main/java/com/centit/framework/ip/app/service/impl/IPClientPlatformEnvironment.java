package com.centit.framework.ip.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.staticsystem.po.*;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成平台客户端业务配置新, 所有的访问需要添加一个cache策略
 *
 * @author codefan
 */
public class IPClientPlatformEnvironment implements PlatformEnvironment {

    private Logger logger = LoggerFactory.getLogger(IPClientPlatformEnvironment.class);

    private String topOptId;

    public IPClientPlatformEnvironment() {

    }

    public void setTopOptId(String topOptId) {
        this.topOptId = topOptId;
    }

    private AppSession appSession;

    public AppSession getPlatAppSession() {
        return this.appSession;
    }

    public void createPlatAppSession(String appServerUrl, boolean needAuthenticated, String userCode, String password) {
        appSession = new AppSession(appServerUrl, needAuthenticated, userCode, password);
    }

    @Override
    public UserSetting getUserSetting(String userCode, String paramCode) {
        HttpReceiveJSON resJson = RestfulHttpRequest.getResponseData(
            appSession,
            "/platform/usersetting/" + userCode + "/" + paramCode);

        if (resJson == null)
            return null;
        return resJson.getDataAsObject(UserSetting.class);
    }

    /**
     * 获取全部个人设置
     *
     * @param userCode 用户编码
     * @return 个人设置列表
     */
    @Override
    public List<? extends IUserSetting> listUserSettings(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/usersettings/" + userCode + "/" + topOptId,
            UserSetting.class);
    }

    @Override
    public void saveUserSetting(IUserSetting userSetting) {
        RestfulHttpRequest.jsonPost(appSession, "/platform/usersetting",
            userSetting);
    }

    /*@Override
    public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {

        return listUserMenuOptInfosUnderSuperOptId(userCode, topOptId, asAdmin);
    }*/

    @Override
    public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId,
                                                             boolean asAdmin) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/usermenu/" + superOptId + "/" + userCode + "?asAdmin=" + asAdmin,
            OptInfo.class);

    }

    @Override
    public List<UserRole> listUserRoles(String topUnit, String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/userroles/" + topUnit + "/" + userCode,
            UserRole.class);
    }

    @Override
    public List<UserRole> listRoleUsers(String topUnit, String roleCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/roleusers/" + topUnit + "/" + roleCode,
            UserRole.class);
    }

    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/unitroles/" + unitCode,
            IUnitRole.class);
    }

    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/roleunits/" + roleCode,
            IUnitRole.class);
    }

    @Override
    public void changeUserPassword(String userCode, String userPassword) {

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("userCode", userCode);
        userInfo.put("password", userPassword);
        userInfo.put("newPassword", userPassword);
        RestfulHttpRequest.jsonPost(appSession,
            "/platform/changepassword/" + userCode,
            userInfo);
    }

    @Override
    public boolean checkUserPassword(String userCode, String userPassword) {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("userCode", userCode);
        userInfo.put("password", userPassword);
        userInfo.put("newPassword", userPassword);
        String sret = RestfulHttpRequest.jsonPost(
            appSession, "/platform/checkpassword/" + userCode,
            userInfo, true);
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(sret);
        return receiveJSON != null &&
            BooleanBaseOpt.castObjectToBoolean(receiveJSON.getData(), false);
    }

    @Override
    public List<UserInfo> listAllUsers(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/allusers/" + topUnit,
            UserInfo.class);
    }

    @Override
    public List<UnitInfo> listAllUnits(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/allunits/" + topUnit,
            UnitInfo.class);
    }

    @Override
    public List<UserUnit> listAllUserUnits(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/alluserunits/" + topUnit,
            UserUnit.class);
    }

    @Override
    public List<? extends IUnitInfo> listUserTopUnits(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/usertopunits/" + userCode,
            UnitInfo.class);
    }

    @Override
    public List<UserUnit> listUserUnits(String topUnit, String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/userunits/" + topUnit + "/" + userCode,
            UserUnit.class);
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/unitusers/" + topOptId + "/" + unitCode,
            UserUnit.class);
    }

    /**
     * 获取所有角色信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRoleInfo> listAllRoleInfo(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/rolerepo/" + topUnit,
            RoleInfo.class);
    }

    @Override
    public List<DataCatalog> listAllDataCatalogs(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/catalogs/" + topUnit,
            DataCatalog.class);
    }

    @Override
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/dictionary/" + topOptId + "/" + catalogCode,
            DataDictionary.class);
    }

    @Override
    public List<? extends IRolePower> listAllRolePower(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/allrolepowers/" + topUnit,
            RolePower.class);
    }

    /**
     * 获取业务操作信息
     *
     * @return List 业务信息
     */
    @Override
    public List<? extends IOptInfo> listAllOptInfo(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/optinforepo/" + topUnit,
            OptInfo.class);
    }

    @Override
    public List<? extends IOptMethod> listAllOptMethod(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/alloptmethods/" + topUnit,
            OptMethod.class);
    }

    /**
     * @return 所有的数据范围定义表达式
     */
    @Override
    public List<? extends IOptDataScope> listAllOptDataScope(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/alloptdatascopes/" + topUnit,
            OptDataScope.class);
    }

    private CentitUserDetails loadUserDetails(String queryParam, String qtype) {
        HttpReceiveJSON resJson = RestfulHttpRequest.getResponseData(
            appSession, "/platform/userdetails/" + topOptId + "/" + queryParam + "?qtype=" + qtype);

        if (resJson == null || resJson.getCode() != 0) {
            return null;
        }
        JsonCentitUserDetails userDetails =
            resJson.getDataAsObject("userDetails", JsonCentitUserDetails.class);
        //userDetails.getUserInfo().put("userPin", resJson.getDataAsString("userPin"));
        userDetails.setUserUnits(
            (JSONArray) resJson.getData("userUnits"));//, UserUnit.class));
        userDetails.setAuthoritiesByRoles(userDetails.getUserRoles());
        return userDetails;
    }

    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        return loadUserDetails(loginName, "loginName");
    }

    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        return loadUserDetails(userCode, "userCode");
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        return loadUserDetails(regEmail, "regEmail");
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        return loadUserDetails(regCellPhone, "regCellPhone");
    }

    @Override
    public void updateUserInfo(IUserInfo userInfo) {
        RestfulHttpRequest.jsonPost(
            appSession, "/platform/userinfo",
            userInfo, true);
    }

    /**
     * 新增菜单和操作
     *
     * @param optInfos   菜单对象集合
     * @param optMethods 操作对象集合
     *//*
    @Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("optInfos", optInfos);
        param.put("optMethods", optMethods);
        RestfulHttpRequest.jsonPost(appSession, "/platform/insertopt", param);
    }*/
    @Override
    public List<? extends IOsInfo> listOsInfos(String topUnit) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/ipenvironment/osinfo/" + topUnit,
            OsInfo.class);
    }
}
