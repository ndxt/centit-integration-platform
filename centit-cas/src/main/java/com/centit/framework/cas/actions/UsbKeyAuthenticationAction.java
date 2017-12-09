package com.centit.framework.cas.actions;

import com.centit.framework.cas.model.UsbKeyCredential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.webflow.execution.RequestContext;

/**
 * 关于单点登录的配置说明可以看下面的文章
 * http://blog.csdn.net/u010475041/article/details/77886765 介绍首页
 * http://blog.csdn.net/u010475041/article/details/77943965 数据库认证
 * http://blog.csdn.net/u010475041/article/details/77972605 自定义认证
 * 官方文档 https://apereo.github.io/cas/5.2.x/index.html
 */

public class UsbKeyAuthenticationAction extends AbstractComplexAuthenticationAction {

    public UsbKeyAuthenticationAction(final CasDelegatingWebflowEventResolver delegatingWebflowEventResolver,
                                      final CasWebflowEventResolver webflowEventResolver,
                                      final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        super(delegatingWebflowEventResolver, webflowEventResolver, adaptiveAuthenticationPolicy);
        super.setSupportAuthType("usbKey");
    }

    @Override
    public boolean doPrepareExecute(RequestContext requestContext) {
        //HttpServletRequest request =WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        UsbKeyCredential credential = (UsbKeyCredential)
            requestContext.getFlowScope().get("usbKeyCredential");
        if(credential != null) {
            requestContext.getFlowScope().put("credential", credential);
            return true;
        }
        return false;
    }

}
