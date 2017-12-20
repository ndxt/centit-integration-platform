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

    @NestedConfigurationProperty
    private LdapProperties ldap = new LdapProperties();

    @NestedConfigurationProperty
    private StrategyProperties strategy = new StrategyProperties();

    public LdapProperties getLdap() {
        return ldap;
    }

    public void setLdap(LdapProperties ldapProperties) {
        this.ldap = ldapProperties;
    }

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

    public StrategyProperties getStrategy() {
        return strategy;
    }

    public void setStrategy(StrategyProperties strategy) {
        this.strategy = strategy;
    }
}
