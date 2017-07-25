package com.centit.framework.ip.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.core.common.ResponseJSON;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.staticsystem.po.*;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.network.HttpExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成平台客户端业务配置新, 所有的访问需要添加一个cache策略
 * @author codefan
 *
 */
public class IPClientPlatformEnvironment implements PlatformEnvironment
{

	private String topOptId;

	public IPClientPlatformEnvironment() {
		
	}
	
	public void setTopOptId(String topOptId) {
		this.topOptId = topOptId;
	}

	private AppSession appSession;



	public CloseableHttpClient getHttpClient() throws Exception {
		return appSession.getHttpClient();
	}

	public void releaseHttpClient(CloseableHttpClient httpClient) {
		appSession.releaseHttpClient(httpClient);
	}

	public void setPlatServerUrl(String platServerUrl) {
		appSession = new AppSession(platServerUrl,false,null,null);
	}
	
	//初始化  这个要定时刷新
	public void init(){
		if(appSession==null)
			return ;
		reloadSecurityMetadata();
	}
	
	

	@Override
	public String getSystemParameter(String paramCode) {
		return SysParametersUtils.getStringValue(paramCode);
	}

	@Override
	public String getUserSetting(String userCode, String paramCode) {

			ResponseJSON resJson = RestfulHttpRequest.getResponseData(
					appSession,
					"/usersetting/"+userCode+"/"+paramCode);

			if(resJson==null)
				return null;
			return resJson.getDataAsString("paramValue");
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
	public UserInfo getUserInfoByUserCode(String userCode) {
		return RestfulHttpRequest.getResponseObject(
				appSession,
				"/userinfo/"+userCode,
				UserInfo.class);
	}


	@Override
	public UnitInfo getUnitInfoByUnitCode(String unitCode){
		return RestfulHttpRequest.getResponseObject(
				appSession,
				"/unitinfo/"+unitCode,
				UnitInfo.class);
	}

	@Override
	public UserInfo getUserInfoByLoginName(String loginName) {
		return RestfulHttpRequest.getResponseObject(
				appSession,
				"/userinfobyloginname/"+loginName,
				UserInfo.class);
	}

	@Override
	public void changeUserPassword(String userCode, String userPassword) {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = appSession.getHttpClient();
			Map<String,String> userInfo = new HashMap<>();
			userInfo.put("userCode", userCode);
			userInfo.put("password", userPassword);
			userInfo.put("newPassword", userPassword);
			HttpExecutor.jsonPost(httpClient,
					appSession.completeQueryUrl("/changepassword/"+userCode),
					JSON.toJSONString(userInfo), true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(httpClient!=null)
				appSession.releaseHttpClient(httpClient);
		}
	}

	@Override
	public boolean checkUserPassword(String userCode, String userPassword) {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = appSession.getHttpClient();
			Map<String,String> userInfo = new HashMap<>();
			userInfo.put("userCode", userCode);
			userInfo.put("password", userPassword);
			userInfo.put("newPassword", userPassword);
			String sret = HttpExecutor.jsonPost(httpClient,
					appSession.completeQueryUrl("/checkpassword/"+userCode),
					JSON.toJSONString(userInfo), true);
			return StringRegularOpt.isTrue(sret);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(httpClient!=null)
				appSession.releaseHttpClient(httpClient);
		}
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'userList'" )
	public List<UserInfo> listAllUsers() {
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/allusers/"+topOptId,
				UserInfo.class);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'unitList'")
	public List<UnitInfo> listAllUnits() {
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/allunits/"+topOptId,
				UnitInfo.class);
	}

