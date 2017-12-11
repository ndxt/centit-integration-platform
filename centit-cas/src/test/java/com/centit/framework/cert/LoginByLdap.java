package com.centit.framework.cert;

/**
 * https://fileserver.centit.com/svn/centit/framework/framework-sys-module2.0/src/main/resources/spring-security-ad.xml
 */
public class LoginByLdap {

    public static void main(String[] args) throws Exception {
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
}
