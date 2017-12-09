package com.centit.framework.cas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

@ConfigurationProperties(value = "complex")
public class ComplexConfigurationProperties implements Serializable {
    /**
     * Prefix used for all CAS-specific settings.
     */
    public static final String PREFIX = "complex";
    private static final long serialVersionUID = 1L;

    @NestedConfigurationProperty
    private QueryUserProperties queryUser = new QueryUserProperties();

    @NestedConfigurationProperty
    private JdbcLoggerProperties jdbcLogger = new JdbcLoggerProperties();

    public QueryUserProperties getQueryUser() {
        return queryUser;
    }

    public void setQueryUser(QueryUserProperties queryUser) {
        this.queryUser = queryUser;
    }

    public JdbcLoggerProperties getJdbcLogger() {
        return jdbcLogger;
    }

    public void setJdbcLogger(JdbcLoggerProperties jdbcLogger) {
        this.jdbcLogger = jdbcLogger;
    }
}
