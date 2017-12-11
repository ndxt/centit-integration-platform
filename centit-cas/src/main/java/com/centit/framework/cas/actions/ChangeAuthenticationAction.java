package com.centit.framework.cas.actions;

import com.centit.framework.cas.config.StrategyProperties;
import com.centit.framework.cas.model.ComplexAuthCredential;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ChangeAuthenticationAction extends AbstractAction {
    private StrategyProperties strategyConfig;

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        ComplexAuthCredential credential = (ComplexAuthCredential)WebUtils.getCredential(context);
        if(credential!=null)
            return new Event(this, credential.getAuthType()) ;
        HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        HttpSession httpSession = request.getSession();
        String currentAuthType = StringBaseOpt.castObjectToString(
                httpSession.getAttribute("_currentAuthType"));
        if(StringUtils.isNotBlank(currentAuthType)){
            return new Event(this, currentAuthType) ;
        }
        if(StringUtils.isNotBlank(strategyConfig.getDefaultAuthType())) {
            return new Event(this, strategyConfig.getDefaultAuthType());
        }
        return new Event(this, "password");
    }


    public void setStrategyConfig(StrategyProperties strategyConfig) {
        this.strategyConfig = strategyConfig;
    }
}
