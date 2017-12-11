package com.centit.framework.cas.audit;

import com.centit.framework.cas.model.ComplexAuthCredential;
import org.apereo.cas.authentication.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.RequestContext;

public class IpMacAuditPolicy implements AuditPolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpMacAuditPolicy.class);


    @Override
    public boolean apply(ComplexAuthCredential credential, Authentication auth, RequestContext requestContext) {
        return true;
    }
}
