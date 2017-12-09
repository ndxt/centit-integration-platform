package com.centit.framework.cas.audit;

import com.centit.framework.cas.model.ComplexAuthCredential;
import org.springframework.webflow.execution.RequestContext;

public interface AuditPolicy {
    boolean apply(ComplexAuthCredential credential, final RequestContext requestContext);
}
