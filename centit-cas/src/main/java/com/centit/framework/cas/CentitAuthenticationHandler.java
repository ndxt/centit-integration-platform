package com.centit.framework.cas;

import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.security.GeneralSecurityException;

@Component("centitAuthenticationHandler")
public class CentitAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    private static final String DEFAULT_PASSWORD_FIELD = "password";
    private static final String DEFAULT_SALT_FIELD = "salt";

    private BCryptPasswordEncoder passwordEncoder ;

    /*@Autowired(required = false)
    public CentitAuthenticationHandler(@Qualifier("queryEncodeDatabaseDataSource")
                                       final DataSource datasource,
                                       @Value("${cas.jdbc.authn.query.encode.sql:}")
                                       final String sql) {
        super("jdbc", (ServicesManager) null, (PrincipalFactory) null, 1,  datasource );

        this.sql = sql;
    }*/

    public CentitAuthenticationHandler(String name, ServicesManager servicesManager,
                                       PrincipalFactory principalFactory, Integer order, DataSource dataSource) {
        super(name, servicesManager, principalFactory, order, dataSource);
        passwordEncoder = new BCryptPasswordEncoder(11);
    }


    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(
            UsernamePasswordCredential transformedCredential, String originalPassword)
            throws GeneralSecurityException, PreventedException {
        boolean matched = passwordEncoder.matches(originalPassword,transformedCredential.getPassword());
        if(!matched)
            throw new FailedLoginException("用户名 密码不匹配");

        return new DefaultHandlerResult(this,  new BasicCredentialMetaData ( transformedCredential),
                this.principalFactory.createPrincipal(transformedCredential.getId()));
    }

  /*  @Override
    public boolean preAuthenticate(Credential credential) {
        return true;
    }

    @Override
    public HandlerResult postAuthenticate(Credential credential, HandlerResult result) {
        return result;
    }*/
}
