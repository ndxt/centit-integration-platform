/*
 * 版权所有.(c)2008-2017. 卡尔科技工作室
 */

package com.centit.framework.cas.handler;

import com.centit.framework.cas.audit.JdbcLoginLogger;
import com.centit.framework.cas.config.LdapProperties;
import com.centit.framework.cas.model.LdapCredential;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.compiler.Pretreatment;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 南大先腾 技术管理中心
 * @author codefan@sina.comc
 * @since 1.0.2
 */
public class LdapAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    private static Logger logger = LoggerFactory.getLogger(JdbcLoginLogger.class);
    private LdapProperties ldapProperties;

    public LdapAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    public boolean checkUserPasswordByDn(String username, String password) throws NamingException {

        Properties env = new Properties();
        //String ldapURL = "LDAP://192.168.128.5:389";//ip:port ldap://192.168.128.5:389/CN=Users,DC=centit,DC=com
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL,username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, ldapProperties.getUrl());
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            //System.out.println(username + "login ok!");
            return true;
        }finally {
            if(ctx!=null) {
                ctx.close();
            }
        }
    }


    public static String getAttributeString(Attributes attrs, String attrName){
        Attribute attr = attrs.get(attrName);
        if(attr==null)
            return null;
        try {
            return StringBaseOpt.objectToString(attr.get());
        } catch (NamingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Principal searchPrincipalByCredential(Credential credential){

        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, ldapProperties.getUsername());
        env.put(Context.SECURITY_CREDENTIALS, ldapProperties.getPassword());
        env.put(Context.PROVIDER_URL, ldapProperties.getUrl());
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            for(String filterStr : ldapProperties.getSearchFilter()) {

                String searchFilter = MessageFormat.format(filterStr,credential.getId());

                searchCtls.setReturningAttributes(ldapProperties.getPrincipalAttributesAsArray());
                NamingEnumeration<SearchResult> answer = ctx.search(ldapProperties.getSearchBase(), searchFilter, searchCtls);
                if (answer.hasMoreElements()) {
                    SearchResult sr = answer.next();

                    Attributes attrs = sr.getAttributes();

                    String principalId = getAttributeString(attrs, ldapProperties.getPrincipalIdField());
                    if (StringUtils.isNotBlank(principalId)) {
                        Map<String, Object> attributes = new HashMap<>(20);
                        NamingEnumeration<? extends Attribute> enumeration = attrs.getAll();
                        while (enumeration.hasMore()) {
                            Attribute attr = enumeration.next();
                            attributes.put(attr.getID(), attr.get());
                        }
                        ctx.close();
                        return this.principalFactory.createPrincipal(principalId, attributes);
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
        return null;
    }

   /*
    https://fileserver.centit.com/svn/centit/framework/framework-sys-module2.0/src/main/resources/spring-security-ad.xml

    */

    @Override
    protected HandlerResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        LdapCredential adCredential = (LdapCredential) credential;
        Principal principal = searchPrincipalByCredential(credential);
        if(principal==null){
            throw new AccountNotFoundException("用户找不到！");
        }

        try {
            boolean passed = checkUserPasswordByDn(
                    Pretreatment.mapTemplateString(ldapProperties.getDnFormat(),principal.getAttributes()),
                    adCredential.getPassword());
            if(!passed){
                throw new FailedLoginException("用户名密码不匹配。");
            }
        } catch (NamingException e) {
            throw new FailedLoginException(e.getLocalizedMessage());
        }

        return createHandlerResult(credential,principal, null);
    }


    @Override
    public boolean supports(Credential credential) {
        return credential instanceof LdapCredential;
    }

    public void setLdapProperties(LdapProperties ldapProperties) {
        this.ldapProperties = ldapProperties;
    }

}