	@Override
	@Cacheable(value="AllUserUnits",key="'allUserUnits'")
	public List<UserUnit> listAllUserUnits() {
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/alluserunits/"+topOptId,
				UserUnit.class);
	}

	@Override
	@Cacheable(value="UserUnits",key="#userCode")
	public List<UserUnit> listUserUnits(String userCode) {
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/userunits/"+topOptId+"/"+userCode,
				UserUnit.class);
	}

	@Override
	@Cacheable(value="UnitUsers",key="#unitCode")
	public List<UserUnit> listUnitUsers(String unitCode) {
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/unitusers/"+topOptId+"/"+unitCode,
				UserUnit.class);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'unitCodeMap'")
	public Map<String, UnitInfo> getUnitRepo() {

		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession,
				"/unitrepo/"+topOptId);

		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UnitInfo.class);
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'userCodeMap'" )
	public Map<String, UserInfo> getUserRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession,
				"/userrepo/"+topOptId);

		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UserInfo.class);
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'loginNameMap'")
	public Map<String, UserInfo> getLoginNameRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession,
				"/loginnamerepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UserInfo.class);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'depNoMap'")
	public Map<String, UnitInfo> getDepNoRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession,
				"/depnorepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UnitInfo.class);
	}

	@Override
	@Cacheable(value="RoleInfo",key="'roleCodeMap'")
	public Map<String, RoleInfo> getRoleRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession, "/rolerepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(RoleInfo.class);
	}

	@Override
	@Cacheable(value="OptInfo",key="'optIdMap'")
	public Map<String, OptInfo> getOptInfoRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession,"/optinforepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(OptInfo.class);
	}

	@Override
	@Cacheable(value="OptInfo",key="'optCodeMap'")
	public Map<String, OptMethod> getOptMethodRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession, "/optmethodrepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(OptMethod.class);
	}

	@Override
	@Cacheable(value = "DataDictionary",key="'CatalogCode'")
	public List<DataCatalog> listAllDataCatalogs() {
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/catalogs/"+topOptId,
				DataCatalog.class);
	}

	@Override
	@Cacheable(value = "DataDictionary",key="#catalogCode")
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
	
	public List<OptMethod> listAllOptMethod(){
		return RestfulHttpRequest.getResponseObjectList(
				appSession,
				"/alloptmethods/"+topOptId,
				OptMethod.class);
	}


	private CentitUserDetails loadUserDetails(String queryParam, String qtype) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				appSession,"/userdetails/"+topOptId+"/"+queryParam+"?qtype="+qtype);
		
		if(resJson==null || resJson.getCode()!=0){
			return null;
		}
		UserInfo userInfo =  resJson.getDataAsObject("userInfo",UserInfo.class);
		if(userInfo==null)
			return null;
		
		List<String> userRoles = resJson.getDataAsArray("userRoles",String.class);
		List<UserUnit> userUnits = resJson.getDataAsArray("userUnits",UserUnit.class);
		userInfo.setUserUnits(userUnits);
		userInfo.setAuthoritiesByRoles(userRoles);
		return userInfo;
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
	@CacheEvict(value ={
			 "DataDictionary","OptInfo","RoleInfo","UserInfo","UnitInfo",
			 "UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
	public boolean reloadDictionary() {
		return true;
	}

	@Override
	public boolean reloadSecurityMetadata() {
		//这个要定时刷新 或者 通过集成平台来主动刷新
		CentitSecurityMetadata.optMethodRoleMap.clear();
        List<RolePower> rplist = listAllRolePower();
        if(rplist==null || rplist.size()==0)
        	return false;
        for(RolePower rp: rplist ){
            List<ConfigAttribute/*roleCode*/> roles = CentitSecurityMetadata.optMethodRoleMap.get(rp.getOptCode());
            if(roles == null){
                roles = new ArrayList<ConfigAttribute/*roleCode*/>();
            }
            roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + StringUtils.trim(rp.getRoleCode())));
            CentitSecurityMetadata.optMethodRoleMap.put(rp.getOptCode(), roles);
        }
        //将操作和角色对应关系中的角色排序，便于权限判断中的比较
        CentitSecurityMetadata.sortOptMethodRoleMap();
        Map<String, OptInfo> optRepo = getOptInfoRepo();
        List<OptMethod> oulist = listAllOptMethod();
        CentitSecurityMetadata.optTreeNode.setChildList(null);
        CentitSecurityMetadata.optTreeNode.setOptCode(null);
        for(OptMethod ou:oulist){
        	OptInfo oi = optRepo.get(ou.getOptId());
        	if(oi!=null){
            	String  optDefUrl = oi.getOptUrl()+ou.getOptUrl();
                List<List<String>> sOpt = CentitSecurityMetadata.parseUrl(
                		optDefUrl,ou.getOptReq());
                
                for(List<String> surls : sOpt){
                    OptTreeNode opt = CentitSecurityMetadata.optTreeNode;
                    for(String surl : surls)
                        opt = opt.setChildPath(surl); 
                    opt.setOptCode(ou.getOptCode());
                }
        	}
        }        
        //CentitSecurityMetadata.optTreeNode.printTreeNode();
		return true;
	}

}
