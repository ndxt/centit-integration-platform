/*
 * 版权所有.(c)2008-2017. 卡尔科技工作室
 */

package com.centit.framework.cas.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.cas.config.QueryUserProperties;
import com.centit.framework.cas.model.AbstractPasswordCredential;
import com.centit.framework.cas.model.ActiveDirectoryCredential;
import com.centit.framework.cas.model.Md5PasswordCredential;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.DbcpConnectPools;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 南大先腾 技术管理中心
 * @author codefan@sina.comc
 * @since 1.0.2
 */
public class ActiveDirectoryAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {


    public ActiveDirectoryAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

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


}
