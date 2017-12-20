package com.centit.framework.cas.config;

import com.centit.framework.cas.actions.*;
import com.centit.framework.cas.audit.AuditPolicy;
import com.centit.framework.cas.audit.IpMacAuditPolicy;
import com.centit.framework.cas.audit.JdbcLoginLogger;
import com.centit.framework.cas.audit.LoginLogger;
import com.centit.framework.cas.controller.CaptchaController;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.pm.config.PasswordManagementConfiguration;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.execution.Action;

@Configuration("complexAuthWebflowConfiguation")
@EnableConfigurationProperties(ComplexConfigurationProperties.class)
@AutoConfigureAfter(PasswordManagementConfiguration.class)
public class ComplexAuthWebflowConfiguation {

    @Autowired
    private ComplexConfigurationProperties complexProperties;

    @Autowired
    @Qualifier("serviceTicketRequestWebflowEventResolver")
    private CasWebflowEventResolver serviceTicketRequestWebflowEventResolver;

    @Autowired
    @Qualifier("initialAuthenticationAttemptWebflowEventResolver")
    private CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver;

    @Autowired
    @Qualifier("adaptiveAuthenticationPolicy")
    private AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy;

    @Bean
    @ConditionalOnMissingBean(name = "captchaController")
    public CaptchaController captchaController() {
        return new CaptchaController();
    }

    @Bean
    public AuditPolicy auditPolicy(){
        return new IpMacAuditPolicy();
    }

    @Bean
    public LoginLogger loginLogger(){
        JdbcLoginLogger logger = new JdbcLoginLogger();
        logger.setJdbcLoggerConfig(complexProperties.getJdbcLogger());
        return logger;
    }

    @ConditionalOnMissingBean(name = "md5PasswordAuthenticationAction")
    @Bean
    public Action md5PasswordAuthenticationAction(@Autowired AuditPolicy auditPolicy,
                                                  @Autowired LoginLogger loginLogger) {
        Md5PasswordAuthenticationAction action =  new Md5PasswordAuthenticationAction(initialAuthenticationAttemptWebflowEventResolver,
                serviceTicketRequestWebflowEventResolver,
                adaptiveAuthenticationPolicy);
        action.setAuditPolicy(auditPolicy);
        action.setLoginLogger(loginLogger);
        action.setStrategyConfig(complexProperties.getStrategy());
        return action;
    }

    @ConditionalOnMissingBean(name = "ldapAuthenticationAction")
    @Bean
    public Action ldapAuthenticationAction(@Autowired AuditPolicy auditPolicy,
                                                      @Autowired LoginLogger loginLogger) {
        LdapAuthenticationAction action =  new LdapAuthenticationAction(initialAuthenticationAttemptWebflowEventResolver,
            serviceTicketRequestWebflowEventResolver,
            adaptiveAuthenticationPolicy);
        action.setAuditPolicy(auditPolicy);
        action.setLoginLogger(loginLogger);
        action.setStrategyConfig(complexProperties.getStrategy());
        return action;
    }

    @ConditionalOnMissingBean(name = "fingerMarkAuthenticationAction")
    @Bean
    public Action fingerMarkAuthenticationAction(@Autowired AuditPolicy auditPolicy,
                                                 @Autowired LoginLogger loginLogger) {
        FingerMarkAuthenticationAction action =  new FingerMarkAuthenticationAction(initialAuthenticationAttemptWebflowEventResolver,
            serviceTicketRequestWebflowEventResolver,
            adaptiveAuthenticationPolicy);
        action.setAuditPolicy(auditPolicy);
        action.setLoginLogger(loginLogger);
        action.setStrategyConfig(complexProperties.getStrategy());
        return action;
    }

    @ConditionalOnMissingBean(name = "usbKeyAuthenticationAction")
    @Bean
    public Action usbKeyAuthenticationAction(@Autowired AuditPolicy auditPolicy,
                                             @Autowired LoginLogger loginLogger) {
        UsbKeyAuthenticationAction action =  new UsbKeyAuthenticationAction(initialAuthenticationAttemptWebflowEventResolver,
            serviceTicketRequestWebflowEventResolver,
            adaptiveAuthenticationPolicy);
        action.setAuditPolicy(auditPolicy);
        action.setLoginLogger(loginLogger);
        action.setStrategyConfig(complexProperties.getStrategy());
        return action;
    }

    @Bean
    public Action changeAuthenticationAction() {
        ChangeAuthenticationAction changeAuthenticationAction = new ChangeAuthenticationAction();
        changeAuthenticationAction.setStrategyConfig(complexProperties.getStrategy());
        return changeAuthenticationAction;
    }
}
