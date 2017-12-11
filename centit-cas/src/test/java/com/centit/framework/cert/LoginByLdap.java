package com.centit.framework.cert;

import com.alibaba.fastjson.JSON;
import com.centit.support.compiler.Pretreatment;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.Principal;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * https://fileserver.centit.com/svn/centit/framework/framework-sys-module2.0/src/main/resources/spring-security-ad.xml
 */
public class LoginByLdap {

    public static Map<String,Object> searchPrincipalByCredential(String id){

        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL,"accounts@centit.com");
        env.put(Context.SECURITY_CREDENTIALS, "yhs@yhs1");
        env.put(Context.PROVIDER_URL,"LDAP://192.168.128.5:389");
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "(&(objectCategory=person)(objectClass=user) (sAMAccountName="+id+"))";//"(objectCategory=group)"
            String[] returnedAtts = {"name","description","distinguishedName","managedBy"};
            searchCtls.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> answer = ctx.search("CN=Users,DC=centit,DC=com", searchFilter,searchCtls);
            if(answer.hasMoreElements()) {
                SearchResult sr = answer.next();

                Attributes attrs = sr.getAttributes();

                Map<String,Object> attributes = new HashMap<>(20);
                NamingEnumeration<? extends Attribute> enumeration = attrs.getAll();
                while (enumeration.hasMore()){
                    Attribute attr = enumeration.next();
                    attributes.put(attr.getID(), attr.get());
                }
                ctx.close();
                return attributes;
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

    public static boolean loginLdapAsUser(String username, String password){

        Properties env = new Properties();
        //String ldapURL = "LDAP://192.168.128.5:389";//ip:port ldap://192.168.128.5:389/CN=Users,DC=centit,DC=com
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, "test@centit.com"); //codefan@centit.com

        env.put(Context.SECURITY_CREDENTIALS, "centit.1");
        env.put(Context.PROVIDER_URL, "LDAP://192.168.128.5:389");
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            //Attributes attrs = ctx.getAttributes("CN="+username+",CN=Users,DC=centit,DC=com",
            //        new String[] {"sAMAccountName","displayName"});
            //NamingEnumeration<? extends Attribute>  a = attrs.getAll();
            //System.out.println(a.next());
            //System.out.println(a.next());
            //ctx.get
            ctx.close();
            System.out.println(username + "login ok!");
            return true;
        }catch (NamingException e) {
            System.out.println(e.getLocalizedMessage());
            if(ctx != null){
                try {
                    ctx.close();
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        }

    }

    public static void main(String[] args) throws Exception {
        Map<String,String> attributes = new HashMap<>(20);
        attributes.put("other","otherValue");
        attributes.put("name","world");
        System.out.println(Pretreatment.mapTemplateString("hello {name}!" ,attributes));
        //Map<String,Object> map = searchPrincipalByCredential("codefan");
        //System.out.println(JSON.toJSONString(map));

        //loginLdapAsUser("杨淮生","abc$A123");
    }
     /*   DefaultSpringSecurityContextSource contextSource =
                new DefaultSpringSecurityContextSource(
                        "ldap://192.168.128.5:389");
        contextSource.setUserDn("accounts");
        contextSource.setPassword("yhs@yhs1");


        contextSource.setCacheEnvironmentProperties(false);

        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
                "CN=Users,DC=centit,DC=com","(sAMAccountName={0})",
                contextSource);
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);

        authenticator.setUserSearch(userSearch);
        authenticator.setUserDnPatterns(new String[]
                {"sAMAccountName={0},CN=Users,DC=centit,DC=com",
                "CN={0},CN=Users,DC=centit,DC=com"});

        UsernamePasswordAuthenticationToken userToken =
                new UsernamePasswordAuthenticationToken("codefan","abc$A123");

        //contextSource.setAuthenticationSource();

        DirContextOperations user = authenticator.authenticate(userToken);

        System.out.println(user.getNameInNamespace());*/
}
