package com.centit.framework.users.controller;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.UserSyncDirectory;
import com.centit.framework.system.service.UserSyncDirectoryManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.compiler.Pretreatment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.*;

/**
 * LDAP登录Controller
 */
@Controller
@RequestMapping("/ldap")
@Api(value = "ldap登录相关接口", tags = "ldap登录相关接口")
public class LdapLogin {

    private static Logger logger = LoggerFactory.getLogger(LdapLogin.class);

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private UserSyncDirectoryManager userSyncDirectoryManager;

    @ApiOperation(value = "ldap登录", notes = "ldap登录")
    @PostMapping(value = "/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("returnUrl") String returnUrl,
                        HttpServletResponse response) throws Exception {

        Map<String, Object> map = searchLdapUserByloginName(username);
        if(map==null || map.isEmpty()){
            throw new ObjectException(500, "用户找不到！");
        }

        try {
            boolean passed = checkUserPasswordByDn(
                Pretreatment.mapTemplateString("CN={name},CN=Users,DC=centit,DC=com",map),
                password);
            if(!passed){
                throw new ObjectException(500, "用户名密码不匹配。");
            }
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(map.get("sAMAccountName") + "");
            SecurityContextHolder.getContext().setAuthentication(ud);
        } catch (NamingException e) {
            throw new ObjectException(500, "系统错误");
        }
        return "redirect:" + returnUrl;
    }

    public Map<String, Object> searchLdapUserByloginName(String loginName){
        List<UserSyncDirectory> list = userSyncDirectoryManager.listObjects();
        UserSyncDirectory directory = new UserSyncDirectory();
        if(list != null && list.size() > 0){
            for(UserSyncDirectory userSyncDirectory : list){
                if (userSyncDirectory.getType().equalsIgnoreCase("LDAP")) {
                    directory = userSyncDirectory;
                }
            }
        }
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
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
            List<String> searchFilters = new ArrayList<>();
            searchFilters.add("(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))");
            searchFilters.add("(distinguishedName=CN={0},CN=Users,DC=centit,DC=com)");
            for(String filterStr : searchFilters) {
                String searchFilter = MessageFormat.format(filterStr,loginName);
                String[] userReturnedAtts = new String[]{"displayName", "name", "sAMAccountName",
                    "mail", "distinguishedName", "jobNo", "idCard", "mobilePhone", "description", "memberOf"};
                searchCtls.setReturningAttributes(userReturnedAtts);
                NamingEnumeration<SearchResult> answer = ctx.search("CN=Users,DC=centit,DC=com", searchFilter, searchCtls);
                if (answer.hasMoreElements()) {
                    SearchResult sr = answer.next();

                    Attributes attrs = sr.getAttributes();

                    String principalId = getAttributeString(attrs, "sAMAccountName");
                    if (StringUtils.isNotBlank(principalId)) {
                        NamingEnumeration<? extends Attribute> enumeration = attrs.getAll();
                        while (enumeration.hasMore()) {
                            Attribute attr = enumeration.next();
                            attributes.put(attr.getID(), attr.get());
                        }
                        ctx.close();
                    }
                }
            }
            ctx.close();

        }catch (NamingException e) {
            //System.out.println(e.getLocalizedMessage());
            if(ctx != null){
                try {
                    ctx.close();
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

        }
        return attributes;
    }

    public static String getAttributeString(Attributes attrs, String attrName){
        Attribute attr = attrs.get(attrName);
        if(attr==null) {
            return null;
        }
        try {
            return StringBaseOpt.objectToString(attr.get());
        } catch (NamingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public boolean checkUserPasswordByDn(String username, String password) throws NamingException {

        Properties env = new Properties();
        //String ldapURL = "LDAP://192.168.128.5:389";//ip:port ldap://192.168.128.5:389/CN=Users,DC=centit,DC=com
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL,username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, "LDAP://192.168.128.5:389");
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }finally {
            if(ctx!=null) {
                ctx.close();
            }
        }
    }


}
