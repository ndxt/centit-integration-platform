package com.centit.framework.cas.actions;

import com.centit.framework.cas.model.ComplexAuthCredential;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class ChangeAuthenticationAction extends AbstractAction {

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        ComplexAuthCredential credential = (ComplexAuthCredential)WebUtils.getCredential(context);
        return new Event(this, credential.getAuthType()) ;
    }
}
