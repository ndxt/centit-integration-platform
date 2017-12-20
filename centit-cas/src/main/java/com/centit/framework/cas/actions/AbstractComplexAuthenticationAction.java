package com.centit.framework.cas.actions;

import com.centit.framework.cas.audit.AuditPolicy;
import com.centit.framework.cas.audit.LoginLogger;
import com.centit.framework.cas.config.StrategyProperties;
import com.centit.framework.cas.model.AbstractPasswordCredential;
import com.centit.framework.cas.model.ComplexAuthCredential;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.image.CaptchaImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationCredentialsLocalBinder;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.adaptive.UnauthorizedAuthenticationException;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationRequest;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 关于单点登录的配置说明可以看下面的文章
 * http://blog.csdn.net/u010475041/article/details/77886765 介绍首页
 * http://blog.csdn.net/u010475041/article/details/77943965 数据库认证
 * http://blog.csdn.net/u010475041/article/details/77972605 自定义认证
 * 官方文档 https://apereo.github.io/cas/5.2.x/index.html
 */

public abstract class AbstractComplexAuthenticationAction extends AbstractAction {

    private String supportAuthType;
    private AuditPolicy auditPolicy;
    private LoginLogger loginLogger;
    private StrategyProperties strategyConfig;

    private final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver;
    private final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy;
    private final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver;

    public AbstractComplexAuthenticationAction(final CasDelegatingWebflowEventResolver delegatingWebflowEventResolver,
                                        final CasWebflowEventResolver webflowEventResolver,
                                        final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        this.initialAuthenticationAttemptWebflowEventResolver = delegatingWebflowEventResolver;
        this.serviceTicketRequestWebflowEventResolver = webflowEventResolver;
        this.adaptiveAuthenticationPolicy = adaptiveAuthenticationPolicy;
    }





    public void setSupportAuthType(String supportAuthType) {
        this.supportAuthType = supportAuthType;
    }

    public abstract boolean doPrepareExecute(final RequestContext requestContext);

    protected Event makeError(final RequestContext requestContext, String sourceCode, String msg) {
        final MessageContext messageContext = requestContext.getMessageContext();
        //messageContext.hasErrorMessages()
        messageContext.addMessage(new MessageBuilder().error().code(
            CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE).source(sourceCode).defaultText(msg).build());
        //return getEventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE);
        final Map<String, Class<? extends Throwable>> map = CollectionUtils.wrap(
            AuthenticationException.class.getSimpleName(),
            AuthenticationException.class);
        final AuthenticationException error = new AuthenticationException(msg, map, new HashMap<>(0));
        onFailedLogin(requestContext);
        return new Event(this, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE,
            new LocalAttributeMap<>(CasWebflowConstants.TRANSITION_ID_ERROR, error));
    }


    @Override
    protected Event doExecute(final RequestContext requestContext) {
        if(!doPrepareExecute(requestContext)){
            return makeError(requestContext,"credentialError","请输入正确的验证信息！") ;
        }

        ComplexAuthCredential credential = (ComplexAuthCredential) WebUtils.getCredential(requestContext);

        if(StringUtils.isNotBlank(credential.getAuthType())
             && ! StringUtils.equals(this.supportAuthType, credential.getAuthType())
             /*&& StringUtils.equalsAny( credential.getAuthType(),
                "password","usbKey", "fingerMark","ldap" )*/ ) {
                return new Event(this, "changeAuth");
        }

        HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("_currentAuthType", credential.getAuthType());
        //check validateCod
        if(credential instanceof AbstractPasswordCredential ) {

            //校验码
            String captchaCode = StringBaseOpt.castObjectToString(
                httpSession.getAttribute(CaptchaImageUtil.SESSIONCHECKCODE));
            //httpSession.setAttribute(CaptchaImageUtil.SESSIONCHECKCODE,"session_checkcode_need_change");
            //校验码失败跳转到登录页
            if(StringUtils.isNotBlank(captchaCode) &&
                    !CaptchaImageUtil.checkcodeMatch(captchaCode,
                        ((AbstractPasswordCredential)credential).getValidateCode())) {

                return makeError(requestContext,"captchaError","验证码输入错误！") ;
            }
        }


        final String agent = WebUtils.getHttpServletRequestUserAgentFromRequestContext();
        final GeoLocationRequest geoLocation = WebUtils.getHttpServletRequestGeoLocationFromRequestContext();

        if (!adaptiveAuthenticationPolicy.apply(agent, geoLocation)) {
            final String msg = "Adaptive authentication policy does not allow this request for " + agent + " and " + geoLocation;
            final Map<String, Class<? extends Throwable>> map = CollectionUtils.wrap(
                    UnauthorizedAuthenticationException.class.getSimpleName(),
                    UnauthorizedAuthenticationException.class);
            final AuthenticationException error = new AuthenticationException(msg, map, new HashMap<>(0));
            return new Event(this, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE,
                    new LocalAttributeMap(CasWebflowConstants.TRANSITION_ID_ERROR, error));
        }

        final Event serviceTicketEvent = this.serviceTicketRequestWebflowEventResolver.resolveSingle(requestContext);
        if (serviceTicketEvent != null) {
            fireEventHooks(serviceTicketEvent, requestContext);
            return serviceTicketEvent;
        }

        Event finalEvent = this.initialAuthenticationAttemptWebflowEventResolver.resolveSingle(requestContext);
        if(finalEvent.getId().equals(CasWebflowConstants.TRANSITION_ID_SUCCESS )) {
            Authentication auth = AuthenticationCredentialsLocalBinder.getCurrentAuthentication();
            if (auditPolicy != null && !auditPolicy.apply(credential,auth, requestContext)) {
                finalEvent = makeError(requestContext, "autidNotPass", "IP地址和Mac地址审核不通过!");
            }
        }
        fireEventHooks(finalEvent, requestContext);
        return finalEvent;

    }


