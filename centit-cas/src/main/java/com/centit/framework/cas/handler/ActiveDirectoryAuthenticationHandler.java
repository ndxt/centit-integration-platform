/*
 * 版权所有.(c)2008-2017. 卡尔科技工作室
 */

package com.centit.framework.cas.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.cas.audit.JdbcLoginLogger;
import com.centit.framework.cas.config.ActiveDirectoryProperties;
import com.centit.framework.cas.model.ActiveDirectoryCredential;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;


/**
 * 南大先腾 技术管理中心
 * @author codefan@sina.comc
 * @since 1.0.2
 */
public class ActiveDirectoryAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    private static Logger logger = LoggerFactory.getLogger(JdbcLoginLogger.class);
    private ActiveDirectoryProperties activeDirectory;

    public ActiveDirectoryAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

   /*
    https://fileserver.centit.com/svn/centit/framework/framework-sys-module2.0/src/main/resources/spring-security-ad.xml

    */

    @Override
    protected HandlerResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        ActiveDirectoryCredential adCredential = (ActiveDirectoryCredential) credential;
        return createHandlerResult(credential,
            this.principalFactory.createPrincipal( adCredential.getId(),
                (JSONObject)JSON.toJSON(adCredential)), null);
    }


    @Override
    public boolean supports(Credential credential) {
        return credential instanceof ActiveDirectoryCredential;
    }

    public void setActiveDirectory(ActiveDirectoryProperties activeDirectory) {
        this.activeDirectory = activeDirectory;
    }

}
