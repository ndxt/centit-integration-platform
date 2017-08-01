package com.centit.framework.cas;

import org.apereo.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.util.Map;

public class CentitAuthenticationHandler extends QueryDatabaseAuthenticationHandler {



    private static final Logger LOGGER = LoggerFactory.getLogger(CentitAuthenticationHandler.class);



    public CentitAuthenticationHandler(final String name, final ServicesManager servicesManager,
                                              final PrincipalFactory principalFactory,
                                              final Integer order, final DataSource dataSource, final String sql,
                                              final String fieldPassword, final String fieldExpired, final String fieldDisabled,
                                              final Map<String, String> attributes) {
        super(name, servicesManager, principalFactory, order, dataSource,
                sql,fieldPassword,fieldExpired,fieldDisabled,attributes);

       super.setPasswordEncoder(new BCryptPasswordEncoder(11));
    }
}
