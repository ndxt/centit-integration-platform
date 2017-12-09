package com.centit.framework.cas.audit;

import com.centit.framework.cas.model.ComplexAuthCredential;
import org.apereo.cas.authentication.Authentication;
import org.apereo.inspektr.common.web.ClientInfo;

public interface LoginLogger {
    void logSuccess(ComplexAuthCredential credential, ClientInfo clientInfo, Authentication auth );
    void logError(ComplexAuthCredential credential, ClientInfo clientInfo);
    void logWarn(ComplexAuthCredential credential, ClientInfo clientInfo);
    void logFailedLogin(ComplexAuthCredential credential, ClientInfo clientInfo);
}
