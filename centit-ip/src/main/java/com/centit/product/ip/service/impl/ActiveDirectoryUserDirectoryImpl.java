package com.centit.product.ip.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.dao.UserRoleDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserRole;
import com.centit.framework.system.po.UserRoleId;
import com.centit.framework.system.po.UserUnit;
import com.centit.product.ip.service.UserDirectory;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;

@Service("activeDirectoryUserDirectory")
public class ActiveDirectoryUserDirectoryImpl implements UserDirectory{

	@Resource
    @NotNull
    private UserUnitDao userUnitDao;

    @Resource
    @NotNull
    private UnitInfoDao unitInfoDao;

    @Resource
    @NotNull
    private UserRoleDao userRoleDao;

    @Resource(name = "userInfoDao")
    @NotNull
    private UserInfoDao userInfoDao;

    @Value("${userdirectory.ldap.url}")
    private String ldapUrl;// = "LDAP://192.168.128.5:389";

	@Value("${userdirectory.ldap.username}")
	private String ldapUser;// = "accounts@centit.com";
	
	@Value("${userdirectory.ldap.userpassword}")
    private String ldapUserPwd;// = "yhs@yhs1";//password
    
	@Value("${userdirectory.ldap.searchbase}")
    private String searchBase;// = "CN=Users,DC=centit,DC=com";
 
	
	@Value("${userdirectory.default.rank:'YG'}")
	@NotNull
	private String defaultRank;
	
	@Value("${userdirectory.default.station:'ZY'}")
	@NotNull
	private String defaultStation;
	
	@Value("${userdirectory.default.rolecode}")
	private String defaultUserRole;
	
    public String getDefaultUserRole() {
		return defaultUserRole;
	}

	public void setDefaultUserRole(String defaultUserRole) {
		this.defaultUserRole = defaultUserRole;
	}

	public String getDefaultRank() {
		return defaultRank;
	}

	public void setDefaultRank(String defaultRank) {
		this.defaultRank = defaultRank;
	}

	public String getDefaultStation() {
		return defaultStation;
	}

	public void setDefaultStation(String defaultStation) {
		this.defaultStation = defaultStation;
	}

	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(String ldapUser) {
		this.ldapUser = ldapUser;
	}

	public String getLdapUserPwd() {
		return ldapUserPwd;
	}

	public void setLdapUserPwd(String ldapUserPwd) {
		this.ldapUserPwd = ldapUserPwd;
	}

	public String getSearchBase() {
		return searchBase;
	}

	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}
   
    public static String getAttributeString(Attribute attr){
    	if(attr==null)
    		return null;
    	try {
			return StringBaseOpt.objectToString(attr.get());
		} catch (NamingException e) {
			return null;
		}
    }
    
    public static String getAttributeString(Attributes attr, String attrName){
    	return getAttributeString(attr.get(attrName));
    }
    
    @Transactional
	public int synchroniseUserDirectory() {
		Properties env = new Properties();
		//String ldapURL = "LDAP://192.168.128.5:389";//ip:port ldap://192.168.128.5:389/CN=Users,DC=centit,DC=com
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
		env.put(Context.SECURITY_PRINCIPAL, ldapUser);
		env.put(Context.SECURITY_CREDENTIALS, ldapUserPwd);
		env.put(Context.PROVIDER_URL, ldapUrl);
		Date now = DatetimeOpt.currentUtilDate();
		try {
			LdapContext ctx = new InitialLdapContext(env, null);
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			Map<String,UnitInfo> allUnits = new HashMap<String,UnitInfo>();
			String searchFilter = "(objectCategory=group)";// 
			String returnedAtts[] = {"name","description","distinguishedName","managedBy"};
			searchCtls.setReturningAttributes(returnedAtts);
			NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				String distinguishedName = getAttributeString(attrs,"distinguishedName");
				String unitName = getAttributeString(attrs,"description");
				if(unitName==null || distinguishedName==null)
					continue;
				UnitInfo unitInfo = unitInfoDao.getUnitByTag(distinguishedName);
				if(unitInfo==null){
					unitInfo = new UnitInfo();
					unitInfo.setUnitCode(unitInfoDao.getNextKey());
					unitInfo.setUnitTag(distinguishedName);
					unitInfo.setIsValid("T");
					unitInfo.setUnitType("L");
					unitInfo.setUnitPath("/"+unitInfo.getUnitCode());
					unitInfo.setCreateDate(now);
				}
				unitInfo.setUnitName(unitName);
				unitInfo.setUnitDesc(getAttributeString(attrs,"managedBy"));
				unitInfo.setLastModifyDate(now);
				unitInfoDao.mergeObject(unitInfo);
				allUnits.put(distinguishedName, unitInfo);
			}			
	
			searchFilter = "(&(objectCategory=person)(objectClass=user))";//"(objectCategory=group)";// 
			String userReturnedAtts[] = {"memberOf","displayName","sAMAccountName",
					"mail","distinguishedName"};
			searchCtls.setReturningAttributes(userReturnedAtts);
			answer = ctx.search(searchBase, searchFilter,searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				String loginName = getAttributeString(attrs,"sAMAccountName");
				String userName = getAttributeString(attrs,"displayName");
				if(userName==null || loginName==null)
					continue;
				boolean createUser=false;
				UserInfo userInfo = userInfoDao.getUserByLoginName(loginName);
				if(userInfo==null){
					userInfo = new UserInfo();
					userInfo.setUserCode(userInfoDao.getNextKey());
					userInfo.setLoginName(loginName);
					userInfo.setIsValid("T");
					userInfo.setCreateDate(now);
					createUser = true;
					String regEmail = getAttributeString(attrs,"mail");
					if(StringUtils.isNoneBlank(regEmail)){
						if(regEmail.length() <40 && userInfoDao.getUserByRegEmail(regEmail)==null)
							userInfo.setRegEmail(regEmail);
					}
				}
				
				userInfo.setUserTag(getAttributeString(attrs,"distinguishedName"));
				userInfo.setUserName(userName);				
				userInfo.setUpdateDate(now);
				userInfoDao.mergeObject(userInfo);
			
				if(createUser && StringUtils.isNoneBlank(this.defaultUserRole)){
					UserRole role = new UserRole(
							new UserRoleId(userInfo.getUserCode(), defaultUserRole));
					role.setObtainDate(now);
					role.setCreateDate(now);
					role.setChangeDesc("LDAP同步时默认设置。");
					userRoleDao.mergeObject(role);
				}
				
				Attribute members =  attrs.get("memberOf");
				if(members!=null){
					NamingEnumeration<?> ms = members.getAll();
					while (ms.hasMoreElements()) {
						Object member =  ms.next();
						String groupName = StringBaseOpt.objectToString(member);
						UnitInfo u = allUnits.get(groupName);
						if(u!=null){
							List<UserUnit> uus = userUnitDao.listObjectByUserUnit(
									userInfo.getUserCode(),u.getUnitCode());
							if(uus==null || uus.size()==0){							
								UserUnit uu = new UserUnit();
								uu.setUserUnitId(userUnitDao.getNextKey());
								uu.setUnitCode(u.getUnitCode());
								uu.setUserCode(userInfo.getUserCode());
								uu.setCreateDate(now);
								uu.setIsPrimary("F");
								uu.setUserRank(defaultRank);
								uu.setUserStation(defaultStation);
								userUnitDao.saveNewObject(uu);
							}
						}
					}
				}
				
			}
			ctx.close();
			return 0;
		}catch (NamingException e) {
			e.printStackTrace();
			return -1;
		}		
	}	
}
