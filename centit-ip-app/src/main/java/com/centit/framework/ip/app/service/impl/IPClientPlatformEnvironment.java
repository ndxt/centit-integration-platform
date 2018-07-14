package com.centit.framework.ip.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.common.ResponseJSON;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.staticsystem.security.StaticCentitUserDetails;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成平台客户端业务配置新, 所有的访问需要添加一个cache策略
 * @author codefan
 *
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


    public void setPlatServerUrl(String platServerUrl) {
        appSession = new AppSession(platServerUrl,false,null,null);
    }

    @Override
    public UserSetting getUserSetting(String userCode, String paramCode) {
        ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                appSession,
                "/usersetting/"+userCode+"/"+paramCode);

        if(resJson==null)
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
            "/usersettings/"+userCode+"/"+topOptId,
            UserSetting.class);
    }

    @Override
    public void saveUserSetting(IUserSetting userSetting) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            /*String resStr =*/ HttpExecutor.jsonPost(
                    HttpExecutorContext.create(httpClient),
                    appSession.completeQueryUrl("/usersetting"),
                    userSetting);
            //ResponseJSON resJson = ResponseJSON.valueOfJson(resStr);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }

    @Override
    public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {

        return listUserMenuOptInfosUnderSuperOptId(userCode,topOptId,asAdmin);
    }

    @Override
    public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId,
                                                             boolean asAdmin) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/usermenu/"+superOptId+"/"+userCode+"?asAdmin="+asAdmin,
                OptInfo.class);

    }

    @Override
    public List<UserRole> listUserRoles(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/userroles/"+userCode,
                UserRole.class);
    }

    @Override
    public List<UserRole> listRoleUsers(String roleCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/roleusers/"+roleCode,
                UserRole.class);
    }

    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/unitroles/"+unitCode,
                IUnitRole.class);
    }

    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/roleunits/"+roleCode,
                IUnitRole.class);
    }

    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            Map<String,String> userInfo = new HashMap<>();
            userInfo.put("userCode", userCode);
            userInfo.put("password", userPassword);
            userInfo.put("newPassword", userPassword);
            HttpExecutor.jsonPost(HttpExecutorContext.create(httpClient),
                    appSession.completeQueryUrl("/changepassword/"+userCode),
                    JSON.toJSONString(userInfo), true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取httpClient出错",e);
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }

    @Override
    public boolean checkUserPassword(String userCode, String userPassword) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            Map<String,String> userInfo = new HashMap<>();
            userInfo.put("userCode", userCode);
            userInfo.put("password", userPassword);
            userInfo.put("newPassword", userPassword);
            String sret = HttpExecutor.jsonPost(HttpExecutorContext.create(httpClient),
                    appSession.completeQueryUrl("/checkpassword/"+userCode),
                    JSON.toJSONString(userInfo), true);
            return StringRegularOpt.isTrue(sret);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取httpClient出错",e);
            return false;
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }

    @Override
    public List<UserInfo> listAllUsers() {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/allusers/"+topOptId,
                UserInfo.class);
    }

    @Override
    public List<UnitInfo> listAllUnits() {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/allunits/"+topOptId,
                UnitInfo.class);
    }

    @Override
    public List<UserUnit> listAllUserUnits() {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/alluserunits/"+topOptId,
                UserUnit.class);
    }

    @Override
    public List<UserUnit> listUserUnits(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/userunits/"+topOptId+"/"+userCode,
                UserUnit.class);
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/unitusers/"+topOptId+"/"+unitCode,
                UserUnit.class);
    }

    /**
     * 获取所有角色信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRoleInfo> listAllRoleInfo() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/rolerepo/"+topOptId,
            RoleInfo.class);
    }


    @Override
    public List<DataCatalog> listAllDataCatalogs() {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/catalogs/"+topOptId,
                DataCatalog.class);
    }

    @Override
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/dictionary/"+topOptId+"/"+catalogCode,
                DataDictionary.class);
    }


    public List<RolePower>  listAllRolePower(){
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/allrolepowers/"+topOptId,
                RolePower.class);
    }

    /**
     * 获取业务操作信息
     *
     * @return List 业务信息
     */
    @Override
    public List<? extends IOptInfo> listAllOptInfo() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/optinforepo/"+topOptId,
            OptInfo.class);
    }

    @Override
    public List<OptMethod> listAllOptMethod(){
        return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/alloptmethods/"+topOptId,
                OptMethod.class);
    }


    private CentitUserDetails loadUserDetails(String queryParam, String qtype) {
        ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                appSession,"/userdetails/"+topOptId+"/"+queryParam+"?qtype="+qtype);

        if(resJson==null || resJson.getCode()!=0) {
            return null;
        }
        StaticCentitUserDetails userDetails =
                resJson.getDataAsObject("userDetails", StaticCentitUserDetails.class);
        userDetails.setUserUnits(
                resJson.getDataAsArray("userUnits", UserUnit.class) );
        userDetails.setAuthoritiesByRoles(userDetails.getUserRoles());
        return userDetails;
    }

    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        return loadUserDetails(loginName,"loginName");
    }

    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        return loadUserDetails(userCode,"userCode");
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        return loadUserDetails(regEmail,"regEmail");
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        return loadUserDetails(regCellPhone,"regCellPhone");
    }

    @Override
    public void updateUserInfo(IUserInfo userInfo) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            /*String resStr =*/ HttpExecutor.jsonPost(
                    HttpExecutorContext.create(httpClient),
                    appSession.completeQueryUrl("/userinfo"),
                    userInfo,
                    true);
            //ResponseJSON resJson = ResponseJSON.valueOfJson(resStr);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }

    /**
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    @Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods) {
        CloseableHttpClient httpClient = null;
        Map<String, Object> param = new HashMap<>(4);
        param.put("optInfos", optInfos);
        param.put("optMethods", optMethods);
        try {
            httpClient = appSession.allocHttpClient();
        HttpExecutor.jsonPost(
                HttpExecutorContext.create(httpClient),
                appSession.completeQueryUrl("/insertopt"), param);
        } catch (Exception e) {

        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }
}
