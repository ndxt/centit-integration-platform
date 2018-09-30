package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.service.UserDirectory;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.dao.UserRoleDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.*;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.*;

@Service("activeDirectoryUserDirectory")
public class ActiveDirectoryUserDirectoryImpl implements UserDirectory{

    private static Logger logger = LoggerFactory.getLogger(ActiveDirectoryUserDirectoryImpl.class);

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

    @Value("${userdirectory.ldap.url:}")
    private String ldapUrl;

    @Value("${userdirectory.ldap.username:}")
    private String ldapUser;

    @Value("${userdirectory.ldap.userpassword:}")
    private String ldapUserPwd;

    @Value("${userdirectory.ldap.searchbase:}")
    private String searchBase;

    //等级默认为普通员工 YG
    @Value("${userdirectory.default.rank:YG}")
    @NotNull
    private String defaultRank;

    //岗位默认为普通职员 ZY
    @Value("${userdirectory.default.station:ZY}")
    @NotNull
    private String defaultStation;

    @Value("${userdirectory.default.rolecode:}")
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
            logger.error(e.getMessage(), e);
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
            Map<String,UnitInfo> allUnits = new HashMap<>();
            String searchFilter = "(objectCategory=group)";//
            String[] returnedAtts = {"name","description","distinguishedName","managedBy"};
            searchCtls.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult sr = answer.next();
                Attributes attrs = sr.getAttributes();
                String distinguishedName = getAttributeString(attrs,"distinguishedName");
                String unitName = getAttributeString(attrs,"description");
                if(unitName==null || distinguishedName==null)
                    continue;
                UnitInfo unitInfo = unitInfoDao.getUnitByTag(distinguishedName);
                boolean createNew = unitInfo==null;
                if(createNew){
                    unitInfo = new UnitInfo();
                    unitInfo.setUnitCode(unitInfoDao.getNextKey());
                    unitInfo.setUnitTag(distinguishedName);
                    unitInfo.setIsValid("T");
                    unitInfo.setUnitType("L");
                    unitInfo.setUnitPath("/"+unitInfo.getUnitCode());
                    unitInfo.setCreateDate(now);
                    //-----------------------------
                  }
                unitInfo.setUnitName(unitName);
                unitInfo.setUnitDesc(getAttributeString(attrs, "managedBy"));
                unitInfo.setLastModifyDate(now);
                if(createNew){
                    unitInfoDao.saveNewObject(unitInfo);
                }else {
                    unitInfoDao.updateUnit(unitInfo);
                }
                allUnits.put(distinguishedName, unitInfo);
            }

            searchFilter = "(&(objectCategory=person)(objectClass=user))";//"(objectCategory=group)"
            String[] userReturnedAtts = {"memberOf","displayName","sAMAccountName",
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
                if(userInfo==null) {
                    userInfo = new UserInfo();
                    userInfo.setUserCode(userInfoDao.getNextKey());
                    userInfo.setIsValid("T");
                    userInfo.setLoginName(loginName);
                    userInfo.setCreateDate(now);
                    createUser = true;
                }
                String regEmail = getAttributeString(attrs,"mail");
                if(StringUtils.isNoneBlank(regEmail)){
                    if(regEmail.length() <60 && userInfoDao.getUserByRegEmail(regEmail)==null)
                        userInfo.setRegEmail(regEmail);
                }
                String regCellPhone = getAttributeString(attrs,"mobilePhone");
                if(StringUtils.isNoneBlank(regCellPhone)){
                    if(regCellPhone.length() <15 && userInfoDao.getUserByRegCellPhone(regCellPhone)==null)
                        userInfo.setRegCellPhone(regCellPhone);
                }
                String idCardNo = getAttributeString(attrs,"idCard");
                if(StringUtils.isNoneBlank(idCardNo)){
                    if(idCardNo.length() <20 && userInfoDao.getUserByIdCardNo(idCardNo)==null)
                        userInfo.setIdCardNo(idCardNo);
                }
                String userWord = getAttributeString(attrs,"jobNo");
                if(StringUtils.isNoneBlank(userWord)){
                    if(userWord.length() <20 && userInfoDao.getUserByUserWord(userWord)==null)
                        userInfo.setUserWord(userWord);
                }
                userInfo.setUserTag(getAttributeString(attrs,"distinguishedName"));
                userInfo.setUserName(userName);
                userInfo.setUpdateDate(now);
                if(createUser)
                    userInfoDao.saveNewObject(userInfo);
                else
                    userInfoDao.updateUser(userInfo);

                if(createUser && StringUtils.isNoneBlank(this.defaultUserRole)){
                    UserRole role = new UserRole(
                            new UserRoleId(userInfo.getUserCode(), defaultUserRole));
                    role.setObtainDate(now);
                    role.setCreateDate(now);
                    role.setChangeDesc("LDAP同步时默认设置。");
                    userRoleDao.mergeUserRole(role);
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
            logger.error(e.getMessage(),e);
            return -1;
        }
    }
}
