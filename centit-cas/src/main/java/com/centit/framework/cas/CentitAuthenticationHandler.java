package com.centit.framework.cas;

import org.apereo.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 关于单点登录的配置说明可以看下面的文章
 * http://blog.csdn.net/u010475041/article/details/77886765 介绍首页
 * http://blog.csdn.net/u010475041/article/details/77943965 数据库认证
 * http://blog.csdn.net/u010475041/article/details/77972605 自定义认证
 * 官方文档 https://apereo.github.io/cas/5.2.x/index.html
 */
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
