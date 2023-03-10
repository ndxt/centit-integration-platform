package com.centit.framework.users.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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
import com.centit.support.security.SecurityOptUtils;
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
        username = SecurityOptUtils.decodeSecurityString(StringEscapeUtils.unescapeHtml4(username));
        password = SecurityOptUtils.decodeSecurityString(StringEscapeUtils.unescapeHtml4(password));

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
        return ResponseData.makeErrorMessage("用户名或密码错误");
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
}