    private void fireEventHooks(final Event e, final RequestContext ctx) {
        switch(e.getId()){
            case CasWebflowConstants.TRANSITION_ID_ERROR:
                onError(ctx);
                break;
            case CasWebflowConstants.TRANSITION_ID_WARN:
                onWarn(ctx);
                break;
            case CasWebflowConstants.TRANSITION_ID_SUCCESS:
                onSuccess(ctx);
                break;
            default:
                onFailedLogin(ctx);
                break;
        }
    }
    /**
     * On warn.
     *
     * @param context the context
     */
    protected void onWarn(final RequestContext context) {
        ComplexAuthCredential credential = (ComplexAuthCredential) WebUtils.getCredential(context);
        loginLogger.logWarn(credential, ClientInfoHolder.getClientInfo());
    }

    /**
     * On success.
     *
     * @param context the context
     */
    protected void onSuccess(final RequestContext context) {
        ComplexAuthCredential credential = (ComplexAuthCredential) WebUtils.getCredential(context);
        Authentication auth = AuthenticationCredentialsLocalBinder.getCurrentAuthentication();
        loginLogger.logSuccess(credential, ClientInfoHolder.getClientInfo(), auth );
    }
    /**
     * On error.
     *
     * @param context the context
     */
    protected void onError(final RequestContext context) {
        ComplexAuthCredential credential = (ComplexAuthCredential) WebUtils.getCredential(context);
        loginLogger.logError(credential, ClientInfoHolder.getClientInfo());
    }

    protected void onFailedLogin(final RequestContext context) {
        ComplexAuthCredential credential = (ComplexAuthCredential) WebUtils.getCredential(context);
        loginLogger.logFailedLogin(credential, ClientInfoHolder.getClientInfo());

        HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        /*StringBuilder errMessage = new StringBuilder();
        for(Message message : context.getMessageContext().getAllMessages()){
            if (message.getSeverity() == Severity.ERROR) {
                errMessage.append(message.getText()).append("\t");
            }
        }
        String errorMsg = errMessage.toString();
        if(StringUtils.isBlank(errorMsg)){
            errorMsg = "认证失败，经检测您的输入信息，并注意大小写！";
        }*/
        HttpSession httpSession = request.getSession();
        //request.setAttribute("_loginErrorMessage",errorMsg);
        int failTimes = NumberBaseOpt.castObjectToInteger(
                httpSession.getAttribute("_failValidateTimes"),0);
        failTimes ++;
        if(failTimes>=this.strategyConfig.getMaxFailTimesBeforeValidateCode()) {
            httpSession.setAttribute("_needValidateCode", true);
            httpSession.setAttribute(CaptchaImageUtil.SESSIONCHECKCODE,"session_checkcode_need_change");
        }else{
            httpSession.setAttribute("_needValidateCode", false);
        }
        httpSession.setAttribute("_failValidateTimes",failTimes);

    }

    public void setAuditPolicy(AuditPolicy auditPolicy) {
        this.auditPolicy = auditPolicy;
    }

    public void setLoginLogger(LoginLogger loginLogger) {
        this.loginLogger = loginLogger;
    }

    public void setStrategyConfig(StrategyProperties strategyConfig) {
        this.strategyConfig = strategyConfig;
    }
}
