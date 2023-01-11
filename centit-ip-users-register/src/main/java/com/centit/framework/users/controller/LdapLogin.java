package com.centit.framework.users.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.operationlog.RecordOperationLog;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.UserSyncDirectory;
import com.centit.framework.system.service.UserSyncDirectoryManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.compiler.Pretreatment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * LDAP登录Controller

{
     searchBase : "(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))",
     searchName : "CN=Users,DC=centit,DC=com",
     loginNameField : "sAMAccountName",
     returnFields : {
         userName : "displayName",
         loginName : "sAMAccountName",
         name : "name",
         regEmail : "mail",
         regCellPhone : "mobilePhone",
         userDesc : "description"
     },
    userURIFormat : "{loginName}@centit.com"
}
 */
@Controller
@RequestMapping("/ldap")
@Api(value = "ldap登录相关接口", tags = "ldap登录相关接口")
public class LdapLogin extends BaseController {

    private final static String LDAP_USER_ID = "ldapUserURI";
    private static Logger logger = LoggerFactory.getLogger(LdapLogin.class);

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private UserSyncDirectoryManager userSyncDirectoryManager;

    @Value("${security.disable.user}")
    private String disableUser;

    public String getOptId() {
        return "LDAPLOGIN";
    }

    @ApiOperation(value = "ldap登录", notes = "ldap登录")
    @PostMapping(value = "/login")
    @WrapUpResponseBody
    @RecordOperationLog(content = "用户{username}使用ldap登录,操作IP地址:{loginIp}",
        newValue = "ldap登录")
    public ResponseData login(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              HttpServletRequest request) throws Exception {
        username = StringEscapeUtils.unescapeHtml4(username);
        password = StringEscapeUtils.unescapeHtml4(password);
        if (StringUtils.isNotBlank(disableUser)) {
            disableUser = StringUtils.deleteWhitespace(disableUser);
            String[] ignoreUsers = disableUser.split(",");
            for (int i = 0; i < ignoreUsers.length; i++) {
                if (username.contains(ignoreUsers[i])) {
                    return ResponseData.makeErrorMessage("禁用的用户账号");
                }
            }
        }

        List<UserSyncDirectory> directories = userSyncDirectoryManager.listLdapDirectory();
        for (UserSyncDirectory directory : directories){
            if(StringUtils.isBlank(directory.getUrl())){
                continue;
            }
            boolean passed = checkUserPasswordByDn(directory, username, password);
            if (passed) {
                CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(username);
                ud.setLoginIp(WebOptUtils.getRequestAddr(request));
                SecurityContextHolder.getContext().setAuthentication(ud);
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("accessToken", request.getSession().getId());
                sessionMap.put("userInfo", ud);
                return ResponseData.makeResponseData(sessionMap);
            }
        }
        return ResponseData.makeErrorMessage("用户名密码不匹配。");
    }


    public static boolean checkUserPasswordByDn(UserSyncDirectory directory, String loginName, String password) {

        JSONObject searchParams = JSON.parseObject(directory.getSearchBase());
        String userURIFormat = searchParams.getString("userURIFormat");
        if(StringUtils.isBlank(userURIFormat)){
            userURIFormat = "{loginName}";
        }
        String userURI = Pretreatment.mapTemplateString(userURIFormat,
            CollectionsOpt.createHashMap("loginName", loginName, "topUnit", directory.getTopUnit()));

        Properties env = new Properties();
        //String ldapURL = "LDAP://192.168.128.5:389";//ip:port ldap://192.168.128.5:389/CN=Users,DC=centit,DC=com
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, userURI);
        env.put(Context.SECURITY_CREDENTIALS, password);
        //"LDAP://192.168.128.5:389"
        env.put(Context.PROVIDER_URL, directory.getUrl() );
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
/*

    public LeftRightPair<UserSyncDirectory, Map<String, Object>> searchUserByLoginName(String loginName) {
        List<UserSyncDirectory> list = userSyncDirectoryManager.listObjects();
        //UserSyncDirectory directory = new UserSyncDirectory();
        if (list != null && list.size() > 0) {
            for (UserSyncDirectory userSyncDirectory : list) {
                if ("LDAP".equalsIgnoreCase(userSyncDirectory.getType())) {
                    Map<String, Object> userDataMap = searchLdapUserByloginName(userSyncDirectory, loginName);
                    if(userDataMap.size()>0){
                        return new LeftRightPair<>(userSyncDirectory, userDataMap);
                    }
                }
            }
        }
        return null;
    }
    public static Map<String, Object> searchLdapUserByloginName(UserSyncDirectory directory, String loginName) {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, directory.getUser());
        env.put(Context.SECURITY_CREDENTIALS, directory.getUserPwd());
        env.put(Context.PROVIDER_URL, directory.getUrl());
        Map<String, Object> attributes = new HashMap<>(20);
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            JSONObject searchParams = JSON.parseObject(directory.getSearchBase());
            // searchBase "(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))"
            //  (distinguishedName=CN={0},CN=Users,DC=centit,DC=com)"
            String searchFilter = MessageFormat.format(searchParams.getString("searchBase"), loginName);
            // {"displayName", "name", "sAMAccountName",
            //                "mail", "distinguishedName", "jobNo", "idCard", "mobilePhone", "description", "memberOf"}
            Map<String, Object> returnFields = CollectionsOpt.objectToMap(searchParams.get("returnFields"));
            String [] fieldNames = new String[returnFields.size()];
            int i = 0;
            for(Object obj : returnFields.values()){
                fieldNames[i++] = StringBaseOpt.castObjectToString(obj);
            }
            searchCtls.setReturningAttributes(fieldNames);
            // searchName "CN=Users,DC=centit,DC=com"
            NamingEnumeration<SearchResult> answer = ctx.search(searchParams.getString("searchName") , searchFilter, searchCtls);
            if (answer.hasMoreElements()) {
                SearchResult sr = answer.next();

                Attributes attrs = sr.getAttributes();
                // loginNameField sAMAccountName
                String principalId = getAttributeString(attrs, searchParams.getString("loginNameField"));
                if (StringUtils.isNotBlank(principalId)) {
                    NamingEnumeration<? extends Attribute> enumeration = attrs.getAll();
                    while (enumeration.hasMore()) {
                        Attribute attr = enumeration.next();
                        attributes.put(attr.getID(), attr.get());
                    }
                }
            }
            ctx.close();

            if(attributes.size()>0){
                HashMap<String, Object> returnMap = new HashMap<>(attributes.size()+2);
                for(Map.Entry<String, Object> ent : returnFields.entrySet()){
                    Object obj = attributes.get(StringBaseOpt.castObjectToString(ent.getValue()));
                    if(obj !=null){
                        returnMap.put(ent.getKey(), obj);
                    }
                }
                returnMap.put(LDAP_USER_ID, Pretreatment.mapTemplateString(
                        searchParams.getString("userURIFormat"),  attributes));
                return returnMap;
            }

        } catch (NamingException e) {
            System.out.println(e.getLocalizedMessage());
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

        }
        return null;
    }

    public static String getAttributeString(Attributes attrs, String attrName) {
        Attribute attr = attrs.get(attrName);
        if (attr == null) {
            return null;
        }
        try {
            return StringBaseOpt.objectToString(attr.get());
        } catch (NamingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
*/


}
