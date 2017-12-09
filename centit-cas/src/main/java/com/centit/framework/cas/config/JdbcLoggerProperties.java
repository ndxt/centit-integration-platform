package com.centit.framework.cas.config;

import com.centit.support.database.utils.DataSourceDescription;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

public class JdbcLoggerProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String successSql;
    private String errorSql;
    private String warnSql;
    private String failedLoginSql;

    @NestedConfigurationProperty
    private DataSourceDescription datasource = new DataSourceDescription();

    public String getSuccessSql() {
        return successSql;
    }

    public void setSuccessSql(String successSql) {
        this.successSql = successSql;
    }

    public String getErrorSql() {
        return errorSql;
    }

    public void setErrorSql(String errorSql) {
        this.errorSql = errorSql;
    }

    public String getWarnSql() {
        return warnSql;
    }

    public void setWarnSql(String warnSql) {
        this.warnSql = warnSql;
    }

    public String getFailedLoginSql() {
        return failedLoginSql;
    }

    public void setFailedLoginSql(String failedLoginSql) {
        this.failedLoginSql = failedLoginSql;
    }

    public DataSourceDescription getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSourceDescription datasource) {
        this.datasource = datasource;
    }

}
